package com.sintad.prueba_tecnica_fullstack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserResponse;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.IUserService;
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

import static org.hamcrest.Matchers.*;
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
class UserControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private IUserService userService;
    @Mock private FilterSpecBuilder<User> specBuilder;

    @BeforeEach
    void setup() {
        UserController controller = new UserController(userService, specBuilder);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void create_success() throws Exception {
        String validJson = """
        {
          "username": "admin",
          "password": "Secr3t0!",
          "fullName": "Administrador General",
          "role": "ADMIN"
        }
        """;

        UserResponse resp = UserResponse.builder()
                .id(1L)
                .username("admin")
                .fullName("Administrador General")
                .role("ADMIN")
                .build();

        when(userService.create(any(UserRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.fullName").value("Administrador General"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void getById_success() throws Exception {
        UserResponse resp = UserResponse.builder()
                .id(10L)
                .username("user10")
                .fullName("Usuario Diez")
                .role("OTRO")
                .build();

        when(userService.getById(10L)).thenReturn(resp);

        mockMvc.perform(get("/api/users/{id}", 10)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.username").value("user10"))
                .andExpect(jsonPath("$.data.fullName").value("Usuario Diez"))
                .andExpect(jsonPath("$.data.role").value("OTRO"));
    }

    @Test
    void update_success() throws Exception {
        String validJson = """
        {
          "username": "admin",
          "fullName": "Admin General",
          "role": "ADMIN"
        }
        """;

        UserResponse resp = UserResponse.builder()
                .id(5L)
                .username("admin")
                .fullName("Admin General")
                .role("ADMIN")
                .build();

        when(userService.update(eq(5L), any(UserRequest.class))).thenReturn(resp);

        mockMvc.perform(put("/api/users/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.fullName").value("Admin General"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void delete_success() throws Exception {
        doNothing().when(userService).delete(9L);

        mockMvc.perform(delete("/api/users/{id}", 9)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))
                .andExpect(jsonPath("$.data").value("Usuario eliminado correctamente"));
    }

    @Test
    void list_success_withPaginationAndFilters_twoItems() throws Exception {
        Specification<User> spec = (root, query, cb) -> cb.conjunction();
        when(specBuilder.build(eq(User.class), anyMap(), any(MultiValueMap.class))).thenReturn(spec);

        List<UserResponse> items = new ArrayList<>();
        items.add(UserResponse.builder()
                .id(2L)
                .fullName("Usuario Demo")
                .username("usuario")
                .role("OTRO")
                .createdAt(LocalDateTime.parse("2025-10-02T22:06:05.188733"))
                .updatedAt(null)
                .build());
        items.add(UserResponse.builder()
                .id(1L)
                .fullName("Administrador General")
                .username("admin")
                .role("ADMIN")
                .createdAt(LocalDateTime.parse("2025-10-02T22:06:05.041243"))
                .updatedAt(null)
                .build());

        Page<UserResponse> page = new PageImpl<>(items, PageRequest.of(0, 10), 2);
        when(userService.search(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("per_page", "10")
                        .param("page", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"))

                .andExpect(jsonPath("$.data.data", hasSize(2)))
                .andExpect(jsonPath("$.data.data[0].id").value(2))
                .andExpect(jsonPath("$.data.data[0].fullName").value("Usuario Demo"))
                .andExpect(jsonPath("$.data.data[0].username").value("usuario"))
                .andExpect(jsonPath("$.data.data[0].role").value("OTRO"))
                .andExpect(jsonPath("$.data.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data.data[0].updatedAt").value(nullValue()))

                .andExpect(jsonPath("$.data.data[1].id").value(1))
                .andExpect(jsonPath("$.data.data[1].fullName").value("Administrador General"))
                .andExpect(jsonPath("$.data.data[1].username").value("admin"))
                .andExpect(jsonPath("$.data.data[1].role").value("ADMIN"))
                .andExpect(jsonPath("$.data.data[1].createdAt").exists())
                .andExpect(jsonPath("$.data.data[1].updatedAt").value(nullValue()))

                .andExpect(jsonPath("$.data.links.first",
                        anyOf(containsString("/api/users?per_page=10&page=1"),
                              containsString("/api/users?page=1"))))
                .andExpect(jsonPath("$.data.links.last",
                        anyOf(containsString("/api/users?per_page=10&page=1"),
                              containsString("/api/users?page=1"))))
                .andExpect(jsonPath("$.data.links.prev").value(nullValue()))
                .andExpect(jsonPath("$.data.links.next").value(nullValue()))

                .andExpect(jsonPath("$.data.meta.currentPage").value(1))
                .andExpect(jsonPath("$.data.meta.from").value(1))
                .andExpect(jsonPath("$.data.meta.lastPage").value(1))
                .andExpect(jsonPath("$.data.meta.path", containsString("/api/users")))
                .andExpect(jsonPath("$.data.meta.perPage").value(10))
                .andExpect(jsonPath("$.data.meta.to").value(2))
                .andExpect(jsonPath("$.data.meta.total").value(2));
    }
}
