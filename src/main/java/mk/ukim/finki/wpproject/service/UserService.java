package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.RegisterUserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User login(String username, String password);

    User register(RegisterUserDto registerUserDto);
}
