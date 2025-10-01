package com.sintad.prueba_tecnica_fullstack.service.impl;

import com.sintad.prueba_tecnica_fullstack.dto.product.ProductRequest;
import com.sintad.prueba_tecnica_fullstack.dto.product.ProductResponse;
import com.sintad.prueba_tecnica_fullstack.entity.Category;
import com.sintad.prueba_tecnica_fullstack.entity.Product;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.CategoryRepository;
import com.sintad.prueba_tecnica_fullstack.repository.ProductRepository;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.IProductService;
import com.sintad.prueba_tecnica_fullstack.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse create(ProductRequest request) {
        //  obtener usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new NotFoundException("Usuario autenticado no encontrado"));

        //  validar categoría obligatoria
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .user(user) // 👈 asignar usuario logueado
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con ID: " + id));
        return toResponse(product);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con ID: " + id));

        Optional.ofNullable(request.getName()).ifPresent(product::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(request.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(request.getStock()).ifPresent(product::setStock);

        //  validar categoría si la mandan
        Optional.ofNullable(request.getCategoryId()).ifPresent(categoryId -> {
            Category category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + categoryId));
            product.setCategory(category);
        });

        product.setUpdatedAt(LocalDateTime.now());

        return toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con ID: " + id));

        product.setDeletedAt(LocalDateTime.now()); //  soft delete
        productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> search(Specification<?> spec, Pageable pageable) {
        return productRepository.findAll((Specification<Product>) spec, pageable)
                .map(this::toResponse);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .userId(product.getUser() != null ? product.getUser().getId() : null)
                .userName(product.getUser() != null ? product.getUser().getFullName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }
}
