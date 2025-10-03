package com.sintad.prueba_tecnica_fullstack.service.impl;

import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryRequest;
import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryResponse;
import com.sintad.prueba_tecnica_fullstack.entity.Category;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.CategoryRepository;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.ICategoryService;
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
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría activa con ese nombre");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario autenticado no encontrado"));

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));
        return toResponse(category);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));

        if (request.getName() != null &&
                categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot(request.getName(), id)) {
            throw new IllegalArgumentException("Ya existe otra categoría activa con ese nombre");
        }

        Optional.ofNullable(request.getName()).ifPresent(category::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(category::setDescription);

        category.setUpdatedAt(LocalDateTime.now());

        return toResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));

        categoryRepository.delete(category);
    }

    @Override
    public Page<CategoryResponse> search(Specification<?> spec, Pageable pageable) {
        return categoryRepository.findAll((Specification<Category>) spec, pageable).map(this::toResponse);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .userId(category.getUser() != null ? category.getUser().getId() : null)
                .userName(category.getUser() != null ? category.getUser().getFullName() : null)
                .createdAt(category.getCreatedAt())
                .build();
    }

}
