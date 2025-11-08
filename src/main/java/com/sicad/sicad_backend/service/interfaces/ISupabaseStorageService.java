package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.Enum.TipoFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ISupabaseStorageService {
    // insertar nueva imagen y devolver url
    String uploadFile(MultipartFile file, String folder) throws IOException;
    // eliminar imagen
    boolean deleteFile(String fileUrl);
    // reemplazar imagen
    String replaceFile(String oldFileUrl, MultipartFile newFile, String folder) throws IOException;
    // es una imagen?
    boolean isImage(MultipartFile file);
    // es un documento?
    boolean isDocument(MultipartFile file);
    String getFileExtension(String filename);
    boolean isValidFileSize(MultipartFile file, int maxSizeMB);

    boolean validarRelacionAspecto(MultipartFile file, int ratioWidth, int ratioHeight);

    TipoFile getTipoFile(MultipartFile file);
}
