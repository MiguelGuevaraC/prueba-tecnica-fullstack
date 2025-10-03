package com.sintad.prueba_tecnica_fullstack.controller;

import com.sintad.prueba_tecnica_fullstack.dto.user.UserRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserListRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserResponse;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.IUserService;
import com.sintad.prueba_tecnica_fullstack.shared.dto.ApiResponse;
import com.sintad.prueba_tecnica_fullstack.shared.dto.PageResponse;
import com.sintad.prueba_tecnica_fullstack.shared.filter.FilterSpecBuilder;
import com.sintad.prueba_tecnica_fullstack.shared.util.PageableUtil;
import com.sintad.prueba_tecnica_fullstack.shared.validation.OnCreate;
import com.sintad.prueba_tecnica_fullstack.shared.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final FilterSpecBuilder<User> specBuilder;

    @PostMapping
    @Operation(summary = "Crear usuario")
    public ApiResponse<UserResponse> create(@Validated(OnCreate.class) @RequestBody UserRequest request) {
        return ApiResponse.ok(userService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ApiResponse<UserResponse> update(@PathVariable Long id,
                                            @Validated(OnUpdate.class) @RequestBody UserRequest request) {
        return ApiResponse.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ApiResponse<String> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok("Usuario eliminado correctamente");
    }

    @GetMapping
    @Operation(summary = "Listar usuarios con paginación y filtros dinámicos")
    public ApiResponse<PageResponse<UserResponse>> list(
            @ParameterObject UserListRequest listRequest,
            @Parameter(hidden = true) @RequestParam MultiValueMap<String, String> params,
            HttpServletRequest request) {

        Specification<User> spec = specBuilder.build(User.class, User.ALLOWED_FILTERS, params);
        Pageable pageable = PageableUtil.fromListRequest(listRequest);

        Page<UserResponse> page = userService.search(spec, pageable);
        return ApiResponse.ok(PageResponse.from(page, request));
    }
}
