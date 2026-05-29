package com.example.sell_lp.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class New {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    String title;

    @Column(nullable = false, length = 500)
    String thumbnail;

    @Column(name = "short_description", nullable = false, columnDefinition = "TEXT")
    String shortDescription;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    String content;

    @Column(nullable = false)
    boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;
}