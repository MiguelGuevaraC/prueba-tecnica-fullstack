package com.sintad.prueba_tecnica_fullstack.repository;

import com.sintad.prueba_tecnica_fullstack.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceLessThanEqual(Double price);

    List<Product> findByCategory_Id(Long categoryId);

    List<Product> findByUser_Id(Long userId);

    // 👇 aquí corregido: devuelve Product, no Category
    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameAndDeletedAtIsNull(String name);
}
