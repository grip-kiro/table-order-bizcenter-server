package com.tableorder.admin.menu;

import com.tableorder.admin.common.exception.BusinessException;
import com.tableorder.admin.common.exception.ErrorCode;
import com.tableorder.admin.domain.Category;
import com.tableorder.admin.domain.Menu;
import com.tableorder.admin.domain.MenuCategory;
import com.tableorder.admin.menu.dto.*;
import com.tableorder.admin.repository.CategoryRepository;
import com.tableorder.admin.repository.MenuCategoryRepository;
import com.tableorder.admin.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final CategoryRepository categoryRepository;

    public List<MenuResponse> getMenus(Long storeId) {
        return menuRepository.findByStoreIdOrderByDisplayOrder(storeId).stream()
                .map(this::toMenuResponse)
                .toList();
    }

    @Transactional
    public MenuResponse createMenu(Long storeId, CreateMenuRequest request) {
        validateCategoryIds(storeId, request.getCategoryIds());

        int maxOrder = menuRepository.findMaxDisplayOrderByStoreId(storeId);
        Menu menu = Menu.builder()
                .storeId(storeId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .displayOrder(maxOrder + 1)
                .build();
        menu = menuRepository.save(menu);

        saveMenuCategories(menu.getId(), request.getCategoryIds());
        return toMenuResponse(menuRepository.findById(menu.getId()).orElseThrow());
    }

    @Transactional
    public MenuResponse updateMenu(Long storeId, Long menuId, UpdateMenuRequest request) {
        Menu menu = findActiveMenu(storeId, menuId);
        validateCategoryIds(storeId, request.getCategoryIds());

        menu.update(request.getName(), request.getDescription(), request.getPrice(), request.getImageUrl());

        menuCategoryRepository.deleteByMenuId(menuId);
        saveMenuCategories(menuId, request.getCategoryIds());

        return toMenuResponse(menuRepository.findById(menuId).orElseThrow());
    }

    @Transactional
    public void deleteMenu(Long storeId, Long menuId) {
        Menu menu = findByIdAndStore(storeId, menuId);
        menu.softDelete();
    }

    @Transactional
    public MenuResponse updateSoldOut(Long storeId, Long menuId, boolean soldOut) {
        Menu menu = findActiveMenu(storeId, menuId);
        menu.updateSoldOut(soldOut);
        return toMenuResponse(menu);
    }

    @Transactional
    public List<MenuResponse> updateDisplayOrder(Long storeId, List<DisplayOrderRequest> items) {
        for (DisplayOrderRequest item : items) {
            Menu menu = findByIdAndStore(storeId, item.getId());
            menu.updateDisplayOrder(item.getDisplayOrder());
        }
        return menuRepository.findByStoreIdOrderByDisplayOrder(storeId).stream()
                .map(this::toMenuResponse)
                .toList();
    }

    private Menu findByIdAndStore(Long storeId, Long menuId) {
        return menuRepository.findByIdAndStoreId(menuId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
    }

    private Menu findActiveMenu(Long storeId, Long menuId) {
        Menu menu = findByIdAndStore(storeId, menuId);
        if (menu.isDeleted()) {
            throw new BusinessException(ErrorCode.MENU_DELETED);
        }
        return menu;
    }

    private void validateCategoryIds(Long storeId, List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            categoryRepository.findByIdAndStoreId(categoryId, storeId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }
    }

    private void saveMenuCategories(Long menuId, List<Long> categoryIds) {
        List<MenuCategory> menuCategories = categoryIds.stream()
                .map(catId -> new MenuCategory(menuId, catId))
                .toList();
        menuCategoryRepository.saveAll(menuCategories);
    }

    private MenuResponse toMenuResponse(Menu menu) {
        List<CategoryResponse> categories = menuCategoryRepository.findByMenuId(menu.getId()).stream()
                .map(mc -> categoryRepository.findById(mc.getCategoryId()).orElse(null))
                .filter(c -> c != null)
                .map(CategoryResponse::from)
                .toList();
        return MenuResponse.from(menu, categories);
    }
}
