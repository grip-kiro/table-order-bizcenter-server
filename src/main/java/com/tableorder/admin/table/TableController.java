package com.tableorder.admin.table;

import com.tableorder.admin.auth.AdminPrincipal;
import com.tableorder.admin.common.exception.BusinessException;
import com.tableorder.admin.common.exception.ErrorCode;
import com.tableorder.admin.table.dto.CreateTableRequest;
import com.tableorder.admin.table.dto.TableResponse;
import com.tableorder.admin.table.dto.UpdateTableRequest;
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
public class TableController {

    private final TableService tableService;

    @GetMapping("/stores/{storeId}/tables")
    public ResponseEntity<List<TableResponse>> getTables(
            @PathVariable Long storeId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        validateStoreAccess(principal, storeId);
        return ResponseEntity.ok(tableService.getTables(storeId));
    }

    @PostMapping("/tables")
    public ResponseEntity<TableResponse> createTable(
            @Valid @RequestBody CreateTableRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tableService.createTable(principal.getStoreId(), request));
    }

    @PutMapping("/tables/{tableId}")
    public ResponseEntity<TableResponse> updateTable(
            @PathVariable Long tableId,
            @Valid @RequestBody UpdateTableRequest request,
            @AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(tableService.updateTable(principal.getStoreId(), tableId, request));
    }

    @DeleteMapping("/tables/{tableId}")
    public ResponseEntity<Void> deleteTable(
            @PathVariable Long tableId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        tableService.deleteTable(principal.getStoreId(), tableId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tables/{tableId}/complete")
    public ResponseEntity<Void> completeTableSession(
            @PathVariable Long tableId,
            @AuthenticationPrincipal AdminPrincipal principal) {
        tableService.completeTableSession(principal.getStoreId(), tableId);
        return ResponseEntity.ok().build();
    }

    private void validateStoreAccess(AdminPrincipal principal, Long storeId) {
        if (!principal.getStoreId().equals(storeId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
