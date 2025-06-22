package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorRequestDTO;
import com.sicad.sicad_backend.dto.director.DirectorResponseDTO;
import com.sicad.sicad_backend.dto.director.DirectorUpdateRequestDTO;
import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDirectorRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDirectorService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl extends CRUDImpl<Director, Integer> implements IDirectorService {

    private final IUsuarioRepo userRepository;
    private final IDirectorRepo repo;
    private final IUsuarioRepo usuarioRepo;
    private final IRolRepo rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Director, Integer> getRepo() {
        return repo;
    }

    public GenericObjectResponse<DirectorResponseDTO> registrarDirector(DirectorRequestDTO request) {
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

        // Crear y guardar usuario
        Usuario usuario = Usuario.builder()
                .codigo(codigo)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true) //por defecto
                .rol(rolUsuario)
                .build();
        usuarioRepo.save(usuario);

        // 4. Crear y guardar Director
        Director director = Director.builder()
                .usuario(usuario)
                .cargo(request.getCargo())
                .enabled(true) //por defecto
                .build();
        repo.save(director);

        // 5. Mapear y retornar DTO
        DirectorResponseDTO dto = modelMapper.map(director, DirectorResponseDTO.class);
        return new GenericObjectResponse<>(201, "Director registrado exitosamente", dto);
    }

    public GenericObjectResponse<DirectorResponseDTO> actualizarDirector(Integer idDirector, DirectorUpdateRequestDTO request) {

        // 1. Buscar director existente
        Director director = repo.findById(idDirector).orElse(null);
        if (director == null) {
            return new GenericObjectResponse<>(404, "Director no encontrado", null);
        }

        // 2. Validar si email ha cambiado y ya está en uso
        Usuario usuario = director.getUsuario();
        if (!usuario.getEmail().equals(request.getEmail()) &&
                usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return new GenericObjectResponse<>(409, "El correo ya está en uso", null);
        }

        // 3. Actualizar datos Usuario
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        usuarioRepo.save(usuario);

        // 4. Actualizar datos Director
        director.setCargo(request.getCargo());
        repo.save(director);

        // 5. Mapear y retornar DTO
        DirectorResponseDTO dto = modelMapper.map(director, DirectorResponseDTO.class);
        return new GenericObjectResponse<>(200, "Director actualizado exitosamente", dto);
    }
}
