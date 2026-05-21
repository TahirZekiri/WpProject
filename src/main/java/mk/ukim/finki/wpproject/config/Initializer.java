package mk.ukim.finki.wpproject.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wpproject.model.TextEntry;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.enums.Role;
import mk.ukim.finki.wpproject.model.enums.TextTone;
import mk.ukim.finki.wpproject.model.enums.TextType;
import mk.ukim.finki.wpproject.repository.TextEntryRepository;
import mk.ukim.finki.wpproject.repository.UserRepository;;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class Initializer {
    private final UserRepository userRepository;
    private final TextEntryRepository textEntryRepository;
    private final PasswordEncoder passwordEncoder;


    @PostConstruct
    public void init() {
        addUsers();
        addTextEntries();
    }

    public void addUsers() {
        if (userRepository.count() == 0) {

            User admin = new User(
                    "admin",
                    "Admin",
                    "Admin",
                    passwordEncoder.encode("admin"),
                    Role.ROLE_ADMINISTRATOR
            );

            User user1 = new User(
                    "user1",
                    "User 1",
                    "User 1",
                    passwordEncoder.encode("user1"),
                    Role.ROLE_USER
            );

            User user2 = new User(
                    "user2",
                    "User 2",
                    "User 2",
                    passwordEncoder.encode("user2"),
                    Role.ROLE_USER
            );


            userRepository.saveAll(List.of(admin, user1, user2));
        }
    }

    public void addTextEntries() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new RuntimeException("Cannot create text entries because there are no users.");
        }

        List<String> contents = List.of(
                "The service was very helpful and fast.",
                "I am not satisfied with the response.",
                "The application works correctly.",
                "This text needs more analysis.",
                "The user experience is excellent.",
                "The result was confusing.",
                "Everything was completed successfully.",
                "The message sounds very negative.",
                "This is a normal neutral text.",
                "The system detected important information."
        );

        TextType[] textTypes = TextType.values();
        TextTone[] textTones = TextTone.values();

        Random random = new Random();

        List<TextEntry> entries = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            TextEntry entry = new TextEntry();

            User randomUser = users.get(random.nextInt(users.size()));
            String randomContent = contents.get(random.nextInt(contents.size()));
            TextType randomType = textTypes[random.nextInt(textTypes.length)];
            TextTone randomTone = textTones[random.nextInt(textTones.length)];

            entry.setUser(randomUser);
            entry.setContent(randomContent);
            entry.setTextType(randomType);
            entry.setTextTone(randomTone);
            entry.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));

            entries.add(entry);
        }

        textEntryRepository.saveAll(entries);
    }
}
