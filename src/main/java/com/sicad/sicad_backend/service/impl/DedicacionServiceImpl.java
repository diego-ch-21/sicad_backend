package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionCreateRequest;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionDetalleResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionUpdateRequest;
import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDedicacionRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDedicacionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DedicacionServiceImpl
        extends CRUDImpl<Dedicacion, Integer>
        implements IDedicacionService {

    private final IDedicacionRepo dedicacionRepo;
    private final IDocenteRepo docenteRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Dedicacion, Integer> getRepo() {
        return dedicacionRepo;
    }

    @Override
    public BaseListReponse<DedicacionDetalleResponse> listar() {
        List<DedicacionDetalleResponse> lista = dedicacionRepo.findByEnabledTrue()
                .stream()
                .map(this::convDedicacionDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.DEDICACION.listado(), lista);
    }

    @Override
    public BaseObjectResponse<DedicacionDetalleResponse> buscar(Integer idDedicacion) {
        Optional<Dedicacion> dedicacionOpt = dedicacionRepo.findByIdAndEnabledTrue(idDedicacion);

        if (dedicacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DEDICACION.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.DEDICACION.encontrado(), convDedicacionDetalle(dedicacionOpt.get()));
    }

    @Override
    public BaseObjectResponse<DedicacionDetalleResponse> buscarPorDocente(Integer idDocente) {
        Optional<Docente> docenteOpt = docenteRepo.findById(idDocente);

        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }

        Dedicacion dedicacion = docenteOpt.get().getDedicacion();
        if (dedicacion == null || !dedicacion.getEnabled()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noTiene(Modulo.DEDICACION), null);
        }

        return new BaseObjectResponse<>(200, Modulo.DEDICACION.encontrado(), convDedicacionDetalle(dedicacion));
    }

    @Override
    public BaseObjectResponse<DedicacionDetalleResponse> registrar(DedicacionCreateRequest request) {

        if (request.getHorasMinLectivas() > request.getHorasMaxLectivas()) {
            return new BaseObjectResponse<>(
                    400,
                    "horasMinLectivas no puede ser mayor que horasMaxLectivas",
                    null
            );
        }

        if (request.getHorasMaxLectivas() > request.getHorasTotales()) {
            return new BaseObjectResponse<>(
                    400,
                    "horasMaxLectivas no puede ser mayor que horasTotales",
                    null
            );
        }
        Dedicacion dedicacion = Dedicacion.builder()
                .nombre(request.getNombre())
                .horasTotales(request.getHorasTotales())
                .horasMaxLectivas(request.getHorasMaxLectivas())
                .horasMinLectivas(request.getHorasMinLectivas())
                .enabled(true)
                .build();

        dedicacionRepo.save(dedicacion);

        return new BaseObjectResponse<>(201, Modulo.DEDICACION.registrado(), convDedicacionDetalle(dedicacion));
    }

    @Override
    public BaseListReponse<DedicacionDetalleResponse> registrarAll(List<DedicacionCreateRequest> requests) {
        List<DedicacionDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (DedicacionCreateRequest request : requests) {
            try {
                BaseObjectResponse<DedicacionDetalleResponse> response = registrar(request);
                if (response.status() == 201 && response.data() != null) {
                    registrados.add(response.data());
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                errorCount++;
            }
        }

        return new BaseListReponse<>(201, Modulo.DEDICACION.resumenAllRegistro(registrados.size(), errorCount), registrados);
    }
    @Override
    public BaseObjectResponse<DedicacionDetalleResponse> actualizar(Integer idDedicacion, DedicacionUpdateRequest request) {
        Optional<Dedicacion> dedicacionOpt = dedicacionRepo.findByIdAndEnabledTrue(idDedicacion);

        if (dedicacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DEDICACION.noEncontrado(), null);
        }

        Dedicacion dedicacion = dedicacionOpt.get();

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            dedicacion.setNombre(request.getNombre());
        }

        if (request.getHorasTotales() != null) {
            dedicacion.setHorasTotales(request.getHorasTotales());
        }

        if (request.getHorasMinLectivas() != null) {
            dedicacion.setHorasMinLectivas(request.getHorasMinLectivas());
        }

        if (request.getHorasMaxLectivas() != null) {
            dedicacion.setHorasMaxLectivas(request.getHorasMaxLectivas());
        }

        if (dedicacion.getHorasMinLectivas() != null && dedicacion.getHorasMaxLectivas() != null) {
            if (dedicacion.getHorasMinLectivas() > dedicacion.getHorasMaxLectivas()) {
                return new BaseObjectResponse<>(400, "horasMinLectivas no puede ser mayor que horasMaxLectivas", null);
            }
        }

        if (dedicacion.getHorasMaxLectivas() != null && dedicacion.getHorasTotales() != null) {
            if (dedicacion.getHorasMaxLectivas() > dedicacion.getHorasTotales()) {
                return new BaseObjectResponse<>(400, "horasMaxLectivas no puede ser mayor que horasTotales", null);
            }
        }

        dedicacionRepo.save(dedicacion);

        return new BaseObjectResponse<>(200, Modulo.DEDICACION.actualizado(), convDedicacionDetalle(dedicacion));
    }


    @Override
    public BaseObjectResponse<String> eliminar(Integer idDedicacion) {
        Optional<Dedicacion> dedicacionOpt = dedicacionRepo.findByIdAndEnabledTrue(idDedicacion);

        if (dedicacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DEDICACION.noEncontrado(), null);
        }

        Dedicacion dedicacion = dedicacionOpt.get();
        dedicacion.setEnabled(false);
        dedicacionRepo.save(dedicacion);

        return new BaseObjectResponse<>(200, Modulo.DEDICACION.eliminado(), null);
    }

    private DedicacionDetalleResponse convDedicacionDetalle(Dedicacion obj) {
        return modelMapper.map(obj, DedicacionDetalleResponse.class);
    }
}
