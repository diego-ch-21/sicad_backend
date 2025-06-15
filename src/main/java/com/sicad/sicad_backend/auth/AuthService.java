package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.jwt.JwtService;
import com.sicad.sicad_backend.persistence.model.Docente;
import com.sicad.sicad_backend.persistence.model.Rol;
import com.sicad.sicad_backend.persistence.model.Usuario;
import com.sicad.sicad_backend.persistence.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.persistence.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.persistence.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.presentation.dto.DocenteDTO;
import com.sicad.sicad_backend.presentation.dto.UsuarioDTO;
import com.sicad.sicad_backend.presentation.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.util.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUsuarioRepo userRepository;
    private final IDocenteRepo docenteRepository;
    private final IRolRepo rolRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public GenericObjectResponse<AuthResponse> login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );
            Optional<Usuario> optionalUser = userRepository.findByUsername(request.getUsername());

            if (optionalUser.isEmpty()) {
                return new GenericObjectResponse<>(404, "Usuario no encontrado", null);
            }

            Usuario usuario = optionalUser.get();
            String token = jwtService.getToken(usuario);
            UsuarioDTO usuarioDTO = convertToDTO(usuario);
            Integer idRol = usuario.getRol().getIdRol();
            Docente docente;
            switch (idRol){
                case 1: // Admin
                    docente =null;
                    break;
                case 2: // Director
                    docente =null;
                    break;
                case 3: // Docente
                    docente = docenteRepository.findByIdUsuario(usuario)
                            .orElse(null);
                    break;
                default:
                    docente =null;
                    break;
            }
            DocenteDTO docenteDTO;
            if(docente != null){
                docenteDTO = modelMapper.map(docente, DocenteDTO.class);
            } else {
                docenteDTO = null;
            }


            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .usuario(usuarioDTO)
                    .docente(docenteDTO)
                    .build();

            return new GenericObjectResponse<>(200, "Inicio de sesión exitoso", authResponse);

        } catch (BadCredentialsException e) {
            return new GenericObjectResponse<>(401, "Credenciales incorrectas", null);
        }
    }

    //no se usara
    public GenericObjectResponse<AuthResponse> registerAdmin(RegisterRequest request, Integer idRol) {
        // Verificar si el username ya existe
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new GenericObjectResponse<>(409, "El nombre de usuario ya está en uso", null);
        }

        // Verificar si el email ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new GenericObjectResponse<>(409, "El correo electrónico ya está en uso", null);
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
        Usuario user = Usuario.builder()
                .codigo(codigo)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .enabled(true)
                .rol(rolUsuario)
                .build();

        userRepository.save(user);

        // Generar token y respuesta
        UsuarioDTO usuarioDTO = convertToDTO(user);
        String token = jwtService.getToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .usuario(usuarioDTO)
                .build();

        return new GenericObjectResponse<>(201, "Usuario registrado exitosamente", authResponse);
    }

    private UsuarioDTO convertToDTO(Usuario obj) {
        return modelMapper.map(obj, UsuarioDTO.class);
    }
    private Usuario convertToEntity(UsuarioDTO dto) {
        return modelMapper.map(dto, Usuario.class);
    }
}
