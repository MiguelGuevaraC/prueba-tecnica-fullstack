package com.sintad.prueba_tecnica_fullstack.service.interfaces;

import com.sintad.prueba_tecnica_fullstack.dto.product.ProductRequest;
import com.sintad.prueba_tecnica_fullstack.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface IProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse getById(Long id);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    Page<ProductResponse> search(Specification<?> spec, Pageable pageable);
}
