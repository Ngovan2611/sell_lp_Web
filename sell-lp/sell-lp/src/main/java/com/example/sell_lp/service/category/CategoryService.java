package com.example.sell_lp.service.category;

import com.example.sell_lp.dto.request.category.CategoryCreationRequest;
import com.example.sell_lp.dto.request.category.CategoryUpdateRequest;
import com.example.sell_lp.dto.response.category.CategoryResponse;
import com.example.sell_lp.entity.Category;
import com.example.sell_lp.mapper.CategoryMapper;
import com.example.sell_lp.repository.category.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<CategoryResponse> getDisplayedCategoriesForUser() {
        List<Category> categories = categoryRepository.findByIsDisplayedTrue();
        return categories.stream()
                .map(categoryMapper::categoryToCategoryResponse)
                .collect(Collectors.toList());
    }

    public Category getById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
    }


    public void save(CategoryCreationRequest request) {

        Category category = categoryMapper.toCategory(request);

        categoryRepository.save(category);

    }
    @Transactional
    public void update(Integer id, CategoryUpdateRequest request) {
        Category category = getById(id);


        categoryMapper.toUserUpdateRequest(category, request);
    }

    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }

}
