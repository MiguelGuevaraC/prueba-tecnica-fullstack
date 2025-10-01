package com.sintad.prueba_tecnica_fullstack.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Parámetros de paginación y ordenamiento")
public class PageableRequest {

    @Schema(description = "Número de página (1-indexado)", example = "1")
    @Min(1)
    private Integer page = 1;   // ✅ ahora Integer, no int

    @Schema(description = "Cantidad de elementos por página", example = "10")
    @Min(1)
    private Integer perPage = 10; // ✅ ahora Integer, no int

    @Schema(description = "Campo por el cual ordenar", example = "id")
    private String sort = "id";

    @Schema(description = "Dirección de ordenamiento (asc o desc)", example = "asc")
    private String direction = "asc";
}
