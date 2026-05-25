package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.category.CategoryCreationRequest;
import com.example.sell_lp.dto.request.category.CategoryUpdateRequest;
import com.example.sell_lp.dto.response.category.CategoryResponse;
import com.example.sell_lp.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse categoryToCategoryResponse(Category category);

    Category toCategory(CategoryCreationRequest request);


    void toUserUpdateRequest(@MappingTarget Category category, CategoryUpdateRequest categoryUpdateRequest);

}
