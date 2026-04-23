package mk.ukim.finki.wpproject.repository;

import mk.ukim.finki.wpproject.model.TextEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextEntryRepository extends JpaRepository<TextEntry, Long> {
    List<TextEntry> findAllByOrderByCreatedAtDesc();
}
