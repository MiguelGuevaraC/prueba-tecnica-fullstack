package com.sintad.prueba_tecnica_fullstack.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un producto")
public class ProductRequest {

    @Schema(description = "Nombre del producto", example = "Laptop Gamer")
    @NotBlank @Size(max = 150)
    private String name;

    @Schema(description = "Descripción del producto", example = "Laptop de alto rendimiento")
    private String description;

    @Schema(description = "Precio del producto", example = "3500.50")
    @NotNull @DecimalMin("0.01")
    private BigDecimal price;

    @Schema(description = "Cantidad en stock", example = "10")
    @NotNull @Min(0)
    private Integer stock;


    @Schema(description = "ID de la categoría asociada", example = "2")
    @NotNull
    private Long categoryId;
}
