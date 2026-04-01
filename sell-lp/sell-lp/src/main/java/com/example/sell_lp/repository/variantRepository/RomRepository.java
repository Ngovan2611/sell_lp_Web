package com.example.sell_lp.repository.variantRepository;

import com.example.sell_lp.entity.Rom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RomRepository extends JpaRepository<Rom, Integer> {
}
