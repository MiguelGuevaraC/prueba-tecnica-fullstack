package com.sintad.prueba_tecnica_fullstack.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear una categoría")
public class CategoryRequest {

    @Schema(description = "Nombre único de la categoría", example = "Electrónica")
    @NotBlank @Size(max = 100)
    private String name;

    @Schema(description = "Descripción opcional de la categoría", example = "Dispositivos electrónicos y gadgets")
    private String description;

}
