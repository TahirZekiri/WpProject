package mk.ukim.finki.wpproject.service.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.RegisterUserDto;
import mk.ukim.finki.wpproject.model.dto.UserFilterDto;
import mk.ukim.finki.wpproject.model.exceptions.*;
import mk.ukim.finki.wpproject.repository.UserRepository;
import mk.ukim.finki.wpproject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static mk.ukim.finki.wpproject.service.FieldFilterSpecification.filterContainsText;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public User login(String username, String password) {
        notNullAndNotEmpty(username, password);
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
    @Transactional
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public Page<User> findAllByPage(UserFilterDto filterDto) {
        filterDto.normalize();

        String text = filterDto.getText() == null
                ? ""
                : filterDto.getText();

        Specification<User> specification;

        if (text.isBlank()) {
            specification = Specification.unrestricted();
        } else {
            specification = Specification.anyOf(
                    filterContainsText(User.class, "name", text),
                    filterContainsText(User.class, "surname", text),
                    filterContainsText(User.class, "username", text)
            );
        }

        return this.userRepository.findAll(
                specification,
                PageRequest.of(
                        filterDto.getPageNum() - 1,
                        filterDto.getPageSize(),
                        Sort.by(Sort.Direction.ASC, filterDto.getSortBy())
                )
        );
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    /// Helpers
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

        existsWithUsername(registerUserDto.username());
    }

    private void notNullAndNotEmpty(String... values) {
        for (String name : values) {
            if (name == null || name.isEmpty()) {
                throw new InvalidRequestException();
            }
        }
    }

    private void existsWithUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
    }

}
