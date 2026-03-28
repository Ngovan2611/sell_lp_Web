package com.example.sell_lp.service;

import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.entity.Category;
import com.example.sell_lp.mapper.CategoryMapper;
import com.example.sell_lp.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CategoryService {

    CategoryRepository categoryRepository;

    CategoryMapper categoryMapper;

    public List<CategoryResponse> findAll() {
        List<Category> categories = categoryRepository.findAll();


        return categories.stream()
                .map(categoryMapper::categoryToCategoryResponse)
                .collect(Collectors.toList());
    }
}
