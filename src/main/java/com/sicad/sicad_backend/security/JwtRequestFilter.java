package com.sicad.sicad_backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//clase 5
@Component
@RequiredArgsConstructor
public class JwtRequestFilter
    extends OncePerRequestFilter {

    private final CustomUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final  String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        String username= null;
        String accessToken= null;

        if(header !=null && header.startsWith("Bearer ")){
            accessToken = header.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(accessToken);
            } catch (ExpiredJwtException e) {
                request.setAttribute("jwt_error", "Token expirado");
            } catch (SignatureException e) {
                request.setAttribute("jwt_error", "Firma JWT inválida");
            } catch (MalformedJwtException e) {
                request.setAttribute("jwt_error", "Token mal formado");
            } catch (Exception e) {
                request.setAttribute("jwt_error", "Token inválido");
            }

        }
        if(username !=null && accessToken !=null){
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
            if(jwtTokenUtil.validateToken(accessToken,userDetails)){
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request,response);
    }
}
