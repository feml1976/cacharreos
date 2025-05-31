package com.image_downloader.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// DTOs para las respuestas
@Schema(description = "Solicitud para descargar imágenes")
public class DownloadRequest {

    @Schema(
        description = "URL del sitio web del cual descargar imágenes",
        example = "https://www.example.com",
        required = true
    )
    private String url;
    
    public DownloadRequest() {}
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
