package com.example.sell_lp.dto.response.news;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewResponse {
    Integer id;
    String title;

    String thumbnail;

    String shortDescription;

    String content;

    boolean active;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
