package com.sintad.prueba_tecnica_fullstack.service.impl;

import com.sintad.prueba_tecnica_fullstack.dto.product.ProductRequest;
import com.sintad.prueba_tecnica_fullstack.dto.product.ProductResponse;
import com.sintad.prueba_tecnica_fullstack.entity.Category;
import com.sintad.prueba_tecnica_fullstack.entity.Product;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.CategoryRepository;
import com.sintad.prueba_tecnica_fullstack.repository.ProductRepository;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.shared.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl service;

    private static final String AUTH_USERNAME = "admin";

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(AUTH_USERNAME, "ignored");
        SecurityContextHolder.setContext(new SecurityContextImpl(auth));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_success() {
        ProductRequest req = new ProductRequest();
        req.setName("Teclado");
        req.setDescription("Mecánico");
        req.setPrice(new BigDecimal("99.90"));
        req.setStock(10);
        req.setCategoryId(1L);

        User user = User.builder().id(10L).username(AUTH_USERNAME).fullName("Administrador General").build();
        when(userRepository.findByUsernameAndDeletedAtIsNull(AUTH_USERNAME)).thenReturn(Optional.of(user));

        Category cat = Category.builder().id(1L).name("Tecnología").build();
        when(categoryRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cat));

        Product saved = Product.builder()
                .id(100L).name("Teclado").description("Mecánico")
                .price(new BigDecimal("99.90")).stock(10)
                .user(user).category(cat).createdAt(LocalDateTime.now()).build();
        when(productRepository.save(Mockito.<Product>any())).thenReturn(saved);

        ProductResponse resp = service.create(req);

        assertThat(resp.getId(), is(100L));
        assertThat(resp.getName(), is("Teclado"));
        assertThat(resp.getDescription(), is("Mecánico"));
        assertThat(resp.getPrice(), comparesEqualTo(new BigDecimal("99.90")));
        assertThat(resp.getStock(), is(10));
        assertThat(resp.getUserId(), is(10L));
        assertThat(resp.getCategoryId(), is(1L));
        assertThat(resp.getCategoryName(), is("Tecnología"));

        verify(userRepository).findByUsernameAndDeletedAtIsNull(AUTH_USERNAME);
        verify(categoryRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).save(Mockito.<Product>any());
    }

    @Test
    void create_fails_userNotFound() {
        ProductRequest req = new ProductRequest();
        req.setName("Teclado");
        req.setCategoryId(1L);

        when(userRepository.findByUsernameAndDeletedAtIsNull(AUTH_USERNAME)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.create(req));
        assertThat(ex.getMessage(), containsString("Usuario autenticado no encontrado"));

        verify(productRepository, never()).save(any());
    }

    @Test
    void create_fails_categoryNotFound() {
        ProductRequest req = new ProductRequest();
        req.setName("Teclado");
        req.setCategoryId(99L);

        User user = User.builder().id(10L).username(AUTH_USERNAME).build();
        when(userRepository.findByUsernameAndDeletedAtIsNull(AUTH_USERNAME)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.create(req));
        assertThat(ex.getMessage(), containsString("Categoría no encontrada con ID: 99"));

        verify(productRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        User user = User.builder().id(1L).fullName("Admin").build();
        Category cat = Category.builder().id(2L).name("Tecnología").build();
        Product prod = Product.builder()
                .id(50L).name("Mouse").description("Óptico")
                .price(new BigDecimal("49.90")).stock(5)
                .user(user).category(cat).createdAt(LocalDateTime.now()).build();

        when(productRepository.findByIdAndDeletedAtIsNull(50L)).thenReturn(Optional.of(prod));

        ProductResponse resp = service.getById(50L);

        assertThat(resp.getId(), is(50L));
        assertThat(resp.getName(), is("Mouse"));
        assertThat(resp.getDescription(), is("Óptico"));
        assertThat(resp.getPrice(), comparesEqualTo(new BigDecimal("49.90")));
        assertThat(resp.getStock(), is(5));
        assertThat(resp.getUserId(), is(1L));
        assertThat(resp.getCategoryId(), is(2L));
        assertThat(resp.getCategoryName(), is("Tecnología"));
    }

    @Test
    void getById_notFound() {
        when(productRepository.findByIdAndDeletedAtIsNull(404L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getById(404L));
        assertThat(ex.getMessage(), containsString("Producto no encontrado con ID: 404"));
    }

    @Test
    void update_success_updateFields_andCategory() {
        Category oldCat = Category.builder().id(1L).name("Antiguo").build();
        Product existing = Product.builder()
                .id(70L).name("Laptop").description("i5")
                .price(new BigDecimal("2500.00")).stock(3)
                .category(oldCat).createdAt(LocalDateTime.now().minusDays(1)).build();

        when(productRepository.findByIdAndDeletedAtIsNull(70L)).thenReturn(Optional.of(existing));

        ProductRequest req = new ProductRequest();
        req.setName("Laptop Pro");
        req.setDescription("i7");
        req.setPrice(new BigDecimal("3500.00"));
        req.setStock(2);
        req.setCategoryId(2L);

        Category newCat = Category.builder().id(2L).name("Tecnología").build();
        when(categoryRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(newCat));

        Product saved = Product.builder()
                .id(70L).name("Laptop Pro").description("i7")
                .price(new BigDecimal("3500.00")).stock(2)
                .category(newCat)
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now()).build();
        when(productRepository.save(Mockito.<Product>any())).thenReturn(saved);

        ProductResponse resp = service.update(70L, req);

        assertThat(resp.getId(), is(70L));
        assertThat(resp.getName(), is("Laptop Pro"));
        assertThat(resp.getDescription(), is("i7"));
        assertThat(resp.getPrice(), comparesEqualTo(new BigDecimal("3500.00")));
        assertThat(resp.getStock(), is(2));
        assertThat(resp.getCategoryId(), is(2L));
        assertThat(resp.getCategoryName(), is("Tecnología"));

        verify(productRepository).findByIdAndDeletedAtIsNull(70L);
        verify(categoryRepository).findByIdAndDeletedAtIsNull(2L);
        verify(productRepository).save(Mockito.<Product>any());
    }

    @Test
    void update_success_partialWithoutCategoryChange() {
        Product existing = Product.builder()
                .id(71L).name("Impresora").description("Ink")
                .price(new BigDecimal("500.00")).stock(4)
                .createdAt(LocalDateTime.now().minusDays(2)).build();

        when(productRepository.findByIdAndDeletedAtIsNull(71L)).thenReturn(Optional.of(existing));

        ProductRequest req = new ProductRequest();
        req.setDescription("Laser");
        req.setStock(6);

        Product saved = Product.builder()
                .id(71L).name("Impresora").description("Laser")
                .price(new BigDecimal("500.00")).stock(6)
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now()).build();
        when(productRepository.save(Mockito.<Product>any())).thenReturn(saved);

        ProductResponse resp = service.update(71L, req);

        assertThat(resp.getId(), is(71L));
        assertThat(resp.getDescription(), is("Laser"));
        assertThat(resp.getStock(), is(6));
        verify(categoryRepository, never()).findByIdAndDeletedAtIsNull(any());
    }

    @Test
    void update_fails_productNotFound() {
        when(productRepository.findByIdAndDeletedAtIsNull(70L)).thenReturn(Optional.empty());

        ProductRequest req = new ProductRequest();
        req.setName("X");

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.update(70L, req));
        assertThat(ex.getMessage(), containsString("Producto no encontrado con ID: 70"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void update_fails_newCategoryNotFound() {
        Product existing = Product.builder().id(80L).name("Tablet").build();
        when(productRepository.findByIdAndDeletedAtIsNull(80L)).thenReturn(Optional.of(existing));

        ProductRequest req = new ProductRequest();
        req.setCategoryId(999L);

        when(categoryRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.update(80L, req));
        assertThat(ex.getMessage(), containsString("Categoría no encontrada con ID: 999"));

        verify(productRepository, never()).save(any());
    }

    @Test
    void delete_success_softDeleteAndSave() {
        Product existing = Product.builder().id(90L).name("Monitor").build();
        when(productRepository.findByIdAndDeletedAtIsNull(90L)).thenReturn(Optional.of(existing));

        service.delete(90L);

        verify(productRepository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();
        assertThat(saved.getId(), is(90L));
        assertThat(saved.getDeletedAt(), notNullValue());
    }

    @Test
    void delete_notFound() {
        when(productRepository.findByIdAndDeletedAtIsNull(90L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.delete(90L));
        assertThat(ex.getMessage(), containsString("Producto no encontrado con ID: 90"));

        verify(productRepository, never()).save(any());
    }

    @Test
    void search_success_mapsPage() {
        User u = User.builder().id(1L).fullName("Administrador General").build();
        Category c = Category.builder().id(2L).name("Tecnología").build();
        Product p = Product.builder()
                .id(1L).name("SSD").description("NVMe")
                .price(new BigDecimal("299.99")).stock(20)
                .user(u).category(c)
                .createdAt(LocalDateTime.now()).build();

        Page<Product> entities = new PageImpl<>(List.of(p), PageRequest.of(0, 10), 1);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entities);

        Page<ProductResponse> result = service.search(
                (Specification<?>) (root, query, cb) -> cb.conjunction(),
                PageRequest.of(0, 10)
        );

        assertThat(result.getTotalElements(), is(1L));
        assertThat(result.getContent(), hasSize(1));
        ProductResponse resp = result.getContent().get(0);
        assertThat(resp.getId(), is(1L));
        assertThat(resp.getName(), is("SSD"));
        assertThat(resp.getCategoryId(), is(2L));
        assertThat(resp.getCategoryName(), is("Tecnología"));
        assertThat(resp.getUserId(), is(1L));
        assertThat(resp.getUserName(), is("Administrador General"));
    }
}
