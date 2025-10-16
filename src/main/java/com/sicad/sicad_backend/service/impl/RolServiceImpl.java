package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.rol.RolDetalleResponse;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IRolService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolServiceImpl
        extends CRUDImpl<Rol, Integer>
        implements IRolService {

    private final IRolRepo rolRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Rol, Integer> getRepo() {
        return rolRepo;
    }


    @Override
    public BaseListReponse<RolDetalleResponse> listar() {
        List<RolDetalleResponse> lista = rolRepo.findByEnabledTrue()
                .stream()
                .map(this::convRolDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.ROL.listado(), lista);
    }

    @Override
    public BaseObjectResponse<RolDetalleResponse> buscar(Integer idRol) {
        Optional<Rol> rolOpt = rolRepo.findByIdAndEnabledTrue(idRol);

        if (rolOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ROL.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.ROL.encontrado(), convRolDetalle(rolOpt.get()));
    }

    private RolDetalleResponse convRolDetalle(Rol obj) {
        return modelMapper.map(obj, RolDetalleResponse.class);
    }
}
