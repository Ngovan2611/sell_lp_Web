package com.example.sell_lp.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    Long tagId;

    @Column(nullable = false, unique = true, length = 100)
    String name;

    @Column(nullable = false, unique = true, length = 100)
    String slug;

    // Tại Tag.java
    @ManyToMany(mappedBy = "tags") // Chỉ định map ngược lại thuộc tính "tags" bên phía Product
    @JsonIgnore                     // Ngăn vòng lặp vô hạn khi Jackson parse sang JSON
    private Set<Product> products; // XÓA HẲN HOẶC KHÔNG ĐỂ @JoinTable Ở ĐÂY NỮA
}