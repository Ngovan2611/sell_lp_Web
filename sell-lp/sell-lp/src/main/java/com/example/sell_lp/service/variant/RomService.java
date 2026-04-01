package com.example.sell_lp.service.variant;


import com.example.sell_lp.entity.Rom;
import com.example.sell_lp.repository.variantRepository.RomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RomService {

    RomRepository romRepository;

    public List<Rom> getAllRom() {
        return romRepository.findAll();
    }
}
