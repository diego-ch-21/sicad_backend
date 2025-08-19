package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public GenericObjectResponse<EscuelaDetalleResponse> registrarEscuela(EscuelaCreateRequest request) {

        // 2. Generar código único
        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (escuelaRepo.existsByCodigo(codigo));

        // 3. Crear y guardar Escuela
        Escuela escuela = Escuela.builder()
                .nombre(request.getNombre())
                .codigo(codigo)
                .enabled(true)
                .build();

        escuelaRepo.save(escuela);

        EscuelaDetalleResponse dto = modelMapper.map(escuela, EscuelaDetalleResponse.class);
        return new GenericObjectResponse<>(201, "Escuela registrada exitosamente", dto);
    }
    public GenericReponse<EscuelaDetalleResponse> registrarEscuelaMultiples(List<EscuelaCreateRequest> requests) {
        List<EscuelaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (EscuelaCreateRequest request : requests) {
            GenericObjectResponse<EscuelaDetalleResponse> response = registrarEscuela(request);
            if (response.status() == 201 && response.data() != null) {
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }

        String mensaje = String.format("Cursos registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new GenericReponse<>(201, mensaje,registrados);
    }

    public GenericObjectResponse<EscuelaDetalleResponse> actualizarEscuela(Integer idEscuela, EscuelaUpdateRequest request) {
        // 1. Buscar la escuela
        Escuela escuela = escuelaRepo.findById(idEscuela).orElse(null);
        if (escuela == null) {
            return new GenericObjectResponse<>(404, "Escuela no encontrada", null);
        }

        // 2. Actualizar nombre si viene
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            escuela.setNombre(request.getNombre());
        }


        // 5. Guardar y retornar
        escuelaRepo.save(escuela);
        EscuelaDetalleResponse dto = modelMapper.map(escuela, EscuelaDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Escuela actualizada exitosamente", dto);
    }


    @Override
    public List<Escuela> findByEnabledTrue() {
        return escuelaRepo.findByEnabledTrue();
    }
}
