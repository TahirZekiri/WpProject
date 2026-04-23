package mk.ukim.finki.wpproject.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.service.CustomEntityService;
import mk.ukim.finki.wpproject.service.CustomLabelService;
import mk.ukim.finki.wpproject.service.TextEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/entries")
@RequiredArgsConstructor
public class TextEntryController {

    private final TextEntryService textEntryService;
    private final CustomLabelService customLabelService;
    private final CustomEntityService customEntityService;

    @GetMapping
    public String listEntries(Model model) {
        model.addAttribute("entries", textEntryService.findAll());
        return "entries";
    }

    @GetMapping("/{id}")
    public String entryDetail(@PathVariable Long id, Model model) {
        model.addAttribute("entry", textEntryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "entry-detail";
    }

    @GetMapping("/{id}/edit")
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
    public String addEntry(@RequestParam String content,
                           @RequestParam TextType textType,
                           @RequestParam TextTone textTone,
                           @RequestParam(required = false) List<Long> labelIds,
                           @RequestParam(required = false) List<Long> entityIds) {
        textEntryService.create(content, textType, textTone, labelIds, entityIds);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id) {
        textEntryService.deleteById(id);
        return "redirect:/entries";
    }
}
