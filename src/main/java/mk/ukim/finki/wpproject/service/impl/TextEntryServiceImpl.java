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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
}
