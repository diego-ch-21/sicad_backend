package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoCreateRequest;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.Algoritmo;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAlgoritmoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAlgoritmoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlgoritmoServiceImpl
        extends CRUDImpl<Algoritmo, Integer>
        implements IAlgoritmoService {

    private final IAlgoritmoRepo algoritmoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Algoritmo, Integer> getRepo() {
        return null;
    }

    @Override
    public List<Algoritmo> findByEnabledTrue() {
        return algoritmoRepo.findByEnabledTrue();
    }

    public GenericObjectResponse<AlgoritmoDetalleResponse> registrarAlgoritmo(AlgoritmoCreateRequest request) {
        // Construir entidad desde el request
        Algoritmo algoritmo = Algoritmo.builder()
                .poblacion(request.poblacion())
                .generacionGa(request.generacionGa())
                .probCruzamientos(request.probCruzamientos())
                .probMutacion(request.probMutacion())
                .elitismo(request.elitismo())
                .enjambrePso(request.enjambrePso())
                .iteracionesPso(request.iteracionesPso())
                .inerciaInicial(request.inerciaInicial())
                .inerciaFinal(request.inerciaFinal())
                .cUno(request.cUno())
                .cDos(request.cDos())
                .velocidadMaxima(request.velocidadMaxima())
                .cicloHibridos(request.cicloHibridos())
                .principal(false)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        algoritmoRepo.save(algoritmo);

        // Convertir a DTO
        AlgoritmoDetalleResponse dto = modelMapper.map(algoritmo, AlgoritmoDetalleResponse.class);

        return new GenericObjectResponse<>(201, "Algoritmo registrado exitosamente", dto);
    }
    public GenericObjectResponse<AlgoritmoDetalleResponse> actualizarAlgoritmo(Integer id, AlgoritmoUpdateRequest request) {
        Algoritmo algoritmo = algoritmoRepo.findById(id).orElse(null);
        if (algoritmo == null) {
            return new GenericObjectResponse<>(404, "Algoritmo no encontrado", null);
        }

        // Actualizar solo si no son null
        if (request.poblacion() != null) algoritmo.setPoblacion(request.poblacion());
        if (request.generacionGa() != null) algoritmo.setGeneracionGa(request.generacionGa());
        algoritmo.setProbCruzamientos(request.probCruzamientos());
        algoritmo.setProbMutacion(request.probMutacion());
        algoritmo.setElitismo(request.elitismo());
        if (request.enjambrePso() != null) algoritmo.setEnjambrePso(request.enjambrePso());
        if (request.iteracionesPso() != null) algoritmo.setIteracionesPso(request.iteracionesPso());
        algoritmo.setInerciaInicial(request.inerciaInicial());
        algoritmo.setInerciaFinal(request.inerciaFinal());
        algoritmo.setCUno(request.cUno());
        algoritmo.setCDos(request.cDos());
        algoritmo.setVelocidadMaxima(request.velocidadMaxima());
        if (request.cicloHibridos() != null) algoritmo.setCicloHibridos(request.cicloHibridos());

        algoritmoRepo.save(algoritmo);

        AlgoritmoDetalleResponse dto = modelMapper.map(algoritmo, AlgoritmoDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Algoritmo actualizado exitosamente", dto);
    }
    public GenericReponse<AlgoritmoDetalleResponse> registrarAlgoritmosMultiples(List<AlgoritmoCreateRequest> requests) {
        List<AlgoritmoDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (AlgoritmoCreateRequest request : requests) {
            try {
                GenericObjectResponse<AlgoritmoDetalleResponse> response = registrarAlgoritmo(request);
                if (response.status() == 201 && response.data() != null) {
                    registrados.add(response.data());
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                errorCount++;
            }
        }

        String mensaje = String.format("Algoritmos registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new GenericReponse<>(201, mensaje, registrados);
    }
    @Transactional
    public GenericObjectResponse<AlgoritmoDetalleResponse> asignarPrincipal(Integer idAlgoritmo) {
        // Paso 1: verificar existencia
        Optional<Algoritmo> optAlgoritmo = algoritmoRepo.findById(idAlgoritmo);

        if (optAlgoritmo.isEmpty()) {
            return new GenericObjectResponse<>(404, "El algoritmo seleccionado no existe", null);
        }

        Algoritmo algoritmo = optAlgoritmo.get();


        // Paso 2: buscar si ya existe un principal habilitado
        Optional<Algoritmo> principalActualOpt = algoritmoRepo.findByPrincipalTrue();

        if (principalActualOpt.isPresent()) {
            Algoritmo principalActual = principalActualOpt.get();

            // Paso 3: verificar si el mismo ya está marcado
            if (principalActual.getIdAlgoritmo().equals(idAlgoritmo)) {
                AlgoritmoDetalleResponse dto = modelMapper.map(algoritmo, AlgoritmoDetalleResponse.class);
                return new GenericObjectResponse<>(200,
                        "Este algoritmo ya está seleccionado como principal", dto);
            }

            // Paso 4: si hay un principal distinto → desmarcarlo
            principalActual.setPrincipal(false);
            algoritmoRepo.save(principalActual);
        }

        // Paso 5: marcar el nuevo como principal
        algoritmo.setPrincipal(true);
        algoritmoRepo.save(algoritmo);

        AlgoritmoDetalleResponse dto = modelMapper.map(algoritmo, AlgoritmoDetalleResponse.class);
        return new GenericObjectResponse<>(201,
                "Algoritmo asignado como principal exitosamente", dto);
    }

    @Transactional
    public GenericObjectResponse<AlgoritmoDetalleResponse> eliminarAlgoritmo(Integer idAlgoritmo) {
        // 1. Buscar el algoritmo
        Algoritmo algoritmo = algoritmoRepo.findById(idAlgoritmo).orElse(null);
        if (algoritmo == null) {
            return new GenericObjectResponse<>(404, "Algoritmo no encontrado", null);
        }

        // 2. Marcar como eliminado
        algoritmo.setEnabled(false);

        // 3. Si era principal → desmarcar
        if (algoritmo.isPrincipal()) {
            algoritmo.setPrincipal(false);
        }

        // 4. Guardar cambios
        algoritmoRepo.save(algoritmo);

        // 5. Mapear a DTO y responder
        AlgoritmoDetalleResponse dto = modelMapper.map(algoritmo, AlgoritmoDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Algoritmo eliminado correctamente", dto);
    }




}
