package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.TextEntry;
import mk.ukim.finki.wpproject.model.enums.TextFormat;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;

import java.util.List;
import java.util.Optional;

public interface TextEntryService {
    List<TextEntry> findAll();
    List<TextEntry> findRecent(int limit);
    Optional<TextEntry> findById(Long id);
    TextEntry create(String content, TextType textType, TextTone textTone, TextFormat textFormat);
    void deleteById(Long id);
}
