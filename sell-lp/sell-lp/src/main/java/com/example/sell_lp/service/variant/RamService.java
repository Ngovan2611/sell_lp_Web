package com.example.sell_lp.service.variant;

import com.example.sell_lp.entity.Ram;
import com.example.sell_lp.repository.variantRepository.RamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RamService {
    @Autowired
    private RamRepository ramRepository;

    public List<Ram> getAllRam() {
        return ramRepository.findAll();
    }
}
