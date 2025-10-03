package com.sintad.prueba_tecnica_fullstack.service.seed;

import com.sintad.prueba_tecnica_fullstack.entity.Category;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.CategoryRepository;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorySeederService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public void seed(boolean force) {
        if (force) {
            categoryRepository.deleteAll();
        }

        if (categoryRepository.count() == 0) {
            User admin = userRepository.findByUsername("admin").orElseThrow();

            List<String> categoryNames = List.of(
                    "Tecnología", "Ropa", "Electrodomésticos", "Libros", "Muebles",
                    "Juguetes", "Calzado", "Accesorios", "Deportes", "Hogar"
            );

            List<Category> categories = categoryNames.stream()
                    .map(name -> Category.builder()
                            .name(name)
                            .description("Descripción de " + name)
                            .user(admin)
                            .createdAt(LocalDateTime.now())
                            .build())
                    .toList();

            categoryRepository.saveAll(categories);
        }
    }

     public void deleteAll() {
        categoryRepository.deleteAll();
    }
}
