package mk.ukim.finki.wpproject.repository;

import mk.ukim.finki.wpproject.model.CustomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomEntityRepository extends JpaRepository<CustomEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
}
