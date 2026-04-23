package mk.ukim.finki.wpproject.repository;

import mk.ukim.finki.wpproject.model.CustomLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomLabelRepository extends JpaRepository<CustomLabel, Long> {
    boolean existsByNameIgnoreCase(String name);
}
