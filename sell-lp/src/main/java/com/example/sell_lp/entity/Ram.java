package com.example.sell_lp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ram {
    @Id
    int ramId;
    int ramSize;
}
