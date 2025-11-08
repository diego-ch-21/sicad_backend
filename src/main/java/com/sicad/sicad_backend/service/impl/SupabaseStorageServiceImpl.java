package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.TipoFile;
import com.sicad.sicad_backend.config.SupabaseStorageConfig;
import com.sicad.sicad_backend.service.interfaces.ISupabaseStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

/**
 * Servicio para gestionar archivos en Supabase Storage
 * Proporciona métodos para subir, eliminar y gestionar archivos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseStorageServiceImpl
    implements ISupabaseStorageService {
    private final SupabaseStorageConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Sube un archivo a Supabase Storage
     * @param file Archivo a subir (MultipartFile de Spring)
     * @param folder "doc" o "img" (folders dentro del bucket)
     * @return URL pública del archivo subido
     * @throws IOException Si hay error al leer el archivo
     * @throws IllegalArgumentException Si el folder no es válido
     * @throws RuntimeException Si falla la subida a Supabase
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        // Validar que el folder sea válido (solo "doc" o "img")
        if (!folder.equals("doc") && !folder.equals("img")) {
            throw new IllegalArgumentException("Folder debe ser 'doc' o 'img'");
        }

        // Generar nombre único para el archivo usando UUID
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Construir URL del endpoint de Supabase Storage
        // Formato: https://tu-proyecto.supabase.co/storage/v1/object/sicad-documents/doc/filename.pdf
        String uploadUrl = String.format("%s/object/%s/%s/%s",
                config.getStorageUrl(),
                config.getBucketName(),
                folder,
                fileName);

        // Configurar headers de la petición HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + config.getSupabaseKey());
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        // Crear request con el contenido del archivo
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        try {
            // Hacer petición POST a Supabase
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Verificar que la respuesta sea exitosa (2xx)
            if (response.getStatusCode().is2xxSuccessful()) {
                // Construir URL pública del archivo
                String publicUrl = config.getPublicUrl(folder, fileName);
                log.info("Archivo subido exitosamente: {}", publicUrl);
                return publicUrl;
            } else {
                throw new RuntimeException("Error al subir archivo: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error al subir archivo a Supabase: {}", e.getMessage(), e);
            throw new RuntimeException("Error al subir archivo: " + e.getMessage());
        }
    }

    /**
     * Elimina un archivo de Supabase Storage
     * @param fileUrl URL pública del archivo a eliminar
     * @return true si se eliminó correctamente, false si falló
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // Extraer el folder y nombre del archivo desde la URL
            String[] parts = extractFolderAndFileName(fileUrl);
            String folder = parts[0];
            String fileName = parts[1];

            // Construir URL para eliminar
            String deleteUrl = String.format("%s/object/%s/%s/%s",
                    config.getStorageUrl(),
                    config.getBucketName(),
                    folder,
                    fileName);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + config.getSupabaseKey());

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // Hacer petición DELETE
            ResponseEntity<String> response = restTemplate.exchange(
                    deleteUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class
            );

            log.info("Archivo eliminado: {}", fileUrl);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Error al eliminar archivo: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Reemplaza un archivo existente por uno nuevo
     * Primero elimina el archivo antiguo y luego sube el nuevo
     * @param oldFileUrl URL del archivo a reemplazar
     * @param newFile Nuevo archivo a subir
     * @param folder Folder donde subir ("doc" o "img")
     * @return URL del nuevo archivo
     * @throws IOException Si hay error al leer el archivo
     */
    public String replaceFile(String oldFileUrl, MultipartFile newFile, String folder) throws IOException {
        // Eliminar archivo antiguo (no lanzamos error si falla)
        deleteFile(oldFileUrl);

        // Subir nuevo archivo
        return uploadFile(newFile, folder);
    }

    /**
     * Extrae el folder y nombre del archivo de una URL pública de Supabase
     * Formato URL: https://proyecto.supabase.co/storage/v1/object/public/sicad-documents/doc/filename.ext
     * @param fileUrl URL completa del archivo
     * @return Array con [folder, fileName]
     */
    private String[] extractFolderAndFileName(String fileUrl) {
        // Dividir la URL por "/"
        String[] urlParts = fileUrl.split("/");
        int length = urlParts.length;

        // Los últimos dos elementos son: folder y filename
        String fileName = urlParts[length - 1];
        String folder = urlParts[length - 2];

        return new String[]{folder, fileName};
    }

    /**
     * Valida si un archivo es una imagen basándose en su tipo MIME
     * @param file Archivo a validar
     * @return true si es imagen (image/*)
     */
    public boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * Valida si un archivo es un documento válido
     * Tipos aceptados: PDF, Word, Excel
     * @param file Archivo a validar
     * @return true si es un documento válido
     */
    public boolean isDocument(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentType.equals("application/vnd.ms-excel") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );
    }

    /**
     * Obtiene la extensión de un archivo
     * @param filename Nombre del archivo
     * @return Extensión con punto (ej: ".pdf") o vacío si no tiene
     */
    public String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    /**
     * Valida el tamaño máximo del archivo
     * @param file Archivo a validar
     * @param maxSizeMB Tamaño máximo en MB
     * @return true si el archivo está dentro del límite
     */
    public boolean isValidFileSize(MultipartFile file, int maxSizeMB) {
        long maxSizeBytes = maxSizeMB * 1024 * 1024;
        return file.getSize() <= maxSizeBytes;
    }
    public boolean validarRelacionAspecto(MultipartFile file, int ratioWidth, int ratioHeight) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return false; // No es imagen válida
            }

            int width = image.getWidth();
            int height = image.getHeight();

            double aspectRatioReal = (double) width / height;
            double aspectRatioEsperada = (double) ratioWidth / ratioHeight;

            // Tolerancia (por pequeñas variaciones)
            double tolerancia = 0.01;

            return Math.abs(aspectRatioReal - aspectRatioEsperada) <= tolerancia;

        } catch (Exception e) {
            return false; // Error al validar => no cumple
        }
    }

    public TipoFile getTipoFile(MultipartFile file) {
        if (file == null || file.getContentType() == null) {
            return TipoFile.UNKNOWN;
        }

        String contentType = file.getContentType();

        // Imagenes
        if (contentType.startsWith("image/")) {
            return TipoFile.IMAGE;
        }

        // PDF
        if (contentType.equals("application/pdf")) {
            return TipoFile.PDF;
        }

        // Excel
        if (contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return TipoFile.EXCEL;
        }

        return TipoFile.UNKNOWN;
    }
}