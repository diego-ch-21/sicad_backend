package com.sicad.sicad_backend.service.impl;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorCreateRequest;
import com.sicad.sicad_backend.dto.director.DirectorDetalleResponse;
import com.sicad.sicad_backend.dto.director.DirectorUpdateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaCreateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaDetalleResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUpdateRequest;
import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDirectorRepo;
import com.sicad.sicad_backend.repository.interfaces.ILogisticaRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDirectorService;
import com.sicad.sicad_backend.service.interfaces.ILogisticaService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogisticaServiceImpl extends CRUDImpl<Logistica, Integer> implements ILogisticaService {
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

    public GenericObjectResponse<LogisticaDetalleResponse> registrarLogistica(LogisticaCreateRequest request) {
        Integer idRol = 5;
        // 1. Verificar si email ya existe
        if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return new GenericObjectResponse<>(409, "El correo ya está en uso", null);
        }

        // Obtener rol
        Optional<Rol> optionalRol = rolRepository.findById(idRol);
        if (!optionalRol.isPresent()) {
            return new GenericObjectResponse<>(404, "Rol no encontrado", null);
        }
        Rol rolUsuario = optionalRol.get();

        // Generar código único y verificar duplicado
        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(8);
        } while (userRepository.existsByCodigo(codigo));

        // Crear y guardar Docente
        LocalDate fecha = LocalDate.now();

        // Crear y guardar usuario
        Usuario usuario = Usuario.builder()
                .codigo(codigo)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true)
                .cretedAt(fecha)
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
        return new GenericObjectResponse<>(201, "usuario logistica registrado exitosamente", convertToResponseDTO(logistica));
    }
    public GenericObjectResponse<LogisticaDetalleResponse> actualizarLogistica(Integer idLogistica, LogisticaUpdateRequest request) {
        // 1. Buscar director existente
        Logistica logistica = logisticaRepo.findById(idLogistica).orElse(null);
        if (logistica == null) {
            return new GenericObjectResponse<>(404, "logistica no encontrado", null);
        }

        Usuario usuario = logistica.getUsuario();

        // 2. Validar si se intenta cambiar email y si ya está en uso
        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
                return new GenericObjectResponse<>(409, "El correo ya está en uso", null);
            }
            usuario.setEmail(request.getEmail());
        }

        // 3. Actualizar solo los datos presentes en Usuario
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

        // 5. Actualizar cargo si fue enviado
        if (request.getCargo() != null) {
            logistica.setCargo(request.getCargo());
        }

        logisticaRepo.save(logistica);

        return new GenericObjectResponse<>(200, "Director actualizado exitosamente", convertToResponseDTO(logistica));
    }
    public GenericObjectResponse<String> eliminarLogistica(Integer idLogistica) {
        // Validación de parámetro
        if (idLogistica == null) {
            return new GenericObjectResponse<>(400, "idLogistica no proporcionado", null);
        }

        // Validar existencia del curso
        Logistica logistica = logisticaRepo.findById(idLogistica).orElse(null);
        if (logistica == null) {
            return new GenericObjectResponse<>(404, "Logistica  no encontrado", null);
        }

        // desabilitar
        logistica.setEnabled(false);
        logisticaRepo.save(logistica);
        return new GenericObjectResponse<>(200, "se elimino la logsitica exitosamente", null);
    }
    private LogisticaDetalleResponse convertToResponseDTO(Logistica obj) {
        return modelMapper.map(obj, LogisticaDetalleResponse.class);
    }

    @Override
    public List<Logistica> findByEnabledTrue() {
        return logisticaRepo.findAll();
    }
}
