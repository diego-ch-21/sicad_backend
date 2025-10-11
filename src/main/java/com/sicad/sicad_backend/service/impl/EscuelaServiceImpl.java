package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaUpdateRequest;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IEscuelaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IEscuelaService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class EscuelaServiceImpl
        extends CRUDImpl<Escuela, Integer>
        implements IEscuelaService {

    private final IEscuelaRepo escuelaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Escuela, Integer> getRepo() {
        return escuelaRepo;
    }

    @Override
    public BaseListReponse<EscuelaDetalleResponse> listar() {
        List<EscuelaDetalleResponse> lista = escuelaRepo.findByEnabledTrue()
                .stream()
                .map(this::convEscuelaDetalle)
                .toList();

            return new BaseListReponse<>(200,Modulo.ESCUELA.listado(), lista);
    }

    @Override
    public BaseObjectResponse<EscuelaDetalleResponse> buscar(Integer idEscuela) {
        Optional<Escuela> escuelaOpt = escuelaRepo.findByIdAndEnabledTrue(idEscuela);

        if (escuelaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }

        EscuelaDetalleResponse response = convEscuelaDetalle(escuelaOpt.get());
        return new BaseObjectResponse<>(200, Modulo.ESCUELA.encontrado(), response);
    }

    @Override
    public BaseObjectResponse<EscuelaDetalleResponse> registrar(EscuelaCreateRequest request) {

        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (escuelaRepo.existsByCodigo(codigo));

        Escuela escuela = Escuela.builder()
                .nombre(request.getNombre())
                .codigo(codigo)
                .enabled(true)
                .build();
        escuelaRepo.save(escuela);

        EscuelaDetalleResponse response = convEscuelaDetalle(escuela);
        return new BaseObjectResponse<>(201, Modulo.ESCUELA.registrado(), response);
    }

    @Override
    public BaseListReponse<EscuelaDetalleResponse> registrarAll(List<EscuelaCreateRequest> requests) {
        List<EscuelaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (EscuelaCreateRequest request : requests) {
            try {
                BaseObjectResponse<EscuelaDetalleResponse> response = registrar(request);
                if (response.status() == 201 && response.data() != null) {
                    registrados.add(response.data());
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorCount++;
            }
        }
        return new BaseListReponse<>(201,Modulo.ESCUELA.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<EscuelaDetalleResponse> actualizar(Integer idEscuela, EscuelaUpdateRequest request) {
        Optional<Escuela> escuelaOpt = escuelaRepo.findByIdAndEnabledTrue(idEscuela);

        if (escuelaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }
        Escuela escuela = escuelaOpt.get();
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            escuela.setNombre(request.getNombre());
        }
        escuelaRepo.save(escuela);
        EscuelaDetalleResponse response = convEscuelaDetalle(escuela);
        return new BaseObjectResponse<>(200, Modulo.ESCUELA.actualizado(), response);
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idEscuela) {
        Optional<Escuela> escuelaOpt = escuelaRepo.findByIdAndEnabledTrue(idEscuela);

        if (escuelaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }
        Escuela escuela = escuelaOpt.get();
        escuela.setEnabled(false);
        escuelaRepo.save(escuela);
        return new BaseObjectResponse<>(200, Modulo.ESCUELA.eliminado(), null);
    }

    private EscuelaDetalleResponse convEscuelaDetalle(Escuela obj) {
        return modelMapper.map(obj, EscuelaDetalleResponse.class);
    }

}
