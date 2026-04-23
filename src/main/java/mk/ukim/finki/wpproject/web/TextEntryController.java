package mk.ukim.finki.wpproject.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.enums.TextFormat;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.service.TextEntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/entries")
@RequiredArgsConstructor
public class TextEntryController {

    private final TextEntryService textEntryService;

    @GetMapping
    public String listEntries(Model model) {
        model.addAttribute("entries", textEntryService.findAll());
        return "entries";
    }

    @PostMapping("/add")
    public String addEntry(@RequestParam String content,
                           @RequestParam TextType textType,
                           @RequestParam TextTone textTone,
                           @RequestParam TextFormat textFormat) {
        textEntryService.create(content, textType, textTone, textFormat);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id) {
        textEntryService.deleteById(id);
        return "redirect:/entries";
    }
}
