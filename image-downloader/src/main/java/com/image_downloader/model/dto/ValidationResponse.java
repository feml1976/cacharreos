package com.image_downloader.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de validación de URL")
public class ValidationResponse {

    @Schema(description = "Indica si la URL tiene formato válido", example = "true")
    private boolean isValid;
    
    @Schema(description = "Indica si la URL existe y responde", example = "true")
    private boolean exists;
    
    @Schema(description = "Mensaje descriptivo del resultado", example = "La URL es válida y existe")
    private String message;
    
    public ValidationResponse(boolean isValid, boolean exists, String message) {
        this.isValid = isValid;
        this.exists = exists;
        this.message = message;
    }
    
    // Getters
    public boolean isValid() { return isValid; }
    public boolean exists() { return exists; }
    public String getMessage() { return message; }

}
