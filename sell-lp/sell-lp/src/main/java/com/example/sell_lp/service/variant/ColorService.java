package com.example.sell_lp.service.variant;


import com.example.sell_lp.entity.Color;
import com.example.sell_lp.repository.variantRepository.ColorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ColorService {

    ColorRepository colorRepository;

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }
}
