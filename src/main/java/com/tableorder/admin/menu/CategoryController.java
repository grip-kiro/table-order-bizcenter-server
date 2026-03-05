package com.tableorder.admin.menu;

import com.tableorder.admin.auth.AdminPrincipal;
import com.tableorder.admin.menu.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/stores/{storeId}/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @PathVariable Long storeId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        validateStoreAccess(principal, storeId);
        return ResponseEntity.ok(categoryService.getCategories(storeId));
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(principal.getStoreId(), request));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(categoryService.updateCategory(principal.getStoreId(), categoryId, request));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        categoryService.deleteCategory(principal.getStoreId(), categoryId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/categories/order")
    public ResponseEntity<List<CategoryResponse>> updateCategoryOrder(
            @Valid @RequestBody DisplayOrderListRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(categoryService.updateDisplayOrder(principal.getStoreId(), request.getItems()));
    }

    private void validateStoreAccess(AdminPrincipal principal, Long storeId) {
        if (!principal.getStoreId().equals(storeId)) {
            throw new com.tableorder.admin.common.exception.BusinessException(
                    com.tableorder.admin.common.exception.ErrorCode.FORBIDDEN);
        }
    }
}
