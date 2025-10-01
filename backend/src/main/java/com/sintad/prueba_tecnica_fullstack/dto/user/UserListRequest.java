package com.sintad.prueba_tecnica_fullstack.dto.user;

import com.sintad.prueba_tecnica_fullstack.shared.dto.BaseListRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para listar usuarios con filtros y paginación")
public class UserListRequest extends BaseListRequest {

    @Schema(description = "Filtrar por nombre de usuario", example = "juanperez")
    private String username;

    @Schema(description = "Filtrar por nombre completo", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Filtrar por rol", example = "ADMIN")
    private String role;
}
