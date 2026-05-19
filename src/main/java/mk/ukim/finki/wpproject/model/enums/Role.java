package mk.ukim.finki.wpproject.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum Role implements GrantedAuthority {
    ROLE_USER("User"),
    ROLE_ADMINISTRATOR("Administrator");

    private final String displayName;

    @Override
    public String getAuthority() {
        return name();
    }
}