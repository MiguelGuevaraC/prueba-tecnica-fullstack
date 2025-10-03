package com.sintad.prueba_tecnica_fullstack.service.seed;

import com.sintad.prueba_tecnica_fullstack.entity.Category;
import com.sintad.prueba_tecnica_fullstack.entity.Product;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.CategoryRepository;
import com.sintad.prueba_tecnica_fullstack.repository.ProductRepository;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ProductSeederService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public void seed(boolean force) {
        if (force) {
            productRepository.deleteAll();
        }

        if (productRepository.count() == 0) {
            User user = userRepository.findByUsername("usuario").orElseThrow();
            List<Category> categories = categoryRepository.findAll();

            List<String> productNames = List.of(
                    "Laptop Lenovo", "Camiseta Nike", "Refrigeradora Samsung", "Libro Java",
                    "Silla Gamer", "Pelota Adidas", "Zapatillas Puma", "Reloj Casio",
                    "Bicicleta Trek", "Cafetera Oster"
            );

            AtomicInteger index = new AtomicInteger(0);
            List<Product> products = productNames.stream()
                    .map(name -> {
                        int i = index.getAndIncrement();
                        Category category = categories.get(i % categories.size());
                        return Product.builder()
                                .name(name)
                                .description("Descripción de " + name)
                                .price(BigDecimal.valueOf(50 + (i * 10)))
                                .stock(10 + (i * 2))
                                .category(category)
                                .user(user)
                                .createdAt(LocalDateTime.now())
                                .build();
                    })
                    .toList();

            productRepository.saveAll(products);
        }
    }
     public void deleteAll() {
        productRepository.deleteAll();
    }
}
