package mk.ukim.finki.wpproject.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.TextEntry;
import mk.ukim.finki.wpproject.model.enums.TextFormat;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.model.exceptions.InvalidTextEntryException;
import mk.ukim.finki.wpproject.model.exceptions.TextEntryNotFoundException;
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
    public TextEntry create(String content, TextType textType, TextTone textTone, TextFormat textFormat) {
        if (content == null || content.isBlank()) {
            throw new InvalidTextEntryException("Content cannot be empty");
        }
        TextEntry entry = new TextEntry(null, content, textType, textTone, textFormat, LocalDateTime.now());
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
