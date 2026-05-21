package mk.ukim.finki.wpproject.repository;

import mk.ukim.finki.wpproject.model.TextEntry;
import mk.ukim.finki.wpproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextEntryRepository extends JpaSpecificationRepository<TextEntry, Long> {
    List<TextEntry> findAllByOrderByCreatedAtDesc();

    List<TextEntry> findAllByUser_UsernameOrderByCreatedAtAsc(String userUsername);

    List<TextEntry> findAllByUser_Username(String username);
}
