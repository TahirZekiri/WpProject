package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.TextEntry;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.TextEntryFilterDto;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TextEntryService {
    List<TextEntry> findAll();

    List<TextEntry> findRecent(User user, int limit);

    Optional<TextEntry> findById(Long id);

    TextEntry create(User user, String content, TextType textType, TextTone textTone, List<Long> labelIds, List<Long> entityIds);

    TextEntry update(Long id, String content, TextType textType, TextTone textTone, List<Long> labelIds, List<Long> entityIds);

    int importEntries(User user, MultipartFile file) throws IOException;

    void deleteById(Long id);

    Page<TextEntry> findAllPaged(User user, TextEntryFilterDto filterDto);
}
