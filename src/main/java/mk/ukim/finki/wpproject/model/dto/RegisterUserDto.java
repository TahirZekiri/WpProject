package mk.ukim.finki.wpproject.model.dto;

import mk.ukim.finki.wpproject.model.enums.Role;

public record RegisterUserDto(
        String username,
        String password,
        String confirmPassword,
        String name,
        String surname,
        Role role
) {
}
