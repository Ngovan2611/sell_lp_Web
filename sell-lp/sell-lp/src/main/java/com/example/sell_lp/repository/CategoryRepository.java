package com.example.sell_lp.repository;

import com.example.sell_lp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByCategoryId(int id);
}
