package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl
        extends CRUDImpl<Usuario, Integer>
        implements IUsuarioService {

    private final IUsuarioRepo usuarioRepo;
    private final ModelMapper modelMapper;

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
    
    private UsuarioDetalleResponse convUsuarioDetalle(Usuario obj) {
        return modelMapper.map(obj, UsuarioDetalleResponse.class);
    }
}
