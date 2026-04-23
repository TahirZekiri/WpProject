package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.CustomLabel;

import java.util.List;

public interface CustomLabelService {
    List<CustomLabel> findAll();
    CustomLabel create(String name);
    void deleteById(Long id);
}
