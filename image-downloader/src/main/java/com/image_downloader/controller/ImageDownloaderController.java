package com.image_downloader.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.image_downloader.model.dto.DownloadRequest;
import com.image_downloader.model.dto.DownloadResponse;
import com.image_downloader.model.dto.ValidationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image Downloader", description = "Operaciones para descargar y validar imágenes de sitios web")
class ImageDownloaderController {

    @PostMapping("/download")
    @Operation(
        summary = "Descargar imágenes de una URL",
        description = "Descarga todas las imágenes encontradas en la página web especificada. " +
                     "Las imágenes se guardan en carpetas organizadas por dominio."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Imágenes descargadas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DownloadResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "URL inválida o no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DownloadResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DownloadResponse.class)
            )
        )
    })
    public ResponseEntity<DownloadResponse> downloadImages(
        @Parameter(
            description = "Datos de la solicitud con la URL a procesar",
            required = true,
            schema = @Schema(implementation = DownloadRequest.class)
        )
        @RequestBody DownloadRequest request
    ) {
        String url = request.getUrl();
        
        if (!isValidUrl(url)) {
            return ResponseEntity.badRequest()
                .body(new DownloadResponse(false, "La URL no es válida", null, new ArrayList<>()));
        }

        if (!urlExists(url)) {
            return ResponseEntity.badRequest()
                .body(new DownloadResponse(false, "La URL no existe", null, new ArrayList<>()));
        }

        try {
            String folderPath = downloadImagesFromUrl(url);
            List<String> downloadedImages = getDownloadedImagesList(folderPath);
            
            return ResponseEntity.ok(new DownloadResponse(
                true, 
                "Imágenes descargadas exitosamente", 
                folderPath,
                downloadedImages
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DownloadResponse(false, "Error: " + e.getMessage(), null, new ArrayList<>()));
        }
    }

    @GetMapping("/validate")
    @Operation(
        summary = "Validar una URL",
        description = "Verifica si una URL es válida y si existe (responde correctamente). " +
                     "Útil para validar URLs antes de intentar descargar imágenes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Validación completada (independientemente del resultado)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ValidationResponse.class)
            )
        )
    })
    public ResponseEntity<ValidationResponse> validateUrl(
        @Parameter(
            description = "URL a validar (debe incluir http:// o https://)",
            required = true,
            example = "https://www.example.com"
        )
        @RequestParam String url
    ) {
        boolean isValid = isValidUrl(url);
        boolean exists = isValid && urlExists(url);
        
        return ResponseEntity.ok(new ValidationResponse(isValid, exists, 
            isValid && exists ? "La URL es válida y existe" : 
            isValid ? "La URL es válida pero no existe" : "La URL no es válida"));
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean urlExists(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            return responseCode >= 200 && responseCode < 400;
        } catch (Exception e) {
            return false;
        }
    }

    private String downloadImagesFromUrl(String url) throws Exception {
        // Crear carpeta única para las imágenes
        String hostName = new URL(url).getHost();
        String uniqueId = UUID.randomUUID().toString();
        Path folderPath = Paths.get("C:", "images", hostName, uniqueId);
        Files.createDirectories(folderPath);

        // Obtener HTML de la página
        Document document = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .get();

        // Buscar todas las imágenes
        Elements imageElements = document.select("img[src]");
        
        for (Element img : imageElements) {
            String imageUrl = img.attr("src");
            
            // Convertir URL relativa a absoluta si es necesario
            if (!imageUrl.startsWith("http")) {
                imageUrl = new URL(new URL(url), imageUrl).toString();
            }

            downloadSingleImage(imageUrl, folderPath.toString());
        }

        return folderPath.toString();
    }

    private void downloadSingleImage(String imageUrl, String folderPath) {
        try {
            URL url = new URL(imageUrl);
            String fileName = Paths.get(url.getPath()).getFileName().toString();
            
            // Si no hay nombre de archivo, generar uno
            if (fileName.isEmpty() || !fileName.contains(".")) {
                fileName = "image_" + System.currentTimeMillis() + ".jpg";
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", 
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(
                     Paths.get(folderPath, fileName).toString())) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                System.out.println("Imagen descargada: " + Paths.get(folderPath, fileName));
            }
        } catch (Exception e) {
            System.err.println("Error descargando la imagen " + imageUrl + ": " + e.getMessage());
        }
    }

    private List<String> getDownloadedImagesList(String folderPath) {
        List<String> imageFiles = new ArrayList<>();
        try {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            imageFiles.add(file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error listando archivos: " + e.getMessage());
        }
        return imageFiles;
    }
}