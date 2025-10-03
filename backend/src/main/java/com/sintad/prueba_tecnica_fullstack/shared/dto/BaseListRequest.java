package com.sintad.prueba_tecnica_fullstack.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseListRequest {

    @Schema(description = "Número de página", example = "1")
    private Integer page = 1;

    @Schema(description = "Cantidad de registros por página", example = "10")
    private Integer per_page = 10;

    @Schema(description = "Campo de ordenamiento", example = "id")
    private String sort = "id";

    @Schema(description = "Dirección de ordenamiento (asc o desc)", example = "asc")
    private String direction = "desc";
}
