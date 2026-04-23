package mk.ukim.finki.wpproject.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.CustomEntity;
import mk.ukim.finki.wpproject.model.CustomLabel;
import mk.ukim.finki.wpproject.model.TextEntry;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.model.exceptions.InvalidTextEntryException;
import mk.ukim.finki.wpproject.model.exceptions.TextEntryNotFoundException;
import mk.ukim.finki.wpproject.repository.CustomEntityRepository;
import mk.ukim.finki.wpproject.repository.CustomLabelRepository;
import mk.ukim.finki.wpproject.repository.TextEntryRepository;
import mk.ukim.finki.wpproject.service.TextEntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<TextEntry> findRecent(int limit) {
        return textEntryRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TextEntry> findById(Long id) {
        return textEntryRepository.findById(id);
    }

    @Override
    public TextEntry create(String content, TextType textType, TextTone textTone, List<Long> labelIds, List<Long> entityIds) {
        if (content == null || content.isBlank()) {
            throw new InvalidTextEntryException("Content cannot be empty");
        }
        TextEntry entry = new TextEntry();
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
}
