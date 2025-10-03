package com.sintad.prueba_tecnica_fullstack.controller;

import com.sintad.prueba_tecnica_fullstack.dto.product.ProductRequest;
import com.sintad.prueba_tecnica_fullstack.dto.product.ProductResponse;
import com.sintad.prueba_tecnica_fullstack.dto.product.ProductListRequest;
import com.sintad.prueba_tecnica_fullstack.entity.Product;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.IProductService;
import com.sintad.prueba_tecnica_fullstack.shared.dto.ApiResponse;
import com.sintad.prueba_tecnica_fullstack.shared.dto.PageResponse;
import com.sintad.prueba_tecnica_fullstack.shared.filter.FilterSpecBuilder;
import com.sintad.prueba_tecnica_fullstack.shared.util.PageableUtil;
import com.sintad.prueba_tecnica_fullstack.shared.validation.OnCreate;
import com.sintad.prueba_tecnica_fullstack.shared.validation.OnUpdate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final FilterSpecBuilder<Product> specBuilder;

    @PostMapping
    @Operation(summary = "Crear producto")
    public ApiResponse<ProductResponse> create(@Validated(OnCreate.class) @RequestBody ProductRequest request) {
        return ApiResponse.ok(productService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ApiResponse<ProductResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    public ApiResponse<ProductResponse> update(@PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody ProductRequest request) {
        return ApiResponse.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    public ApiResponse<String> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok("Producto eliminado correctamente");
    }

    @GetMapping
    @Operation(summary = "Listar productos con paginación y filtros dinámicos")
    public ApiResponse<PageResponse<ProductResponse>> list(
            @ParameterObject @Valid ProductListRequest listRequest,
            @Parameter(hidden = true) @RequestParam MultiValueMap<String, String> params,
            HttpServletRequest request) {
        Specification<Product> spec = specBuilder.build(Product.class, Product.ALLOWED_FILTERS, params);
        Pageable pageable = PageableUtil.fromListRequest(listRequest);
        Page<ProductResponse> page = productService.search(spec, pageable);
        return ApiResponse.ok(PageResponse.from(page, request));
    }
}
