package mk.ukim.finki.wpproject.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.CustomEntity;
import mk.ukim.finki.wpproject.service.CustomEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/entities")
@RequiredArgsConstructor
public class CustomEntityController {

    private final CustomEntityService customEntityService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<CustomEntity> addEntity(@RequestParam String name) {
        CustomEntity entity = customEntityService.create(name);
        return ResponseEntity.ok(entity);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Void> deleteEntity(@RequestParam Long id) {
        customEntityService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
