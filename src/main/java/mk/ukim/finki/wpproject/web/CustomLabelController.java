package mk.ukim.finki.wpproject.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.CustomLabel;
import mk.ukim.finki.wpproject.service.CustomLabelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/labels")
@RequiredArgsConstructor
public class CustomLabelController {

    private final CustomLabelService customLabelService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<CustomLabel> addLabel(@RequestParam String name) {
        CustomLabel label = customLabelService.create(name);
        return ResponseEntity.ok(label);
    }

    @PostMapping("/delete")
    @ResponseBody
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteLabel(@RequestParam Long id) {
        customLabelService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
