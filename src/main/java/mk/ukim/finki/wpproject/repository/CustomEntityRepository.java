package mk.ukim.finki.wpproject.repository;

import mk.ukim.finki.wpproject.model.CustomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomEntityRepository extends JpaRepository<CustomEntity, Long> {
    boolean existsByNameIgnoreCase(String name);

    @Query(value = "SELECT e.name, COUNT(*) as usage_count FROM custom_entities e " +
            "LEFT JOIN text_entry_entities tee ON e.id = tee.entity_id " +
            "GROUP BY e.id, e.name ORDER BY usage_count DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopEntities();
}
