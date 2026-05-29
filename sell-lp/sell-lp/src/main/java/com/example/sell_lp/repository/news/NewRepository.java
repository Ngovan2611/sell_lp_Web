package com.example.sell_lp.repository.news;


import com.example.sell_lp.entity.New;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewRepository extends JpaRepository<New, Integer> {

    
}
