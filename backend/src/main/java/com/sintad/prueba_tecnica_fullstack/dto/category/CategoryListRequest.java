package com.sintad.prueba_tecnica_fullstack.dto.category;

import com.sintad.prueba_tecnica_fullstack.shared.dto.BaseListRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para listar categorías con filtros y paginación")
public class CategoryListRequest extends BaseListRequest {

    @Schema(description = "Filtrar por nombre de la categoría", example = "Electrónica")
    private String name;
}
