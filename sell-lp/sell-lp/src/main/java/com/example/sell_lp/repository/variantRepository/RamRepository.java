package com.example.sell_lp.repository.variantRepository;

import com.example.sell_lp.entity.Ram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RamRepository extends JpaRepository<Ram, Integer> {
}
