package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorCreateRequest;
import com.sicad.sicad_backend.dto.director.DirectorDetalleResponse;
import com.sicad.sicad_backend.dto.director.DirectorUpdateRequest;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDirectorRepo;
import com.sicad.sicad_backend.repository.interfaces.IFacultadRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDirectorService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl extends CRUDImpl<Director, Integer> implements IDirectorService {

    private final IUsuarioRepo userRepository;
    private final IDirectorRepo directorRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IRolRepo rolRepository;
    private final IFacultadRepo facultadRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Director, Integer> getRepo() {
        return directorRepo;
    }

    public GenericObjectResponse<DirectorDetalleResponse> registrarDirector(DirectorCreateRequest request) {
        Integer idRol = 2;
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

        Facultad facultad = facultadRepo.findById(request.getIdFacultad()).orElse(null);
        if (facultad == null)
            return new GenericObjectResponse<>(404, "facultad no encontrada", null);

        String codigoDirector;
        do {
            codigoDirector = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (directorRepo.existsByCodigo(codigoDirector));

        Director director = Director.builder()
                .usuario(usuario)
                .cargo(request.getCargo())
                .enabled(true)
                .facultad(facultad)
                .codigo(codigoDirector)
                .build();
        directorRepo.save(director);

        // 5. Mapear y retornar DTO
        DirectorDetalleResponse dto = modelMapper.map(director, DirectorDetalleResponse.class);
        return new GenericObjectResponse<>(201, "Director registrado exitosamente", dto);
    }

    public GenericObjectResponse<DirectorDetalleResponse> actualizarDirector(Integer idDirector, DirectorUpdateRequest request) {
        // 1. Buscar director existente
        Director director = directorRepo.findById(idDirector).orElse(null);
        if (director == null) {
            return new GenericObjectResponse<>(404, "Director no encontrado", null);
        }

        Usuario usuario = director.getUsuario();

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

        // 4. Actualizar Facultad solo si se envió ID
        if (request.getIdFacultad() != null) {
            Facultad facultad = facultadRepo.findById(request.getIdFacultad()).orElse(null);
            if (facultad == null) {
                return new GenericObjectResponse<>(404, "Facultad no encontrada", null);
            }
            director.setFacultad(facultad);
        }

        // 5. Actualizar cargo si fue enviado
        if (request.getCargo() != null) {
            director.setCargo(request.getCargo());
        }

        directorRepo.save(director);

        DirectorDetalleResponse dto = modelMapper.map(director, DirectorDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Director actualizado exitosamente", dto);
    }

}
