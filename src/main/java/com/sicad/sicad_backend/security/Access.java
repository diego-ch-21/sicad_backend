package com.sicad.sicad_backend.security;

import com.sicad.sicad_backend.Enum.RolEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Access {

    public boolean isAdmin() {
        return hasRole(RolEnum.ADMIN.name());
    }
    public boolean isDepaAcad(){
        return  hasRole(RolEnum.DEPARTAMENTO_ACADEMICO.name());
    }
    public boolean isDocente(){
        return hasRole(RolEnum.DOCENTE.name());
    }
    public boolean isEscuelaProf(){
        return hasRole(RolEnum.ESCUELA_PROFESIONAL.name());
    }
    public boolean isLogistica(){
        return hasRole(RolEnum.LOGISTICA.name());
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
