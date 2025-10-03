package com.sintad.prueba_tecnica_fullstack.controller;

import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryListRequest;
import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryRequest;
import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryResponse;
import com.sintad.prueba_tecnica_fullstack.entity.Category;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.ICategoryService;
import com.sintad.prueba_tecnica_fullstack.shared.dto.ApiResponse;
import com.sintad.prueba_tecnica_fullstack.shared.dto.PageResponse;
import com.sintad.prueba_tecnica_fullstack.shared.filter.FilterSpecBuilder;
import com.sintad.prueba_tecnica_fullstack.shared.util.PageableUtil;
import com.sintad.prueba_tecnica_fullstack.shared.validation.OnCreate;
import com.sintad.prueba_tecnica_fullstack.shared.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;
    private final FilterSpecBuilder<Category> specBuilder;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear categoría")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Validated(OnCreate.class) @RequestBody CategoryRequest request) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.ok(categoryService.create(request)));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener categoría por ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.ok(categoryService.getById(id)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar categoría")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody CategoryRequest request) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.ok(categoryService.update(id, request)));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Eliminar categoría")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.ok("Categoría eliminada correctamente"));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar categorías con paginación y filtros dinámicos")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> list(
            @ParameterObject @Valid CategoryListRequest listRequest,
            @Parameter(hidden = true) @RequestParam MultiValueMap<String, String> params,
            HttpServletRequest request) {

        Specification<Category> spec = specBuilder.build(Category.class, Category.ALLOWED_FILTERS, params);
        Pageable pageable = PageableUtil.fromListRequest(listRequest);
        Page<CategoryResponse> page = categoryService.search(spec, pageable);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.ok(PageResponse.from(page, request)));
    }
}
