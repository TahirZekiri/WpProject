package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.RegisterUserDto;
import mk.ukim.finki.wpproject.model.dto.UserFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> findAll();

    User findByUsername(String username);

    User login(String username, String password);

    User register(RegisterUserDto registerUserDto);

    void deleteByUsername(String username);

    Page<User> findAllByPage(UserFilterDto filterDto);
}
