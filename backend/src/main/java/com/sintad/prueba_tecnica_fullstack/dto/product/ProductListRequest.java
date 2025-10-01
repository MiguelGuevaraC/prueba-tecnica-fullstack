package com.sintad.prueba_tecnica_fullstack.dto.product;

import com.sintad.prueba_tecnica_fullstack.shared.dto.BaseListRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para listar productos con paginación y filtros")
public class ProductListRequest extends BaseListRequest {

    @Schema(description = "Filtrar por nombre del producto", example = "Laptop Gamer")
    private String name;

    @Schema(description = "Filtrar por rango de precio (ej: '1000,2000')", example = "1000,2000")
    private String price;

    @Schema(description = "Filtrar por nombre de categoría", example = "Electrónica")
    private String categoryName;

    @Schema(description = "Filtrar por nombre de usuario creador", example = "juanperez")
    private String userUsername;
}
