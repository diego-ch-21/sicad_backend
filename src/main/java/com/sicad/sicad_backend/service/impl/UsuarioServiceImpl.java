package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ISupabaseStorageService;
import com.sicad.sicad_backend.service.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl
        extends CRUDImpl<Usuario, Integer>
        implements IUsuarioService {

    private final IUsuarioRepo usuarioRepo;
    private final ModelMapper modelMapper;
    private final ISupabaseStorageService storageService;

    @Override
    protected IGenericRepo<Usuario, Integer> getRepo() {
        return usuarioRepo;
    }

    @Override
    public BaseListReponse<UsuarioDetalleResponse> listar() {
        List<UsuarioDetalleResponse> response = usuarioRepo.findByEnabledTrue()
                .stream()
                .map(this::convUsuarioDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.USUARIO.listado(), response);
    }


    @Override
    public BaseObjectResponse<UsuarioDetalleResponse> buscar(Integer idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByIdAndEnabledTrue(idUsuario);

        if (usuarioOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.USUARIO.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.USUARIO.encontrado(), convUsuarioDetalle(usuarioOpt.get()));
    }

    // Agregar esta dependencia en UsuarioServiceImpl
// private final ISupabaseStorageService supabaseStorageService;

// Y agregar este método en UsuarioServiceImpl

    @Override
    public BaseObjectResponse<UsuarioDetalleResponse> actualizarUrlPerfil(Integer idUsuario, MultipartFile file) {
        try {
            // 1. Validar que el usuario existe
            Optional<Usuario> usuarioOpt = usuarioRepo.findByIdAndEnabledTrue(idUsuario);
            if (usuarioOpt.isEmpty()) {
                return new BaseObjectResponse(404, Modulo.USUARIO.noEncontrado(),null);
            }

            Usuario usuario = usuarioOpt.get();

            // 2. Validar que el archivo sea una imagen
            if (!storageService.isImage(file)) {
                return new BaseObjectResponse(400, "El archivo debe ser una imagen válida",null);
            }

            // 3. Validar tamaño del archivo (máximo 5MB)
            if (!storageService.isValidFileSize(file, 5)) {
                return new BaseObjectResponse(400, "La imagen no debe superar los 5MB",null);
            }

            if(!storageService.validarRelacionAspecto(file, 1, 1)){
                return new BaseObjectResponse(400, "La imagen no tiene proporción 1:1",null);

            }

            // 4. Si el usuario ya tiene una foto de perfil, eliminarla
            if (usuario.getUrlPerfil() != null && !usuario.getUrlPerfil().isEmpty()) {
                storageService.deleteFile(usuario.getUrlPerfil());
            }

            // 5. Subir la nueva imagen a Supabase Storage (carpeta "img")
            String nuevaUrlPerfil = storageService.uploadFile(file, "img");

            // 6. Actualizar la URL del perfil en la base de datos
            usuario.setUrlPerfil(nuevaUrlPerfil);
            usuarioRepo.save(usuario);

            return new BaseObjectResponse(200, "Foto de perfil actualizada exitosamente",convUsuarioDetalle(usuario));

        } catch (Exception e) {
            return new BaseObjectResponse(500, "Error al actualizar la foto de perfil" ,null);
        }
    }


    
    private UsuarioDetalleResponse convUsuarioDetalle(Usuario obj) {
        return modelMapper.map(obj, UsuarioDetalleResponse.class);
    }
}
