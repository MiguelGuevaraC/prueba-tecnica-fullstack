package com.sintad.prueba_tecnica_fullstack.controller;

import com.sintad.prueba_tecnica_fullstack.service.seed.CategorySeederService;
import com.sintad.prueba_tecnica_fullstack.service.seed.ProductSeederService;
import com.sintad.prueba_tecnica_fullstack.service.seed.UserSeederService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seed")
@RequiredArgsConstructor
@Tag(name = "Seeder", description = "Endpoints para inicializar datos de prueba en la base de datos")
public class SeederController {

    private final UserSeederService userSeederService;
    private final CategorySeederService categorySeederService;
    private final ProductSeederService productSeederService;

    @Operation(
            summary = "Ejecutar seeders",
            description = """
                Inicializa usuarios, categorías y productos de prueba.  
                Si el parámetro `force` es `true`, borra los datos existentes antes de recrearlos.
                """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Seeders ejecutados correctamente",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno al ejecutar los seeders",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping
    public String runSeed(
            @Parameter(
                    description = "Si es `true`, borra los datos previos y reinicia la base de datos con datos fake",
                    example = "false"
            )
            @RequestParam(defaultValue = "false") boolean force
    ) {

        if (force) {
            productSeederService.deleteAll();
            categorySeederService.deleteAll();
            userSeederService.deleteAll();
        }

        userSeederService.seed(false);
        categorySeederService.seed(false);
        productSeederService.seed(false);

        return "Seeders ejecutados correctamente" +
                (force ? " (modo FORCE, datos reseteados)" : "");
    }
}
