package com.sintad.prueba_tecnica_fullstack.shared.exception;

import com.sintad.prueba_tecnica_fullstack.shared.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Manejo de errores de validación @Valid
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                                .stream()
                                .collect(Collectors.toMap(
                                                FieldError::getField,
                                                FieldError::getDefaultMessage,
                                                (a, b) -> a // si hay conflicto, toma el primero
                                ));

                return ResponseEntity.badRequest().body(
                                ApiResponse.<Map<String, String>>builder()
                                                .success(false)
                                                .message("Errores de validación")
                                                .data(errors)
                                                .build());
        }

        /**
         * Manejo de entidades no encontradas (ej: repositorios, servicios)
         */
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ApiResponse<String>> handleEntityNotFound(EntityNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                ApiResponse.<String>builder()
                                                .success(false)
                                                .message(ex.getMessage() != null ? ex.getMessage()
                                                                : "Recurso no encontrado")
                                                .build());
        }

        /**
         * Manejo de rutas inexistentes
         */
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ApiResponse<String>> handleNoResourceFound(NoResourceFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                ApiResponse.<String>builder()
                                                .success(false)
                                                .message("La ruta solicitada no existe")
                                                .build());
        }

        /**
         * Manejo genérico de errores 500
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
                // Log detallado (para debug)
                ex.printStackTrace();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                ApiResponse.<String>builder()
                                                .success(false)
                                                .message("Ocurrió un error interno en el servidor. Intente más tarde.")
                                                .build());
        }

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "status", HttpStatus.NOT_FOUND.value(),
                                                "error", "Not Found",
                                                "message", ex.getMessage()));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "status", HttpStatus.BAD_REQUEST.value(),
                                                "error", "Bad Request",
                                                "message", ex.getMessage()));
        }
}
