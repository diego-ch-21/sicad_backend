package com.sicad.sicad_backend.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sicad.sicad_backend.dto.base.CustomErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

//clase 6
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        Object errorAttr = request.getAttribute("jwt_error");

        String message = (errorAttr != null)
                ? errorAttr.toString()
                : "Falló la autenticación";

        CustomErrorResponse errorResponse = new CustomErrorResponse(
                LocalDateTime.now(),
                message,
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(convertObjectToJson(errorResponse));
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());  // Habilita serialización moderna
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Evita array [year, month, day...]

        return mapper.writeValueAsString(object);
    }

}
