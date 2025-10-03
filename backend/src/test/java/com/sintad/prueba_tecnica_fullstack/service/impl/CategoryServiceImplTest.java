package com.sintad.prueba_tecnica_fullstack.service.impl;

import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryRequest;
import com.sintad.prueba_tecnica_fullstack.dto.category.CategoryResponse;
import com.sintad.prueba_tecnica_fullstack.entity.Category;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.CategoryRepository;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.shared.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryServiceImpl service;

    private static final String AUTH_USERNAME = "admin";

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
        CategoryRequest req = new CategoryRequest();
        req.setName("Bebidas");
        req.setDescription("Refrescos y jugos");

        when(categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNull("Bebidas")).thenReturn(false);

        User user = User.builder().id(99L).username(AUTH_USERNAME).fullName("Administrador General").build();
        when(userRepository.findByUsername(AUTH_USERNAME)).thenReturn(Optional.of(user));

        Category saved = Category.builder()
                .id(1L).name("Bebidas").description("Refrescos y jugos")
                .user(user).createdAt(LocalDateTime.now()).build();
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(saved);

        CategoryResponse resp = service.create(req);

        assertThat(resp.getId(), is(1L));
        assertThat(resp.getName(), is("Bebidas"));
        assertThat(resp.getDescription(), is("Refrescos y jugos"));
        assertThat(resp.getUserId(), is(99L));
        assertThat(resp.getUserName(), is("Administrador General"));

        verify(categoryRepository).existsByNameIgnoreCaseAndDeletedAtIsNull("Bebidas");
        verify(userRepository).findByUsername(AUTH_USERNAME);
        verify(categoryRepository).save(Mockito.<Category>any());
    }

    @Test
    void create_fails_whenNameExists() {
        CategoryRequest req = new CategoryRequest();
        req.setName("Repetida");

        when(categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNull("Repetida")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(req));
        assertThat(ex.getMessage(), containsString("Ya existe una categoría activa"));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void create_fails_whenAuthenticatedUserNotFound() {
        CategoryRequest req = new CategoryRequest();
        req.setName("Útiles");

        when(categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNull("Útiles")).thenReturn(false);
        when(userRepository.findByUsername(AUTH_USERNAME)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.create(req));
        assertThat(ex.getMessage(), containsString("Usuario autenticado no encontrado"));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        User u = User.builder().id(5L).fullName("Admin").build();
        Category cat = Category.builder()
                .id(10L).name("Lácteos").description("Leche y derivados")
                .user(u).createdAt(LocalDateTime.now()).build();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(cat));

        CategoryResponse resp = service.getById(10L);

        assertThat(resp.getId(), is(10L));
        assertThat(resp.getName(), is("Lácteos"));
        assertThat(resp.getDescription(), is("Leche y derivados"));
        assertThat(resp.getUserId(), is(5L));
        assertThat(resp.getUserName(), is("Admin"));
    }

    @Test
    void getById_notFound() {
        when(categoryRepository.findById(123L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getById(123L));
        assertThat(ex.getMessage(), containsString("Categoría no encontrada con ID: 123"));
    }

    @Test
    void update_success() {
        Category existing = Category.builder()
                .id(5L).name("Snacks").description("Vieja desc")
                .createdAt(LocalDateTime.now().minusDays(1)).build();

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot("Snacks", 5L)).thenReturn(false);

        CategoryRequest req = new CategoryRequest();
        req.setName("Snacks");
        req.setDescription("Galletas y papas");

        Category saved = Category.builder()
                .id(5L).name("Snacks").description("Galletas y papas")
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now()).build();

        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(saved);

        CategoryResponse resp = service.update(5L, req);

        assertThat(resp.getId(), is(5L));
        assertThat(resp.getName(), is("Snacks"));
        assertThat(resp.getDescription(), is("Galletas y papas"));

        verify(categoryRepository).findById(5L);
        verify(categoryRepository).existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot("Snacks", 5L);
        verify(categoryRepository).save(Mockito.<Category>any());
    }

    @Test
    void update_fails_whenNameConflict() {
        Category existing = Category.builder().id(7L).name("Tech").build();
        when(categoryRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot("Tech", 7L)).thenReturn(true);

        CategoryRequest req = new CategoryRequest();
        req.setName("Tech");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(7L, req));
        assertThat(ex.getMessage(), containsString("Ya existe otra categoría activa"));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_notFound() {
        when(categoryRepository.findById(404L)).thenReturn(Optional.empty());

        CategoryRequest req = new CategoryRequest();
        req.setName("Algo");

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.update(404L, req));
        assertThat(ex.getMessage(), containsString("Categoría no encontrada con ID: 404"));
    }

    @Test
    void delete_success() {
        Category existing = Category.builder().id(9L).name("Eliminar").build();
        when(categoryRepository.findById(9L)).thenReturn(Optional.of(existing));

        service.delete(9L);

        verify(categoryRepository).delete(existing);
    }

    @Test
    void delete_notFound() {
        when(categoryRepository.findById(9L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.delete(9L));
        assertThat(ex.getMessage(), containsString("Categoría no encontrada con ID: 9"));
        verify(categoryRepository, never()).delete(Mockito.<Category>any());
    }

    @Test
    void search_success() {
        User u = User.builder().id(1L).fullName("Administrador General").build();
        Category c1 = Category.builder()
                .id(1L).name("Tecnología").description("Descripción de Tecnología").user(u)
                .createdAt(LocalDateTime.parse("2025-10-02T22:06:05.313240")).build();

        Page<Category> entities = new PageImpl<>(List.of(c1), PageRequest.of(0, 10), 1);

        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entities);

        Page<CategoryResponse> result = service.search(
                (Specification<?>) (root, query, cb) -> cb.conjunction(),
                PageRequest.of(0, 10));

        assertThat(result.getTotalElements(), is(1L));
        assertThat(result.getContent(), hasSize(1));
        CategoryResponse resp = result.getContent().get(0);
        assertThat(resp.getId(), is(1L));
        assertThat(resp.getName(), is("Tecnología"));
        assertThat(resp.getUserId(), is(1L));
        assertThat(resp.getUserName(), is("Administrador General"));
    }
}
