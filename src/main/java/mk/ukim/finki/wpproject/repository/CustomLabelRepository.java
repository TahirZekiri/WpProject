package mk.ukim.finki.wpproject.repository;

import mk.ukim.finki.wpproject.model.CustomLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomLabelRepository extends JpaRepository<CustomLabel, Long> {
    boolean existsByNameIgnoreCase(String name);

    @Query(value = "SELECT l.name, COUNT(*) as usage_count FROM custom_labels l " +
            "LEFT JOIN text_entry_labels tel ON l.id = tel.label_id " +
            "GROUP BY l.id, l.name ORDER BY usage_count DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopLabels();
}
