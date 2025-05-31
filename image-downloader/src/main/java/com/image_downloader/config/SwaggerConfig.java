package com.image_downloader.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Image Downloader API")
                        .version("1.0.0")
                        .description("""
                            ## 🖼️ Image Downloader API
                            
                            Esta API permite descargar imágenes de sitios web de manera automática.
                            
                            ### 🚀 Características principales:
                            - **Validación de URLs**: Verifica si una URL es válida y accesible
                            - **Descarga masiva**: Descarga todas las imágenes encontradas en una página
                            - **Organización automática**: Las imágenes se organizan por dominio en carpetas únicas
                            - **Manejo de errores**: Reportes detallados de errores durante el proceso
                            
                            ### 📋 Cómo usar:
                            1. **Validar URL**: Usa `/api/images/validate` para verificar si una URL es válida
                            2. **Descargar imágenes**: Usa `/api/images/download` para descargar todas las imágenes
                            
                            ### 💡 Ejemplos de URLs válidas:
                            - `https://www.example.com`
                            - `http://localhost:3000`
                            - `https://unsplash.com`
                            
                            ### ⚠️ Consideraciones:
                            - Las imágenes se guardan en `C:\\images\\[dominio]\\[uuid]`
                            - Se respetan los robots.txt y términos de uso de los sitios
                            - Timeout de conexión: 5 segundos
                            """)
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@imagedownloader.com")
                                .url("https://github.com/tu-usuario/image-downloader"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.imagedownloader.com")
                                .description("Servidor de producción")
                ));
    }
}
