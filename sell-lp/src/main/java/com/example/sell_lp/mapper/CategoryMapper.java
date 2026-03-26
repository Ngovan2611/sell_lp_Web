package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse categoryToCategoryResponse(Category category);
}
