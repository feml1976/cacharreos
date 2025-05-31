package com.image_downloader.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del proceso de descarga de imágenes")
public class DownloadResponse {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean success;
    
    @Schema(description = "Mensaje descriptivo del resultado", example = "Imágenes descargadas exitosamente")
    private String message;
    
    @Schema(description = "Ruta donde se guardaron las imágenes", example = "C:\\images\\example.com\\uuid-123")
    private String folderPath;
    
    @Schema(description = "Lista de nombres de archivos de imágenes descargadas")
    private List<String> downloadedImages;
    
    public DownloadResponse(boolean success, String message, String folderPath, List<String> downloadedImages) {
        this.success = success;
        this.message = message;
        this.folderPath = folderPath;
        this.downloadedImages = downloadedImages;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getFolderPath() { return folderPath; }
    public List<String> getDownloadedImages() { return downloadedImages; }
}
