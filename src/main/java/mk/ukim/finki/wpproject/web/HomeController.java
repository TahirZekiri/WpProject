package mk.ukim.finki.wpproject.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.enums.TextFormat;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.service.TextEntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final TextEntryService textEntryService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("recentEntries", textEntryService.findRecent(5));
        model.addAttribute("textTypes", TextType.values());
        model.addAttribute("textTones", TextTone.values());
        model.addAttribute("textFormats", TextFormat.values());
        return "home";
    }
}
