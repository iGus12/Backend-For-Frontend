package cl.sanosysalvo.BFF.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JWTservice {

    private static final String SECRET = "sanosysalvos_super_secret_key_2026_123456";

    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public Long extractUsuarioId(String token) {
        Object id = obtenerClaims(token).get("id");

        if (id instanceof Integer) {
            return ((Integer) id).longValue();
        }

        if (id instanceof Long) {
            return (Long) id;
        }

        return Long.valueOf(id.toString());
    }

    public String extractRol(String token) {
        Object rol = obtenerClaims(token).get("rol");
        return rol != null ? rol.toString() : null;
    }

    public String extractSubject(String token) {
        return obtenerClaims(token).getSubject();
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}