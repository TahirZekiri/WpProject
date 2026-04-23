package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.TextEntry;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;

import java.util.List;
import java.util.Optional;

public interface TextEntryService {
    List<TextEntry> findAll();
    List<TextEntry> findRecent(int limit);
    Optional<TextEntry> findById(Long id);
    TextEntry create(String content, TextType textType, TextTone textTone, List<Long> labelIds, List<Long> entityIds);
    TextEntry update(Long id, String content, TextType textType, TextTone textTone, List<Long> labelIds, List<Long> entityIds);
    void deleteById(Long id);
}
