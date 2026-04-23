package mk.ukim.finki.wpproject.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wpproject.model.CustomLabel;
import mk.ukim.finki.wpproject.repository.CustomLabelRepository;
import mk.ukim.finki.wpproject.service.CustomLabelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomLabelServiceImpl implements CustomLabelService {

    private final CustomLabelRepository customLabelRepository;

    @Override
    public List<CustomLabel> findAll() {
        return customLabelRepository.findAll();
    }

    @Override
    public CustomLabel create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Label name cannot be empty");
        }
        return customLabelRepository.save(new CustomLabel(null, name.trim()));
    }

    @Override
    public void deleteById(Long id) {
        customLabelRepository.deleteById(id);
    }
}
