package com.sintad.prueba_tecnica_fullstack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryRequest;
import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryResponse;
import com.sintad.prueba_tecnica_fullstack.entity.Category;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.ICategoryService;
import com.sintad.prueba_tecnica_fullstack.shared.filter.FilterSpecBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

        private MockMvc mockMvc;
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Mock
        private ICategoryService categoryService;
        @Mock
        private FilterSpecBuilder<Category> specBuilder;

        @BeforeEach
        void setup() {
                CategoryController controller = new CategoryController(categoryService, specBuilder);
                this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        }

        @Test
        void create_success() throws Exception {
                String validJson = """
                                {
                                  "name": "Bebidas",
                                  "description": "Refrescos y jugos"
                                }
                                """;

                CategoryResponse resp = CategoryResponse.builder()
                                .id(1L).name("Bebidas").description("Refrescos y jugos").build();

                when(categoryService.create(any(CategoryRequest.class))).thenReturn(resp);

                mockMvc.perform(post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(validJson))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().string(not(isEmptyOrNullString())))
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                                .andExpect(jsonPath("$.data.id").value(1))
                                .andExpect(jsonPath("$.data.name").value("Bebidas"))
                                .andExpect(jsonPath("$.data.description").value("Refrescos y jugos"));
        }

        @Test
        void getById_success() throws Exception {
                CategoryResponse resp = CategoryResponse.builder()
                                .id(10L).name("Lácteos").description("Leche y derivados").build();

                when(categoryService.getById(10L)).thenReturn(resp);

                mockMvc.perform(get("/api/categories/{id}", 10)
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().string(not(isEmptyOrNullString())))
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                                .andExpect(jsonPath("$.data.id").value(10))
                                .andExpect(jsonPath("$.data.name").value("Lácteos"))
                                .andExpect(jsonPath("$.data.description").value("Leche y derivados"));
        }

        @Test
        void update_success() throws Exception {
                String validJson = """
                                {
                                  "name": "Snacks",
                                  "description": "Galletas y papas"
                                }
                                """;

                CategoryResponse resp = CategoryResponse.builder()
                                .id(5L).name("Snacks").description("Galletas y papas").build();

                when(categoryService.update(eq(5L), any(CategoryRequest.class))).thenReturn(resp);

                mockMvc.perform(put("/api/categories/{id}", 5)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(validJson))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().string(not(isEmptyOrNullString())))
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                                .andExpect(jsonPath("$.data.id").value(5))
                                .andExpect(jsonPath("$.data.name").value("Snacks"))
                                .andExpect(jsonPath("$.data.description").value("Galletas y papas"));
        }

        @Test
        void delete_success() throws Exception {
                doNothing().when(categoryService).delete(9L);

                mockMvc.perform(delete("/api/categories/{id}", 9)
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().string(not(isEmptyOrNullString())))
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                                .andExpect(jsonPath("$.data").value("Categoría eliminada correctamente"));
        }

        @Test
        void list_success_withPaginationAndFilters_singleItem() throws Exception {
                
                Specification<Category> spec = (root, query, cb) -> cb.conjunction();
                when(specBuilder.build(eq(Category.class), anyMap(), any(MultiValueMap.class))).thenReturn(spec);

  
                List<CategoryResponse> items = new ArrayList<>();
                items.add(CategoryResponse.builder()
                                .id(1L)
                                .name("Tecnología")
                                .description("Descripción de Tecnología")
                                .userId(1L)
                                .userName("Administrador General")
                                .createdAt(LocalDateTime.parse("2025-10-02T22:06:05.313240"))
                                .build());

                Page<CategoryResponse> page = new PageImpl<>(items, PageRequest.of(0, 10), 1);
                when(categoryService.search(any(), any(Pageable.class))).thenReturn(page);

                mockMvc.perform(get("/api/categories")
                                .param("per_page", "10")
                                .param("page", "1")
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Operación exitosa"))

                               
                                .andExpect(jsonPath("$.data.data", hasSize(1)))
                                .andExpect(jsonPath("$.data.data[0].id").value(1))
                                .andExpect(jsonPath("$.data.data[0].name").value("Tecnología"))
                                .andExpect(jsonPath("$.data.data[0].description").value("Descripción de Tecnología"))
                                .andExpect(jsonPath("$.data.data[0].userId").value(1))
                                .andExpect(jsonPath("$.data.data[0].userName").value("Administrador General"))

                                
                                .andExpect(jsonPath("$.data.links.first", containsString("/api/categories?page=1")))
                                .andExpect(jsonPath("$.data.links.last", containsString("/api/categories?page=1")))
                                .andExpect(jsonPath("$.data.links.prev").value(nullValue()))
                                .andExpect(jsonPath("$.data.links.next").value(nullValue()))

                                
                                .andExpect(jsonPath("$.data.meta.currentPage").value(1))
                                .andExpect(jsonPath("$.data.meta.from").value(1))
                                .andExpect(jsonPath("$.data.meta.lastPage").value(1))
                                .andExpect(jsonPath("$.data.meta.path", containsString("/api/categories")))
                                .andExpect(jsonPath("$.data.meta.perPage").value(10))
                                .andExpect(jsonPath("$.data.meta.to").value(1))
                                .andExpect(jsonPath("$.data.meta.total").value(1));

        }
}
