package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorCreateRequest;
import com.sicad.sicad_backend.dto.jefeDepartamento.JefeDepartamentoCreateRequest;
import com.sicad.sicad_backend.dto.jefeDepartamento.JefeDepartamentoDetalleResponse;
import com.sicad.sicad_backend.dto.jefeDepartamento.JefeDepartamentoUpdateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaDetalleResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUpdateRequest;
import com.sicad.sicad_backend.model.JefeDepartamento;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IJefeDepartamentoRepo;
import com.sicad.sicad_backend.repository.interfaces.ILogisticaRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IJefeDepartamentoService;
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
public class JefeDepartamentoServiceImpl extends CRUDImpl<JefeDepartamento, Integer> implements IJefeDepartamentoService {
    private final IUsuarioRepo userRepository;
    private final IJefeDepartamentoRepo jefeDepartamentoRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IRolRepo rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    @Override
    protected IGenericRepo<JefeDepartamento, Integer> getRepo() {
        return jefeDepartamentoRepo;
    }

    public GenericObjectResponse<JefeDepartamentoDetalleResponse> registrarJefeDepartamento(JefeDepartamentoCreateRequest request) {
        Integer idRol = 4;
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

        JefeDepartamento jefe = JefeDepartamento.builder()
                .usuario(usuario)
                .cargo(request.getCargo())
                .enabled(true)
                .build();
        jefeDepartamentoRepo.save(jefe);

        // 5. Mapear y retornar DTO
        JefeDepartamentoDetalleResponse dto = modelMapper.map(jefe, JefeDepartamentoDetalleResponse.class);
        return new GenericObjectResponse<>(201, "usuario jefe Departamento registrado exitosamente", dto);
    }
    public GenericObjectResponse<JefeDepartamentoDetalleResponse> actualizarJefeDepartamento(Integer idJefeDepartamento, JefeDepartamentoUpdateRequest request) {
        // 1. Buscar director existente
        JefeDepartamento jefe = jefeDepartamentoRepo.findById(idJefeDepartamento).orElse(null);
        if (jefe == null) {
            return new GenericObjectResponse<>(404, "jefe departamento no encontrado", null);
        }

        Usuario usuario = jefe.getUsuario();

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
            jefe.setCargo(request.getCargo());
        }

        jefeDepartamentoRepo.save(jefe);

        return new GenericObjectResponse<>(200, "Jefe departamento actualizado exitosamente", convertToResponseDTO(jefe));
    }
    private JefeDepartamentoDetalleResponse convertToResponseDTO(JefeDepartamento obj) {
        return modelMapper.map(obj, JefeDepartamentoDetalleResponse.class);
    }
    public GenericObjectResponse<String> eliminarJefeDepartamento(Integer idJefeDepartamento) {
        // Validación de parámetro
        if (idJefeDepartamento == null) {
            return new GenericObjectResponse<>(400, "idJefeDepartamento no proporcionado", null);
        }

        // Validar existencia del curso
        JefeDepartamento jefeDepartamento = jefeDepartamentoRepo.findById(idJefeDepartamento).orElse(null);
        if (jefeDepartamento == null) {
            return new GenericObjectResponse<>(404, "Jefe departamento  no encontrado", null);
        }

        // desabilitar
        jefeDepartamento.setEnabled(false);
        jefeDepartamentoRepo.save(jefeDepartamento);
        return new GenericObjectResponse<>(200, "se elimino el Jefe departamento exitosamente", null);
    }

    @Override
    public List<JefeDepartamento> findByEnabledTrue() {
        return jefeDepartamentoRepo.findAll();
    }
}
