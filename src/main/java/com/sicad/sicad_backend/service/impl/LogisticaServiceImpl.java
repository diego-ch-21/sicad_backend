package com.sicad.sicad_backend.service.impl;


import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaCreateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaDetalleResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUpdateRequest;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ILogisticaRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ILogisticaService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sicad.sicad_backend.Enum.Message.CORREO_EN_USO;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticaServiceImpl
        extends CRUDImpl<Logistica, Integer>
        implements ILogisticaService {

    private final IUsuarioRepo userRepository;
    private final ILogisticaRepo logisticaRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IRolRepo rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Logistica, Integer> getRepo() {
        return logisticaRepo;
    }


    @Override
    public BaseListReponse<LogisticaDetalleResponse> listar() {
        List<LogisticaDetalleResponse> lista = logisticaRepo.findByEnabledTrue()
                .stream()
                .map(this::convLogisticaDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.LOGISTICA.listado(), lista);
    }

    @Override
    public BaseObjectResponse<LogisticaDetalleResponse> buscar(Integer idLogistica) {
        Optional<Logistica> logisticaOpt = logisticaRepo.findByIdAndEnabledTrue(idLogistica);

        if (logisticaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }

        LogisticaDetalleResponse response = convLogisticaDetalle(logisticaOpt.get());
        return new BaseObjectResponse<>(200, Modulo.LOGISTICA.encontrado(), response);
    }

    @Override
    public BaseObjectResponse<LogisticaDetalleResponse> registrar(LogisticaCreateRequest request) {
        Integer idRol = 5;
        if (usuarioRepo.findByEmailAndEnabledTrue(request.getEmail()).isPresent()) {
            return new BaseObjectResponse<>(409, CORREO_EN_USO.toString(), null);
        }
        Optional<Rol> optionalRol = rolRepository.findById(idRol);
        if (!optionalRol.isPresent()) {
            return new BaseObjectResponse<>(404, Modulo.ROL.noEncontrado(), null);
        }
        Rol rolUsuario = optionalRol.get();

        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(8);
        } while (userRepository.existsByCodigo(codigo));

        // Crear y guardar usuario
        Usuario usuario = Usuario.builder()
                .codigo(codigo)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true)
                .cretedAt(LocalDate.now())
                .rol(rolUsuario)
                .build();
        usuarioRepo.save(usuario);

        Logistica logistica = Logistica.builder()
                .usuario(usuario)
                .cargo(request.getCargo())
                .enabled(true)
                .build();
        logisticaRepo.save(logistica);

        // 5. Mapear y retornar DTO
        return new BaseObjectResponse<>(201, Modulo.LOGISTICA.registrado(), convLogisticaDetalle(logistica));
    }

    @Override
    public BaseListReponse<LogisticaDetalleResponse> registrarAll(List<LogisticaCreateRequest> requests) {
        List<LogisticaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (LogisticaCreateRequest request : requests) {
            try {
                BaseObjectResponse<LogisticaDetalleResponse> response = registrar(request);
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
        return new BaseListReponse<>(201,Modulo.LOGISTICA.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<LogisticaDetalleResponse> actualizar(Integer idLogistica, LogisticaUpdateRequest request) {
        Optional<Logistica> logisticaOpt = logisticaRepo.findByIdAndEnabledTrue(idLogistica);

        if (logisticaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }
        Logistica logistica = logisticaOpt.get();

        Usuario usuario = logistica.getUsuario();

        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepo.findByEmailAndEnabledTrue(request.getEmail()).isPresent()) {
                return new BaseObjectResponse<>(409, CORREO_EN_USO.toString(), null);
            }
            usuario.setEmail(request.getEmail());
        }

        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }

        if (request.getApellido() != null) {
            usuario.setApellido(request.getApellido());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuarioRepo.save(usuario);

        if (request.getCargo() != null) {
            logistica.setCargo(request.getCargo());
        }

        logisticaRepo.save(logistica);

        return new BaseObjectResponse<>(200, Modulo.LOGISTICA.actualizado(), convLogisticaDetalle(logistica));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idLogistica) {
        Optional<Logistica> logisticaOpt = logisticaRepo.findByIdAndEnabledTrue(idLogistica);

        if (logisticaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.LOGISTICA.noEncontrado(), null);
        }
        Logistica logistica = logisticaOpt.get();
        logistica.setEnabled(false);
        logisticaRepo.save(logistica);
        return new BaseObjectResponse<>(200, Modulo.LOGISTICA.eliminado(),null);
    }

    private LogisticaDetalleResponse convLogisticaDetalle(Logistica obj) {
        return modelMapper.map(obj, LogisticaDetalleResponse.class);
    }


}
