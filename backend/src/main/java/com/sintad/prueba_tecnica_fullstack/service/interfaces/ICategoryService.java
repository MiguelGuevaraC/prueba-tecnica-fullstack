package com.sintad.prueba_tecnica_fullstack.service.interfaces;

import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryRequest;
import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ICategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse getById(Long id);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);

    Page<CategoryResponse> search(Specification<?> spec, Pageable pageable);
}
