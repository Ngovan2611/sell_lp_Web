package com.example.sell_lp.repository;


import com.example.sell_lp.entity.Address;
import com.example.sell_lp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAddressByUser(User user);
}
