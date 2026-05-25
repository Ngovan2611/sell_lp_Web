package com.example.sell_lp.dto.request.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CategoryCreationRequest {
    String categoryName;
    String icon;
    Boolean isDisplayed;
    Boolean hasSpecs;
}
