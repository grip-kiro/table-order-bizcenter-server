package com.tableorder.admin.menu;

import com.tableorder.admin.auth.AdminPrincipal;
import com.tableorder.admin.common.exception.BusinessException;
import com.tableorder.admin.common.exception.ErrorCode;
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
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/stores/{storeId}/menus")
    public ResponseEntity<List<MenuResponse>> getMenus(
            @PathVariable Long storeId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        validateStoreAccess(principal, storeId);
        return ResponseEntity.ok(menuService.getMenus(storeId));
    }

    @PostMapping("/menus")
    public ResponseEntity<MenuResponse> createMenu(
            @Valid @RequestBody CreateMenuRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(menuService.createMenu(principal.getStoreId(), request));
    }

    @PutMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long menuId,
            @Valid @RequestBody UpdateMenuRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(menuService.updateMenu(principal.getStoreId(), menuId, request));
    }

    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(
            @PathVariable Long menuId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        menuService.deleteMenu(principal.getStoreId(), menuId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/menus/{menuId}/sold-out")
    public ResponseEntity<MenuResponse> updateSoldOut(
            @PathVariable Long menuId,
            @Valid @RequestBody SoldOutRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(menuService.updateSoldOut(principal.getStoreId(), menuId, request.getSoldOut()));
    }

    @PatchMapping("/menus/order")
    public ResponseEntity<List<MenuResponse>> updateMenuOrder(
            @Valid @RequestBody DisplayOrderListRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(menuService.updateDisplayOrder(principal.getStoreId(), request.getItems()));
    }

    private void validateStoreAccess(AdminPrincipal principal, Long storeId) {
        if (!principal.getStoreId().equals(storeId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
