package com.sintad.prueba_tecnica_fullstack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sintad.prueba_tecnica_fullstack.dto.product.ProductRequest;
import com.sintad.prueba_tecnica_fullstack.dto.product.ProductResponse;
import com.sintad.prueba_tecnica_fullstack.entity.Product;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.IProductService;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IProductService productService;
    @Mock
    private FilterSpecBuilder<Product> specBuilder;

    @BeforeEach
    void setup() {
        ProductController controller = new ProductController(productService, specBuilder);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void create_success() throws Exception {
        String validJson = """
                {
                  "name": "Laptop Lenovo",
                  "description": "Descripción de Laptop Lenovo",
                  "price": 50.00,
                  "stock": 10,
                  "categoryId": 1
                }
                """;

        ProductResponse resp = ProductResponse.builder()
                .id(1L)
                .name("Laptop Lenovo")
                .description("Descripción de Laptop Lenovo")
                .price(new java.math.BigDecimal("50.00"))
                .stock(10)
                .categoryId(1L)
                .categoryName("Tecnología")
                .userId(2L)
                .userName("Usuario Demo")
                .createdAt(LocalDateTime.parse("2025-10-02T22:06:05.409925"))
                .build();

        when(productService.create(any(ProductRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Laptop Lenovo"))
                .andExpect(jsonPath("$.data.description").value("Descripción de Laptop Lenovo"))
                .andExpect(jsonPath("$.data.price").value(50.00))
                .andExpect(jsonPath("$.data.stock").value(10))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.categoryName").value("Tecnología"))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.userName").value("Usuario Demo"));
    }

    @Test
    void getById_success() throws Exception {
        ProductResponse resp = ProductResponse.builder()
                .id(1L)
                .name("Laptop Lenovo")
                .description("Descripción de Laptop Lenovo")
                .price(new java.math.BigDecimal("50.00"))
                .stock(10)
                .categoryId(1L)
                .categoryName("Tecnología")
                .userId(2L)
                .userName("Usuario Demo")
                .createdAt(LocalDateTime.parse("2025-10-02T22:06:05.409925"))
                .build();

        when(productService.getById(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/products/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Laptop Lenovo"))
                .andExpect(jsonPath("$.data.categoryName").value("Tecnología"))
                .andExpect(jsonPath("$.data.userName").value("Usuario Demo"));
    }

    @Test
    void update_success() throws Exception {
        String validJson = """
                {
                  "name": "Laptop Lenovo",
                  "description": "Descripción de Laptop Lenovo",
                  "price": 55.00,
                  "stock": 12,
                  "categoryId": 1
                }
                """;

        ProductResponse resp = ProductResponse.builder()
                .id(1L)
                .name("Laptop Lenovo")
                .description("Descripción de Laptop Lenovo")
                .price(new java.math.BigDecimal("55.00"))
                .stock(12)
                .categoryId(1L)
                .categoryName("Tecnología")
                .userId(2L)
                .userName("Usuario Demo")
                .createdAt(LocalDateTime.parse("2025-10-02T22:06:05.409925"))
                .build();

        when(productService.update(eq(1L), any(ProductRequest.class))).thenReturn(resp);

        mockMvc.perform(put("/api/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Laptop Lenovo"))
                .andExpect(jsonPath("$.data.price").value(55.00))
                .andExpect(jsonPath("$.data.stock").value(12))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.categoryName").value("Tecnología"));
    }

    @Test
    void delete_success() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/products/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                .andExpect(jsonPath("$.data").value("Producto eliminado correctamente"));
    }

    @Test
    void list_success_singleItem_onlyId1() throws Exception {
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();
        when(specBuilder.build(eq(Product.class), anyMap(), any(MultiValueMap.class))).thenReturn(spec);

        ProductResponse item = ProductResponse.builder()
                .id(1L)
                .name("Laptop Lenovo")
                .description("Descripción de Laptop Lenovo")
                .price(new java.math.BigDecimal("50.00"))
                .stock(10)
                .categoryId(1L)
                .categoryName("Tecnología")
                .userId(2L)
                .userName("Usuario Demo")
                .createdAt(LocalDateTime.parse("2025-10-02T22:06:05.409925"))
                .build();

        Page<ProductResponse> page = new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1);
        when(productService.search(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                .param("per_page", "10")
                .param("page", "1")
              
                .param("name", "")
                .param("description", "")
                .param("category$name", "")
                .param("user$username", "")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())

             
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))

                .andExpect(jsonPath("$.data.data", hasSize(1)))
                .andExpect(jsonPath("$.data.data[0].id").value(1))
                .andExpect(jsonPath("$.data.data[0].name").value("Laptop Lenovo"))
                .andExpect(jsonPath("$.data.data[0].price").value(50.00))
                .andExpect(jsonPath("$.data.data[0].stock").value(10))
                .andExpect(jsonPath("$.data.data[0].categoryId").value(1))
                .andExpect(jsonPath("$.data.data[0].categoryName").value("Tecnología"))
                .andExpect(jsonPath("$.data.data[0].userId").value(2))
                .andExpect(jsonPath("$.data.data[0].userName").value("Usuario Demo"))

                .andExpect(jsonPath("$.data.links.first",
                        anyOf(
                                allOf(containsString("/api/products?"), containsString("page=1"),
                                        containsString("per_page=10")),
                                allOf(containsString("/api/products?"), containsString("page=1"))
                        )))
                .andExpect(jsonPath("$.data.links.last",
                        anyOf(
                                allOf(containsString("/api/products?"), containsString("page=1"),
                                        containsString("per_page=10")),
                                allOf(containsString("/api/products?"), containsString("page=1"))
                        )))

                .andExpect(jsonPath("$.data.links.prev").value(nullValue()))
                .andExpect(jsonPath("$.data.links.next").value(nullValue()))

                .andExpect(jsonPath("$.data.meta.currentPage").value(1))
                .andExpect(jsonPath("$.data.meta.from").value(1))
                .andExpect(jsonPath("$.data.meta.lastPage").value(1))
                .andExpect(jsonPath("$.data.meta.path", containsString("/api/products")))
                .andExpect(jsonPath("$.data.meta.perPage").value(10))
                .andExpect(jsonPath("$.data.meta.to").value(1))
                .andExpect(jsonPath("$.data.meta.total").value(1));
    }
}
