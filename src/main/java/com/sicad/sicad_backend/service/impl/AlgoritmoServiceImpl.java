package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoCreateRequest;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaCreateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaDetalleResponse;
import com.sicad.sicad_backend.model.Algoritmo;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAlgoritmoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAlgoritmoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlgoritmoServiceImpl
        extends CRUDImpl<Algoritmo, Integer>
        implements IAlgoritmoService {

    private final IAlgoritmoRepo algoritmoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Algoritmo, Integer> getRepo() {
        return algoritmoRepo;
    }

    @Override
    public BaseListReponse<AlgoritmoDetalleResponse> listar() {
        List<AlgoritmoDetalleResponse> lista = algoritmoRepo.findByEnabledTrue()
                .stream()
                .map(this::convAlgoritmoDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.ALGORITMO.listado(), lista);
    }

    @Override
    public BaseObjectResponse<AlgoritmoDetalleResponse> buscar(Integer idAlgoritmo) {
        Optional<Algoritmo> algoritmoOpt = algoritmoRepo.findByIdAndEnabledTrue(idAlgoritmo);

        if (algoritmoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ALGORITMO.noEncontrado(), null);
        }
        Algoritmo algoritmo = algoritmoOpt.get();

        return new BaseObjectResponse<>(200, Modulo.ALGORITMO.encontrado(), convAlgoritmoDetalle(algoritmo));
    }

    @Override
    public BaseObjectResponse<AlgoritmoDetalleResponse> registrar(AlgoritmoCreateRequest request) {
        Algoritmo algoritmo = Algoritmo.builder()
                .poblacion(request.getPoblacion())
                .generacionGa(request.getGeneracionGa())
                .probCruzamientos(request.getProbCruzamientos())
                .probMutacion(request.getProbMutacion())
                .elitismo(request.getElitismo())
                .enjambrePso(request.getEnjambrePso())
                .iteracionesPso(request.getIteracionesPso())
                .inerciaInicial(request.getInerciaInicial())
                .inerciaFinal(request.getInerciaFinal())
                .cUno(request.getCUno())
                .cDos(request.getCDos())
                .velocidadMaxima(request.getVelocidadMaxima())
                .cicloHibridos(request.getCicloHibridos())
                .principal(false)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();
        algoritmoRepo.save(algoritmo);

        return new BaseObjectResponse<>(201, Modulo.ALGORITMO.registrado(), convAlgoritmoDetalle(algoritmo));
    }

    @Override
    public BaseListReponse<AlgoritmoDetalleResponse> registrarAll(List<AlgoritmoCreateRequest> requests) {
        List<AlgoritmoDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (AlgoritmoCreateRequest request : requests) {
            try {
                BaseObjectResponse<AlgoritmoDetalleResponse> response = registrar(request);
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
    public BaseObjectResponse<AlgoritmoDetalleResponse> actualizar(Integer idAlgoritmo, AlgoritmoUpdateRequest request) {
        Optional<Algoritmo> optAlgoritmo = algoritmoRepo.findByIdAndEnabledTrue(idAlgoritmo);
        if (optAlgoritmo.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ALGORITMO.noEncontrado(), null);
        }
        Algoritmo algoritmo = optAlgoritmo.get();

        if (request.getPoblacion() != null) algoritmo.setPoblacion(request.getPoblacion());
        if (request.getGeneracionGa() != null) algoritmo.setGeneracionGa(request.getGeneracionGa());
        algoritmo.setProbCruzamientos(request.getProbCruzamientos());
        algoritmo.setProbMutacion(request.getProbMutacion());
        algoritmo.setElitismo(request.getElitismo());
        if (request.getEnjambrePso() != null) algoritmo.setEnjambrePso(request.getEnjambrePso());
        if (request.getIteracionesPso() != null) algoritmo.setIteracionesPso(request.getIteracionesPso());
        algoritmo.setInerciaInicial(request.getInerciaInicial());
        algoritmo.setInerciaFinal(request.getInerciaFinal());
        algoritmo.setCUno(request.getCUno());
        algoritmo.setCDos(request.getCDos());
        algoritmo.setVelocidadMaxima(request.getVelocidadMaxima());
        if (request.getCicloHibridos() != null) algoritmo.setCicloHibridos(request.getCicloHibridos());
        algoritmoRepo.save(algoritmo);

        return new BaseObjectResponse<>(200, Modulo.ALGORITMO.actualizado(),  convAlgoritmoDetalle(algoritmo));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idAlgoritmo) {
        Optional<Algoritmo> optAlgoritmo = algoritmoRepo.findByIdAndEnabledTrue(idAlgoritmo);
        if (optAlgoritmo.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ALGORITMO.noEncontrado(), null);
        }
        Algoritmo algoritmo = optAlgoritmo.get();
        algoritmo.setEnabled(false);
        algoritmo.setPrincipal(false);
        algoritmoRepo.save(algoritmo);

        return new BaseObjectResponse<>(200, Modulo.ALGORITMO.eliminado(), null);
    }
    @Override
    public BaseObjectResponse<AlgoritmoDetalleResponse> asignarPrincipal(Integer idAlgoritmo) {
        Optional<Algoritmo> optAlgoritmo = algoritmoRepo.findByIdAndEnabledTrue(idAlgoritmo);
        if (optAlgoritmo.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ALGORITMO.noEncontrado(), null);
        }
        Algoritmo algoritmo = optAlgoritmo.get();
        if(algoritmo.isPrincipal()){
            return new BaseObjectResponse<>(200, Modulo.ALGORITMO.principalYaSeleccionado(), convAlgoritmoDetalle(algoritmo));
        }
        algoritmoRepo.resetPrincipal();
        algoritmo.setPrincipal(true);
        algoritmoRepo.save(algoritmo);

        return new BaseObjectResponse<>(201, Modulo.ALGORITMO.encontrado(), convAlgoritmoDetalle(algoritmo));
    }

    @Override
    public BaseObjectResponse<AlgoritmoDetalleResponse> buscarPrincipal() {
        Optional<Algoritmo> optAlgoritmo = algoritmoRepo.findByPrincipalTrue();
        if (optAlgoritmo.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ALGORITMO.noEncontrado(), null);
        }
        Algoritmo algoritmo = optAlgoritmo.get();
        return new BaseObjectResponse<>(201, Modulo.ALGORITMO.principalSeleccionado(), convAlgoritmoDetalle(algoritmo));
    }

    private AlgoritmoDetalleResponse convAlgoritmoDetalle(Algoritmo obj) {
        return modelMapper.map(obj, AlgoritmoDetalleResponse.class);
    }


}
