package mk.ukim.finki.wpproject.repository;

import mk.ukim.finki.wpproject.model.TextEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TextEntryRepository extends JpaSpecificationRepository<TextEntry, Long> {
    List<TextEntry> findAllByOrderByCreatedAtDesc();

    List<TextEntry> findAllByUser_UsernameOrderByCreatedAtAsc(String userUsername);

    List<TextEntry> findAllByUser_Username(String username);

    List<TextEntry> findByCreatedAtGreaterThanEqual(LocalDateTime createdAt);

    @Query(value = "SELECT COUNT(DISTINCT l.id) FROM text_entries te " +
            "LEFT JOIN text_entry_labels tel ON te.id = tel.entry_id " +
            "LEFT JOIN custom_labels l ON tel.label_id = l.id " +
            "WHERE te.user_id = :username", nativeQuery = true)
    Integer countDistinctLabelsByUsername(String username);

    @Query(value = "SELECT COUNT(DISTINCT e.id) FROM text_entries te " +
            "LEFT JOIN text_entry_entities tee ON te.id = tee.entry_id " +
            "LEFT JOIN custom_entities e ON tee.entity_id = e.id " +
            "WHERE te.user_id = :username", nativeQuery = true)
    Integer countDistinctEntitiesByUsername(String username);

    @Query(value = "SELECT te.text_type as name, COUNT(*) as count FROM text_entries te " +
            "GROUP BY te.text_type ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countByTextType();

    @Query(value = "SELECT te.text_tone as name, COUNT(*) as count FROM text_entries te " +
            "GROUP BY te.text_tone ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countByTextTone();

    @Query(value = "SELECT te.text_type as name, COUNT(*) as count FROM text_entries te " +
            "WHERE te.user_id = :username " +
            "GROUP BY te.text_type ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countByTextTypeForUser(String username);

    @Query(value = "SELECT te.text_tone as name, COUNT(*) as count FROM text_entries te " +
            "WHERE te.user_id = :username " +
            "GROUP BY te.text_tone ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countByTextToneForUser(String username);

    @Query(value = "SELECT MAX(te.created_at) FROM text_entries te " +
            "WHERE te.user_id = :username", nativeQuery = true)
    Object getLastActivityDateByUsername(String username);
}
