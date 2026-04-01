package com.example.sell_lp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ram {
    @Id
    int ramId;
    int ramSize;
}
