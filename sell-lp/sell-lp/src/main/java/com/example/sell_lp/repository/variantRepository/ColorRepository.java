package com.example.sell_lp.repository.variantRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sell_lp.entity.Color;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
}
