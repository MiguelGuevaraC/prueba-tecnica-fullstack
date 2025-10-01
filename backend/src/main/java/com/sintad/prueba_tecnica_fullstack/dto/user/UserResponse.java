package com.sintad.prueba_tecnica_fullstack.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de usuario")
public class UserResponse {

    @Schema(description = "Identificador único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Nombre de usuario", example = "juanperez")
    private String username;

    @Schema(description = "Rol asignado", example = "ADMIN")
    private String role;

    @Schema(description = "Fecha de creación", example = "2025-09-30T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Última fecha de actualización", example = "2025-09-30T15:30:00")
    private LocalDateTime updatedAt;
}
