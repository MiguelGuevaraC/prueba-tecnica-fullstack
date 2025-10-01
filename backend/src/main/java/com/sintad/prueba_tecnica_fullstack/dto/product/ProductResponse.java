package com.sintad.prueba_tecnica_fullstack.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de producto")
public class ProductResponse {

    @Schema(description = "Identificador único del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Gamer")
    private String name;

    @Schema(description = "Descripción del producto", example = "Laptop de alto rendimiento")
    private String description;

    @Schema(description = "Precio del producto", example = "3500.50")
    private BigDecimal price;

    @Schema(description = "Cantidad en stock", example = "10")
    private Integer stock;

    @Schema(description = "ID de la categoría", example = "2")
    private Long categoryId;

    @Schema(description = "Nombre de la categoría", example = "Electrónica")
    private String categoryName;

    @Schema(description = "ID del usuario que creó la categoría")
    private Long userId;

    @Schema(description = "Nombre del usuario que creó la categoría")
    private String userName;

    @Schema(description = "Fecha de creación", example = "2025-09-30T12:00:00")
    private LocalDateTime createdAt;
}
