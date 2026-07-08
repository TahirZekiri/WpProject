package mk.ukim.finki.wpproject.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.CustomEntity;
import mk.ukim.finki.wpproject.model.CustomLabel;
import mk.ukim.finki.wpproject.model.TextEntry;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.TextEntryFilterDto;
import mk.ukim.finki.wpproject.model.enums.Role;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.model.exceptions.InvalidTextEntryException;
import mk.ukim.finki.wpproject.model.exceptions.TextEntryNotFoundException;
import mk.ukim.finki.wpproject.repository.CustomEntityRepository;
import mk.ukim.finki.wpproject.repository.CustomLabelRepository;
import mk.ukim.finki.wpproject.repository.TextEntryRepository;
import mk.ukim.finki.wpproject.service.TextEntryService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static mk.ukim.finki.wpproject.service.FieldFilterSpecification.filterEquals;
import static mk.ukim.finki.wpproject.service.FieldFilterSpecification.filterEqualsV;

@Service
@RequiredArgsConstructor
public class TextEntryServiceImpl implements TextEntryService {

    private final TextEntryRepository textEntryRepository;
    private final CustomLabelRepository customLabelRepository;
    private final CustomEntityRepository customEntityRepository;

    @Override
    public List<TextEntry> findAll() {
        return textEntryRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<TextEntry> findRecent(User user, int limit) {
        List<TextEntry> entries;

        if (user.getRole() == Role.ROLE_ADMINISTRATOR) {
            entries = textEntryRepository.findAllByOrderByCreatedAtDesc();
        } else {
            entries = textEntryRepository.findAllByUser_UsernameOrderByCreatedAtAsc(user.getUsername());
        }
        return entries
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TextEntry> findById(Long id) {
        return textEntryRepository.findById(id);
    }

    @Override
    public TextEntry create(User user, String content, TextType textType, TextTone textTone, List<Long> labelIds, List<Long> entityIds) {
        if (content == null || content.isBlank()) {
            throw new InvalidTextEntryException("Content cannot be empty");
        }
        TextEntry entry = new TextEntry();
        entry.setUser(user);
        entry.setContent(content);
        entry.setTextType(textType);
        entry.setTextTone(textTone);
        entry.setCreatedAt(LocalDateTime.now());
        if (labelIds != null && !labelIds.isEmpty()) {
            entry.setLabels(customLabelRepository.findAllById(labelIds));
        }
        if (entityIds != null && !entityIds.isEmpty()) {
            entry.setEntities(customEntityRepository.findAllById(entityIds));
        }
        return textEntryRepository.save(entry);
    }

    @Override
    public TextEntry update(Long id, String content, TextType textType, TextTone textTone, List<Long> labelIds, List<Long> entityIds) {
        TextEntry entry = textEntryRepository.findById(id)
                .orElseThrow(() -> new TextEntryNotFoundException(id));
        if (content == null || content.isBlank()) {
            throw new InvalidTextEntryException("Content cannot be empty");
        }
        entry.setContent(content);
        entry.setTextType(textType);
        entry.setTextTone(textTone);
        entry.setLabels(labelIds != null && !labelIds.isEmpty()
                ? customLabelRepository.findAllById(labelIds)
                : new java.util.ArrayList<>());
        entry.setEntities(entityIds != null && !entityIds.isEmpty()
                ? customEntityRepository.findAllById(entityIds)
                : new java.util.ArrayList<>());
        return textEntryRepository.save(entry);
    }

    @Override
    public int importEntries(User user, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidTextEntryException("CSV file cannot be empty");
        }

        Map<String, CustomLabel> labelsByName = customLabelRepository.findAll()
                .stream()
                .collect(Collectors.toMap(label -> normalizeName(label.getName()), Function.identity(),
                        (first, ignored) -> first));
        Map<String, CustomEntity> entitiesByName = customEntityRepository.findAll()
                .stream()
                .collect(Collectors.toMap(entity -> normalizeName(entity.getName()), Function.identity(),
                        (first, ignored) -> first));

        List<TextEntry> entries = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreSurroundingSpaces(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                String content = record.get("content");
                if (content == null || content.isBlank()) {
                    continue;
                }

                TextEntry entry = new TextEntry();
                entry.setUser(user);
                entry.setContent(content);
                entry.setTextType(parseTextType(record.get("textType")));
                entry.setTextTone(parseTextTone(record.get("textTone")));
                entry.setCreatedAt(LocalDateTime.now());
                entry.setLabels(resolveLabels(getOptionalField(record, "labels"), labelsByName));
                entry.setEntities(resolveEntities(getOptionalField(record, "entities"), entitiesByName));
                entries.add(entry);
            }
        }

        return textEntryRepository.saveAll(entries).size();
    }

    @Override
    public void deleteById(Long id) {
        if (!textEntryRepository.existsById(id)) {
            throw new TextEntryNotFoundException(id);
        }
        textEntryRepository.deleteById(id);
    }

    @Override
    public Page<TextEntry> findAllPaged(User user, TextEntryFilterDto filterDto) {
        filterDto.normalize();
        List<Specification<TextEntry>> filters = new ArrayList<>();

        filters.add(filterEquals(TextEntry.class, "id", filterDto.getId()));
        filters.add(filterEqualsV(TextEntry.class, "textType", filterDto.getTextType()));
        filters.add(filterEqualsV(TextEntry.class, "textTone", filterDto.getTextTone()));

        if (!user.getRole().equals(Role.ROLE_ADMINISTRATOR)) {
            filters.add(filterEquals(TextEntry.class, "user.username", user.getUsername()));
        } else if (filterDto.getUsername() != null && !filterDto.getUsername().isBlank()) {
            filters.add(filterEquals(TextEntry.class, "user.username", filterDto.getUsername()));
        }


        Specification<TextEntry> specification = filters.stream()
                .filter(Objects::nonNull)
                .reduce(Specification.unrestricted(), Specification::and);

        Pageable pageable = PageRequest.of(
                filterDto.getPageNum() - 1,
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.ASC, filterDto.getSortBy())
        );

        return textEntryRepository.findAll(specification, pageable);
    }

    private TextType parseTextType(String value) {
        return Arrays.stream(TextType.values())
                .filter(type -> type.name().equalsIgnoreCase(value)
                        || type.getDisplayName().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidTextEntryException("Invalid text type: " + value));
    }

    private TextTone parseTextTone(String value) {
        return Arrays.stream(TextTone.values())
                .filter(tone -> tone.name().equalsIgnoreCase(value)
                        || tone.getDisplayName().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidTextEntryException("Invalid text tone: " + value));
    }

    private List<CustomLabel> resolveLabels(String value, Map<String, CustomLabel> labelsByName) {
        return splitNames(value)
                .stream()
                .map(name -> labelsByName.computeIfAbsent(normalizeName(name),
                        key -> customLabelRepository.save(new CustomLabel(null, name.trim()))))
                .toList();
    }

    private List<CustomEntity> resolveEntities(String value, Map<String, CustomEntity> entitiesByName) {
        return splitNames(value)
                .stream()
                .map(name -> entitiesByName.computeIfAbsent(normalizeName(name),
                        key -> customEntityRepository.save(new CustomEntity(null, name.trim()))))
                .toList();
    }

    private List<String> splitNames(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(";"))
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .toList();
    }

    private String normalizeName(String value) {
        return value.trim().toLowerCase();
    }

    private String getOptionalField(CSVRecord record, String name) {
        return record.isMapped(name) ? record.get(name) : null;
    }
}
