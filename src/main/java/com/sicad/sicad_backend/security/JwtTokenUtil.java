package com.sicad.sicad_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

//clase 1
@Component
public class JwtTokenUtil {

    // Tiempo de validez del token: 5 horas (en milisegundos)
    private final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    // Se obtiene la clave secreta desde application.properties
    @Value("${jwt.secret}")
    private String secret;

    // ======================================================
    // GENERACIÓN DEL TOKEN JWT
    // ======================================================

    /**
     * Genera un token JWT para un usuario.
     * Se agregan:
     * - Claims personalizados (roles, valores adicionales).
     * - Expiración del token.
     * - Usuario dueño del token.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Agregar roles como claim
        claims.put("role", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));

        // Claims adicionales de prueba
        claims.put("test1", "value-test");
        claims.put("test2", 50);

        // Llama al método interno para construir el token
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Construye y firma el token JWT usando:
     * - Claims personalizados
     * - Subject (username)
     * - Fecha de creación
     * - Fecha de expiración
     * - Firma HS512
     */
    private String doGenerateToken(Map<String, Object> claims, String username) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .claims(claims)                             // Claims del payload
                .subject(username)                          // Usuario dueño del token
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) // Expiración
                .signWith(key)              // Firma con HS512 (SHA-512)
                .compact();
    }

    // ======================================================
    // OBTENER CLAIMS DESDE EL TOKEN
    // ======================================================

    /**
     * Obtiene todos los claims (payload) del token.
     * Si el token está expirado, igualmente se devuelven los claims.
     */
    public Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        try {
            return Jwts.parser()
                    .verifyWith(key)     // Valida la firma
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();       // Retorna los claims
        } catch (ExpiredJwtException e) {
            // Permite obtener los claims aunque esté expirado
            return e.getClaims();
        }
    }

    /**
     * Permite obtener un claim específico aplicando una función.
     * Ejemplo: obtener el "subject", "expiration", etc.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // ======================================================
    // OBTENER INFORMACIÓN ESPECÍFICA DEL TOKEN
    // ======================================================

    /**
     * Obtiene el username (subject) del token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Obtiene la fecha de expiración del token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // ======================================================
    // VALIDACIONES
    // ======================================================

    /**
     * Indica si el token ya expiró.
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Valida:
     * 1. Que el username del token coincida con el usuario autenticado.
     * 2. Que el token no esté expirado.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
