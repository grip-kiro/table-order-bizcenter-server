package com.tableorder.admin.menu;

import com.tableorder.admin.common.exception.BusinessException;
import com.tableorder.admin.common.exception.ErrorCode;
import com.tableorder.admin.domain.Category;
import com.tableorder.admin.menu.dto.*;
import com.tableorder.admin.repository.CategoryRepository;
import com.tableorder.admin.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    public List<CategoryResponse> getCategories(Long storeId) {
        return categoryRepository.findByStoreIdOrderByDisplayOrder(storeId).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(Long storeId, CreateCategoryRequest request) {
        int maxOrder = categoryRepository.findMaxDisplayOrderByStoreId(storeId);
        Category category = Category.builder()
                .storeId(storeId)
                .name(request.getName())
                .displayOrder(maxOrder + 1)
                .build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long storeId, Long categoryId, UpdateCategoryRequest request) {
        Category category = findByIdAndStore(categoryId, storeId);
        category.updateName(request.getName());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long storeId, Long categoryId) {
        Category category = findByIdAndStore(categoryId, storeId);
        if (menuCategoryRepository.existsActiveMenuByCategoryId(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_MENUS);
        }
        categoryRepository.delete(category);
    }

    @Transactional
    public List<CategoryResponse> updateDisplayOrder(Long storeId, List<DisplayOrderRequest> items) {
        for (DisplayOrderRequest item : items) {
            Category category = findByIdAndStore(item.getId(), storeId);
            category.updateDisplayOrder(item.getDisplayOrder());
        }
        return categoryRepository.findByStoreIdOrderByDisplayOrder(storeId).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    private Category findByIdAndStore(Long categoryId, Long storeId) {
        return categoryRepository.findByIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
