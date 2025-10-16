package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoUpdateRequest;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDepartamentoAcademicoRepo;
import com.sicad.sicad_backend.repository.interfaces.IEscuelaRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDepartamentoAcademicoService;
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
public class DepartamentoAcademicoServiceImpl
        extends CRUDImpl<DepartamentoAcademico, Integer>
        implements IDepartamentoAcademicoService {

    private final IUsuarioRepo userRepository;
    private final IDepartamentoAcademicoRepo departamentoAcademicoRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IRolRepo rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<DepartamentoAcademico, Integer> getRepo() {
        return departamentoAcademicoRepo;
    }

    @Override
    public BaseListReponse<DepartamentoAcademicoDetalleResponse> listar() {
        List<DepartamentoAcademicoDetalleResponse> lista = departamentoAcademicoRepo.findByEnabledTrue()
                .stream()
                .map(this::convDirectorDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.DEPARTAMENTO_ACADEMICO.listado(), lista);
    }

    @Override
    public BaseObjectResponse<DepartamentoAcademicoDetalleResponse> buscar(Integer idDirector) {
        Optional<DepartamentoAcademico> directorOpt = departamentoAcademicoRepo.findByIdAndEnabledTrue(idDirector);

        if (directorOpt.isEmpty()) {
            return new BaseObjectResponse<>(404,Modulo.DEPARTAMENTO_ACADEMICO.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.DEPARTAMENTO_ACADEMICO.encontrado(),convDirectorDetalle(directorOpt.get()));
    }

    @Override
    public BaseObjectResponse<DepartamentoAcademicoDetalleResponse> registrar(DepartamentoAcademicoCreateRequest request) {
        Integer idRol = 2;
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

        DepartamentoAcademico departamentoAcademico = DepartamentoAcademico.builder()
                .usuario(usuario)
                .descripcion("Director del departamento academico de ciencia de la computación")
                .enabled(true)
                .build();
        departamentoAcademicoRepo.save(departamentoAcademico);

        DepartamentoAcademicoDetalleResponse response = convDirectorDetalle(departamentoAcademico);

        return new BaseObjectResponse<>(201, Modulo.DEPARTAMENTO_ACADEMICO.registrado(), response);
    }

    @Override
    public BaseObjectResponse<DepartamentoAcademicoDetalleResponse> actualizar(Integer idDirector, DepartamentoAcademicoUpdateRequest request) {
        Optional<DepartamentoAcademico> directorOpt = departamentoAcademicoRepo.findByIdAndEnabledTrue(idDirector);
        if (directorOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }
        DepartamentoAcademico departamentoAcademico = directorOpt.get();

        Usuario usuario = departamentoAcademico.getUsuario();

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

        departamentoAcademicoRepo.save(departamentoAcademico);
        DepartamentoAcademicoDetalleResponse response = convDirectorDetalle(departamentoAcademico);
        return new BaseObjectResponse<>(200, Modulo.DEPARTAMENTO_ACADEMICO.actualizado(), response);
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idDirector) {
        Optional<DepartamentoAcademico> directorOpt = departamentoAcademicoRepo.findByIdAndEnabledTrue(idDirector);
        if (directorOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DEPARTAMENTO_ACADEMICO.noEncontrado(), null);
        }
        DepartamentoAcademico departamentoAcademico = directorOpt.get();
        departamentoAcademico.setEnabled(false);
        departamentoAcademicoRepo.save(departamentoAcademico);
        return new BaseObjectResponse<>(200, Modulo.DEPARTAMENTO_ACADEMICO.eliminado(), null);
    }
    private DepartamentoAcademicoDetalleResponse convDirectorDetalle(DepartamentoAcademico obj) {
        return modelMapper.map(obj, DepartamentoAcademicoDetalleResponse.class);
    }
}
