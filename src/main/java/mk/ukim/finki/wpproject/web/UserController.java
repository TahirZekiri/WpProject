package mk.ukim.finki.wpproject.web;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.UserFilterDto;
import mk.ukim.finki.wpproject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public String getUserPage(
            @ModelAttribute UserFilterDto userFilterDto,
            Model model
    ) {
        Page<User> users = userService.findAllByPage(userFilterDto);
        model.addAttribute("page", users);
        model.addAttribute("text", userFilterDto.getText());
        model.addAttribute("sortBy", userFilterDto.getSortBy());
        return "users";
    }

    @PostMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username) {
        userService.deleteByUsername(username);
        return "redirect:/users";
    }
}
