package mk.ukim.finki.wpproject.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.TextEntryFilterDto;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.service.CustomEntityService;
import mk.ukim.finki.wpproject.service.CustomLabelService;
import mk.ukim.finki.wpproject.service.TextEntryService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/entries")
@RequiredArgsConstructor
public class TextEntryController {

    private final TextEntryService textEntryService;
    private final CustomLabelService customLabelService;
    private final CustomEntityService customEntityService;

    @GetMapping
    public String getEntriesPage(@AuthenticationPrincipal User user,
                                 @ModelAttribute TextEntryFilterDto filterDto,
                                 Model model) {

        model.addAttribute("page", textEntryService.findAllPaged(user, filterDto));
        model.addAttribute("tones", TextTone.values());
        model.addAttribute("types", TextType.values());
        model.addAttribute("filterDto", filterDto);
        return "entries";
    }

    @GetMapping("/{id}")
    public String entryDetail(@PathVariable Long id, Model model) {
        model.addAttribute("entry", textEntryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "entry-detail";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("entry", textEntryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        model.addAttribute("textTypes", TextType.values());
        model.addAttribute("textTones", TextTone.values());
        model.addAttribute("customLabels", customLabelService.findAll());
        model.addAttribute("customEntities", customEntityService.findAll());
        return "entry-edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String editEntry(@PathVariable Long id,
                            @RequestParam String content,
                            @RequestParam TextType textType,
                            @RequestParam TextTone textTone,
                            @RequestParam(required = false) List<Long> labelIds,
                            @RequestParam(required = false) List<Long> entityIds) {
        textEntryService.update(id, content, textType, textTone, labelIds, entityIds);
        return "redirect:/entries/" + id;
    }

    @PostMapping("/add")
    public String addEntry(@AuthenticationPrincipal User user,
                           @RequestParam String content,
                           @RequestParam TextType textType,
                           @RequestParam TextTone textTone,
                           @RequestParam(required = false) List<Long> labelIds,
                           @RequestParam(required = false) List<Long> entityIds) {
        textEntryService.create(user, content, textType, textTone, labelIds, entityIds);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteEntry(@PathVariable Long id) {
        textEntryService.deleteById(id);
        return "redirect:/entries";
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String importEntries(@AuthenticationPrincipal User user,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) throws IOException {
        int importedCount = textEntryService.importEntries(user, file);
        redirectAttributes.addFlashAttribute("successMessage",
                importedCount + " entries imported successfully.");
        return "redirect:/entries";
    }

    @GetMapping("/import/template")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<byte[]> downloadImportTemplate() {
        String template = """
                content,textType,textTone,labels,entities
                "Please send the report by Friday.",REQUEST,FORMAL,"Work;Deadline","Report"
                "Great job on the presentation!",FEEDBACK,FRIENDLY,"Praise","Presentation;Team"
                "Can we move our meeting to 14:00?",QUESTION,CASUAL,"Schedule","Meeting"
                """;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("text-entry-import-template.csv")
                                .build()
                                .toString())
                .body(template.getBytes(StandardCharsets.UTF_8));
    }
}
