package edu.pe.vallegrande.Security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import edu.pe.vallegrande.Security.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "clave_secreta_para_jwt_vallegrande_security";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 días en milisegundos

    public String generarToken(Usuario usuario, String rol) {
        return JWT.create()
                .withSubject(usuario.getUid())
                .withClaim("email", usuario.getEmail())
                .withClaim("rol", rol)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String extraerUidDelToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token.replace("Bearer ", ""))
                .getSubject();
    }

    public String extraerRolDelToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token.replace("Bearer ", ""))
                .getClaim("rol")
                .asString();
    }
}