package com.sintad.prueba_tecnica_fullstack.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de categoría")
public class CategoryResponse {

    @Schema(description = "Identificador único de la categoría", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "Electrónica")
    private String name;

    @Schema(description = "Descripción de la categoría", example = "Dispositivos electrónicos")
    private String description;

    @Schema(description = "ID del usuario que creó la categoría")
    private Long userId;

    @Schema(description = "Nombre del usuario que creó la categoría")
    private String userName;

    @Schema(description = "Fecha de creación", example = "2025-09-30T12:00:00")
    private LocalDateTime createdAt;
}
