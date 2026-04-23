package mk.ukim.finki.wpproject.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.CustomEntity;
import mk.ukim.finki.wpproject.repository.CustomEntityRepository;
import mk.ukim.finki.wpproject.service.CustomEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomEntityServiceImpl implements CustomEntityService {

    private final CustomEntityRepository customEntityRepository;

    @Override
    public List<CustomEntity> findAll() {
        return customEntityRepository.findAll();
    }

    @Override
    public CustomEntity create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Entity name cannot be empty");
        }
        return customEntityRepository.save(new CustomEntity(null, name.trim()));
    }

    @Override
    public void deleteById(Long id) {
        customEntityRepository.deleteById(id);
    }
}
