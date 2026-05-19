package mk.ukim.finki.wpproject.service.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.RegisterUserDto;
import mk.ukim.finki.wpproject.model.exceptions.InvalidCredentialsException;
import mk.ukim.finki.wpproject.model.exceptions.InvalidRequestException;
import mk.ukim.finki.wpproject.model.exceptions.PasswordConfirmationMismatchException;
import mk.ukim.finki.wpproject.model.exceptions.UsernameAlreadyExistsException;
import mk.ukim.finki.wpproject.repository.UserRepository;
import mk.ukim.finki.wpproject.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User login(String username, String password) {
        notNullAndNotEmpty(username,password);
        return this.userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(InvalidCredentialsException::new);
    }

    @Override
    public User register(RegisterUserDto registerUserDto) {
        validateRegisterArgs(registerUserDto);
        User user = new User(
                registerUserDto.username(),
                registerUserDto.name(),
                registerUserDto.surname(),
                passwordEncoder.encode(registerUserDto.password()),
                registerUserDto.role()
        );

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private void validateRegisterArgs(RegisterUserDto registerUserDto) {
        notNullAndNotEmpty(
                registerUserDto.username(),
                registerUserDto.name(),
                registerUserDto.surname(),
                registerUserDto.password(),
                registerUserDto.confirmPassword()
        );


        if (!registerUserDto.password().equals(registerUserDto.confirmPassword())) {
            throw new PasswordConfirmationMismatchException();
        }

        if (userRepository.existsByUsername(registerUserDto.username())) {
            throw new UsernameAlreadyExistsException(registerUserDto.username());
        }
    }

    private void notNullAndNotEmpty(String... values) {
        for (String name : values) {
            if (name == null || name.isEmpty()) {
                throw new InvalidRequestException();
            }
        }
    }
}
