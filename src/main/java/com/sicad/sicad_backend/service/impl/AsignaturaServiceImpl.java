package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignaturaService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public BaseObjectResponse<AsignaturaDetalleResponse> registrarAsignatura(AsignaturaCreateRequest request) {
        // Generar código único
        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (asignaturaRepo.existsByCodigo(codigo));

        // Crear y guardar asignatura
        Asignatura asignatura = Asignatura.builder()
                .codigo(codigo)
                .nombre(request.getNombre())
                .enabled(true)
                .build();

        asignaturaRepo.save(asignatura);

        AsignaturaDetalleResponse dto = modelMapper.map(asignatura, AsignaturaDetalleResponse.class);
        return new BaseObjectResponse<>(201, "Asignatura registrada exitosamente", dto);
    }

    public BaseObjectResponse<AsignaturaDetalleResponse> actualizarAsignatura(Integer id, AsignaturaUpdateRequest request) {
        Asignatura asignatura = asignaturaRepo.findById(id).orElse(null);
        if (asignatura == null) {
            return new BaseObjectResponse<>(404, "Asignatura no encontrada", null);
        }

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            asignatura.setNombre(request.getNombre());
        }

        asignaturaRepo.save(asignatura);

        AsignaturaDetalleResponse dto = modelMapper.map(asignatura, AsignaturaDetalleResponse.class);
        return new BaseObjectResponse<>(200, "Asignatura actualizada exitosamente", dto);
    }
    public BaseListReponse<AsignaturaDetalleResponse> registrarAsignaturasMultiples(List<AsignaturaCreateRequest> requests) {
        List<AsignaturaDetalleResponse> registrados = new ArrayList<>();
        int errores = 0;
        for (AsignaturaCreateRequest request : requests) {
            BaseObjectResponse<AsignaturaDetalleResponse> response = registrarAsignatura(request);
            if (response.status() == 201 && response.data() != null) {
                registrados.add(response.data());
            } else {
                errores++;
            }
        }

        String mensaje = String.format("Asignaturas registradas: %d. Fallidos: %d.", registrados.size(), errores);
        return new BaseListReponse<>(201, mensaje, registrados.isEmpty() ? null : registrados);
    }
    public BaseObjectResponse<String> eliminarAsignatura(Integer idAsignatura) {
        if (idAsignatura == null) {
            return new BaseObjectResponse<>(400, "idAsignatura no proporcionado", null);
        }

        Asignatura asignatura = asignaturaRepo.findById(idAsignatura).orElse(null);
        if (asignatura == null) {
            return new BaseObjectResponse<>(404, "Asignatura  no encontrado", null);
        }

        asignatura.setEnabled(false);
        asignaturaRepo.save(asignatura);
        return new BaseObjectResponse<>(200, "se elimino la asignatura exitosamente", null);
    }

    @Override
    public List<Asignatura> findByEnabledTrue() {
        return asignaturaRepo.findByEnabledTrue();
    }
}
