package mk.ukim.finki.wpproject.web;


import lombok.AllArgsConstructor;
import mk.ukim.finki.wpproject.model.dto.RegisterUserDto;
import mk.ukim.finki.wpproject.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
@AllArgsConstructor
public class RegisterController {
    private final UserService userService;

    @GetMapping
    public String getRegisterPage(Model model) {
        return "register";
    }

    @PostMapping
    public String register(@ModelAttribute RegisterUserDto registerUserDto, Model model) {

        try {
            this.userService.register(registerUserDto);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", registerUserDto.name());
            model.addAttribute("surname", registerUserDto.surname());
            model.addAttribute("username", registerUserDto.username());
            return getRegisterPage(model);
        }

    }

}