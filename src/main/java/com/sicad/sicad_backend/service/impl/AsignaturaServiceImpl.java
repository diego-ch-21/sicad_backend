package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignaturaService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sicad.sicad_backend.Enum.Message.CODIGO_EXISTENTE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsignaturaServiceImpl
    extends CRUDImpl<Asignatura, Integer>
    implements IAsignaturaService {

    private final IAsignaturaRepo asignaturaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Asignatura, Integer> getRepo() {
        return asignaturaRepo;
    }


    @Override
    public BaseListReponse<AsignaturaDetalleResponse> listar() {
        List<AsignaturaDetalleResponse> lista = asignaturaRepo.findByEnabledTrue()
                .stream()
                .map(this::convAsignaturaDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.ASIGNATURA.listado(), lista);    }

    @Override
    public BaseObjectResponse<AsignaturaDetalleResponse> buscar(Integer idAsignatura) {
        Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(idAsignatura);

        if (asignaturaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.ASIGNATURA.encontrado(), convAsignaturaDetalle(asignaturaOpt.get()));
    }

    @Override
    public BaseObjectResponse<AsignaturaDetalleResponse> registrar(AsignaturaCreateRequest request) {

        String codigoFinal;

        // Si el usuario no envía un código, genera uno automáticamente
        if (request.getCodigo() == null || request.getCodigo().isEmpty()) {
            codigoFinal = generarCodigoUnico();
        }
        // Si lo envía, valida que no exista
        else if (asignaturaRepo.existsByCodigoAndEnabled(request.getCodigo())) {
            return new BaseObjectResponse<>(409, CODIGO_EXISTENTE.toString(), null);
        }
        else {
            codigoFinal = request.getCodigo(); // usa el que mandó
        }

        // Crear y guardar la asignatura
        Asignatura asignatura = Asignatura.builder()
                .nombre(request.getNombre())
                .codigo(codigoFinal)
                .enabled(true)
                .build();
        asignaturaRepo.save(asignatura);

        // Retornar respuesta
        return new BaseObjectResponse<>(201, Modulo.ASIGNATURA.registrado(), convAsignaturaDetalle(asignatura));
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            String numero = String.format("%06d", (int) (Math.random() * 1000000));
            codigo = "PR" + numero;
        } while (asignaturaRepo.existsByCodigoAndEnabled(codigo));
        return codigo;
    }



    @Override
    public BaseListReponse<AsignaturaDetalleResponse> registrarAll(List<AsignaturaCreateRequest> requests) {
        List<AsignaturaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (AsignaturaCreateRequest request : requests) {
            try {
                BaseObjectResponse<AsignaturaDetalleResponse> response = registrar(request);
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
        return new BaseListReponse<>(201,Modulo.ALGORITMO.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }


    @Override
    public BaseObjectResponse<AsignaturaDetalleResponse> actualizar(Integer idAsignatura, AsignaturaUpdateRequest request) {
        Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(idAsignatura);

        if (asignaturaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
        }

        Asignatura asignatura = asignaturaOpt.get();

        // Actualizar código (si se envía y es válido)
        String nuevoCodigo = request.getCodigo();
        if (nuevoCodigo != null && !nuevoCodigo.isBlank()) {
            if (!nuevoCodigo.equals(asignatura.getCodigo()) && asignaturaRepo.existsByCodigoAndEnabled(nuevoCodigo)) {
                return new BaseObjectResponse<>(409, CODIGO_EXISTENTE.toString(), null);
            }
            asignatura.setCodigo(nuevoCodigo);
        }

        // Actualizar nombre (si se envía)
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            asignatura.setNombre(request.getNombre());
        }

        asignaturaRepo.save(asignatura);

        return new BaseObjectResponse<>(200, Modulo.ASIGNATURA.actualizado(), convAsignaturaDetalle(asignatura));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idAsignatura) {
        Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(idAsignatura);

        if (asignaturaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
        }
        Asignatura asignatura = asignaturaOpt.get();
        asignatura.setEnabled(false);
        asignaturaRepo.save(asignatura);
        return new BaseObjectResponse<>(200, Modulo.ASIGNATURA.eliminado(), null);
    }
    private AsignaturaDetalleResponse convAsignaturaDetalle(Asignatura obj) {
        return modelMapper.map(obj, AsignaturaDetalleResponse.class);
    }
}
