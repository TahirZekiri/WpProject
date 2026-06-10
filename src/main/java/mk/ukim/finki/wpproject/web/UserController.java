package mk.ukim.finki.wpproject.web;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.UserFilterDto;
import mk.ukim.finki.wpproject.service.StatisticsService;
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
    private final StatisticsService statisticsService;

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

    @GetMapping("/{username}")
    public String getUserProfile(@PathVariable String username, Model model) {
        model.addAttribute("profileUser", userService.findByUsername(username));
        model.addAttribute("activity", statisticsService.getUserActivityByUsername(username));
        return "user-profile";
    }

    @PostMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username) {
        userService.deleteByUsername(username);
        return "redirect:/users";
    }
}
