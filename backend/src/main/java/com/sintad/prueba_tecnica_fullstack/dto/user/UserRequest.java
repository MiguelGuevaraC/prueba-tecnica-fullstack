package com.sintad.prueba_tecnica_fullstack.dto.user;

import com.sintad.prueba_tecnica_fullstack.shared.validation.OnCreate;
import com.sintad.prueba_tecnica_fullstack.shared.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar usuario")
public class UserRequest {

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    @NotBlank(groups = OnCreate.class)
    @Size(max = 100)
    private String fullName;

    @Schema(description = "Nombre de usuario único para login", example = "juanperez")
    @NotBlank(groups = OnCreate.class)
    @Size(max = 50)
    private String username;

    @Schema(description = "Contraseña en texto plano (será encriptada)", example = "12345678")
    @NotBlank(groups = OnCreate.class)
    @Size(min = 6)
    private String password;

    @Schema(description = "Rol del usuario (ejemplo: ADMIN, USER)", example = "ADMIN")
    @NotBlank(groups = OnCreate.class)
    @Size(max = 50)
    private String role;
}
