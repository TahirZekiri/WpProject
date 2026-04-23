package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.CustomEntity;

import java.util.List;

public interface CustomEntityService {
    List<CustomEntity> findAll();
    CustomEntity create(String name);
    void deleteById(Long id);
}
