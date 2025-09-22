package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.auth.dto.AuthResponse;
import com.sicad.sicad_backend.auth.dto.LoginRequest;
import com.sicad.sicad_backend.auth.dto.RegisterRequest;
import com.sicad.sicad_backend.auth.dto.RolesResponse;
import com.sicad.sicad_backend.dto.director.DirectorUsuarioResponse;
import com.sicad.sicad_backend.dto.docente.DocenteDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteUsuarioResponse;
import com.sicad.sicad_backend.dto.jefeDepartamento.JefeDepartamentoUsuarioResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUsuarioResponse;
import com.sicad.sicad_backend.jwt.JwtService;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.dto.usuario.UsuarioDTO;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUsuarioRepo userRepository;
    private final IDocenteRepo docenteRepository;
    private final IDirectorRepo directorRepository;
    private final IJefeDepartamentoRepo jefeDepartamentoRepo;
    private final ILogisticaRepo logisticaRepo;
    private final IRolRepo rolRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public GenericObjectResponse<AuthResponse> login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );
            Optional<Usuario> optionalUser = userRepository.findByEmail(request.getEmail());

            if (optionalUser.isEmpty()) {
                return new GenericObjectResponse<>(404, "Email no encontrado", null);
            }

            Usuario usuario = optionalUser.get();
            String token = jwtService.getToken(usuario);
            UsuarioDTO usuarioDTO = convertToDTO(usuario);
            Integer idRol = usuario.getRol().getIdRol();
            Docente docente=null;
            Director director = null;
            JefeDepartamento jefeDepartamento = null;
            Logistica logistica = null;
            switch (idRol){
                case 1: // Admin
                    break;
                case 2: // Director
                    director = directorRepository.findByUsuario(usuario)
                            .orElse(null);
                    break;
                case 3: // Docente
                    docente = docenteRepository.findByUsuario(usuario)
                            .orElse(null);
                    break;
                case 4:
                    jefeDepartamento =jefeDepartamentoRepo.findByUsuario(usuario)
                            .orElse(null);
                    break;
                case 5:
                    logistica = logisticaRepo.findByUsuario(usuario)
                            .orElse(null);
                    break;
                default:
                    docente =null;
                    break;
            }
            DocenteUsuarioResponse docenteResponseDTO;
            if(docente != null){
                docenteResponseDTO = convertToUResponseDTO(docente);
            } else {
                docenteResponseDTO = null;
            }
            DirectorUsuarioResponse directorResponseDTO;
            if(director != null){
                directorResponseDTO = convertToDirectorUResponseDTO(director);
            } else {
                directorResponseDTO = null;
            }

            LogisticaUsuarioResponse logisticaResponseDTO;
            if(logistica != null){
                logisticaResponseDTO = convertToLogisticaUResponseDTO(logistica);
            } else {
                logisticaResponseDTO = null;
            }

            JefeDepartamentoUsuarioResponse jefeDepartamentoResponseDTO;
            if(jefeDepartamento != null){
                jefeDepartamentoResponseDTO = convertToJefeDepartamentoUResponseDTO(jefeDepartamento);
            } else {
                jefeDepartamentoResponseDTO = null;
            }



            RolesResponse rolesResponse = RolesResponse.builder()
                    .docente(docenteResponseDTO)
                    .director(directorResponseDTO)
                    .logistica(logisticaResponseDTO)
                    .jefeDepatamento(jefeDepartamentoResponseDTO)
                    .build();


            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .usuario(usuarioDTO)
                    .roles(rolesResponse)
                    .build();

            return new GenericObjectResponse<>(200, "Inicio de sesión exitoso", authResponse);

        } catch (BadCredentialsException e) {
            return new GenericObjectResponse<>(401, "Credenciales incorrectas", null);
        }
    }

    //no se usara
    public GenericObjectResponse<AuthResponse> registerAdmin(RegisterRequest request) {
        Integer idRol =1;
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
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (userRepository.existsByCodigo(codigo));

        LocalDate createdAt = LocalDate.now();

        // Crear y guardar usuario
        Usuario user = Usuario.builder()
                .codigo(codigo)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true)
                .cretedAt(createdAt)
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
    private DocenteDetalleResponse convertToResponseDTO(Docente obj) {
        return modelMapper.map(obj, DocenteDetalleResponse.class);
    }
    private DocenteUsuarioResponse convertToUResponseDTO(Docente obj) {
        return modelMapper.map(obj, DocenteUsuarioResponse.class);
    }
    private DirectorUsuarioResponse convertToDirectorUResponseDTO(Director obj) {
        return modelMapper.map(obj, DirectorUsuarioResponse.class);
    }
    private JefeDepartamentoUsuarioResponse convertToJefeDepartamentoUResponseDTO(JefeDepartamento obj) {
        return modelMapper.map(obj, JefeDepartamentoUsuarioResponse.class);
    }
    private LogisticaUsuarioResponse convertToLogisticaUResponseDTO(Logistica obj) {
        return modelMapper.map(obj, LogisticaUsuarioResponse.class);
    }
    private Usuario convertToEntity(UsuarioDTO dto) {
        return modelMapper.map(dto, Usuario.class);
    }

}
