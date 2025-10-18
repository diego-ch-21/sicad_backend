package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalCreateRequest;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalDetalleResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalUpdateRequest;
import com.sicad.sicad_backend.model.EscuelaProfesional;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IEscuelaProfesionalRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IEscuelaProfesionalService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.sicad.sicad_backend.Enum.Message.CORREO_EN_USO;

@Slf4j
@Service
@RequiredArgsConstructor
public class EscuelaProfesionalServiceImpl
        extends CRUDImpl<EscuelaProfesional, Integer>
        implements IEscuelaProfesionalService {

    private final IUsuarioRepo userRepository;
    private final IEscuelaProfesionalRepo escuelaProfesionalRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IRolRepo rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    @Override
    protected IGenericRepo<EscuelaProfesional, Integer> getRepo() {
        return escuelaProfesionalRepo;
    }

    @Override
    public BaseListReponse<EscuelaProfesionalDetalleResponse> listar() {
        List<EscuelaProfesionalDetalleResponse> lista = escuelaProfesionalRepo.findByEnabledTrue()
                .stream()
                .map(this::convEscuelaProfesionalDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.ESCUELA_PROFESIONAL.listado(), lista);
    }

    @Override
    public BaseObjectResponse<EscuelaProfesionalDetalleResponse> buscar(Integer idEscuelaProfesional) {
        Optional<EscuelaProfesional> escuelaOpt = escuelaProfesionalRepo.findByIdAndEnabledTrue(idEscuelaProfesional);

        if (escuelaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA_PROFESIONAL.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.ESCUELA_PROFESIONAL.encontrado(), convEscuelaProfesionalDetalle(escuelaOpt.get()));
    }

    @Override
    public BaseObjectResponse<EscuelaProfesionalDetalleResponse> registrar(EscuelaProfesionalCreateRequest request) {
        Integer idRol = 4;
        if (usuarioRepo.findByEmailAndEnabledTrue(request.getEmail()).isPresent()) {
            return new BaseObjectResponse<>(409, CORREO_EN_USO.toString(), null);
        }

        Optional<Rol> optionalRol = rolRepository.findById(idRol);
        if (optionalRol.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ROL.noEncontrado(), null);
        }

        Rol rolUsuario = optionalRol.get();

        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(8);
        } while (userRepository.existsByCodigo(codigo));

        LocalDate fecha = LocalDate.now();
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

        EscuelaProfesional escuela = EscuelaProfesional.builder()
                .usuario(usuario)
                .descripcion("Director de la escuela profesional de ing Sistemas")
                .enabled(true)
                .build();
        escuelaProfesionalRepo.save(escuela);

        return new BaseObjectResponse<>(201, Modulo.ESCUELA_PROFESIONAL.registrado(), convEscuelaProfesionalDetalle(escuela));
    }

    @Override
    public BaseObjectResponse<EscuelaProfesionalDetalleResponse> actualizar(Integer idEscuelaProfesional, EscuelaProfesionalUpdateRequest request) {
        Optional<EscuelaProfesional> escuelaOpt = escuelaProfesionalRepo.findByIdAndEnabledTrue(idEscuelaProfesional);
        if (escuelaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA_PROFESIONAL.noEncontrado(), null);
        }

        EscuelaProfesional escuela = escuelaOpt.get();
        Usuario usuario = escuela.getUsuario();

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
        return new BaseObjectResponse<>(200, Modulo.ESCUELA_PROFESIONAL.actualizado(), convEscuelaProfesionalDetalle(escuela));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idEscuelaProfesional) {
        Optional<EscuelaProfesional> escuelaOpt = escuelaProfesionalRepo.findByIdAndEnabledTrue(idEscuelaProfesional);
        if (escuelaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA_PROFESIONAL.noEncontrado(), null);
        }

        EscuelaProfesional escuela = escuelaOpt.get();
        escuela.setEnabled(false);
        escuelaProfesionalRepo.save(escuela);

        return new BaseObjectResponse<>(200, Modulo.ESCUELA_PROFESIONAL.eliminado(), null);
    }

    private EscuelaProfesionalDetalleResponse convEscuelaProfesionalDetalle(EscuelaProfesional obj) {
        return modelMapper.map(obj, EscuelaProfesionalDetalleResponse.class);
    }
}
