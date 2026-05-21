package mk.ukim.finki.wpproject.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.service.CustomEntityService;
import mk.ukim.finki.wpproject.service.CustomLabelService;
import mk.ukim.finki.wpproject.service.TextEntryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final TextEntryService textEntryService;
    private final CustomLabelService customLabelService;
    private final CustomEntityService customEntityService;

    @GetMapping
    public String home(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("recentEntries", textEntryService.findRecent(user,5));
        model.addAttribute("textTypes", TextType.values());
        model.addAttribute("textTones", TextTone.values());
        model.addAttribute("customLabels", customLabelService.findAll());
        model.addAttribute("customEntities", customEntityService.findAll());
        return "home";
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

}
