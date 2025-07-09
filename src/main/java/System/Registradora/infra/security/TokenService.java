package System.Registradora.infra.security;

import System.Registradora.domain.usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.secret}")
    private String apiSecret;

    public String generarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            var jwtBuilder = JWT.create()
            //return JWT.create()
                    .withIssuer("Registradora")
                    .withSubject(usuario.getUsername())
                    .withClaim("id", usuario.getId())
                    .withClaim("rol", mapearRol(usuario.getRol()))
                    .withArrayClaim("authorities", new String[]{"ROLE_" + usuario.getRol().toUpperCase()})
                    .withExpiresAt(generarFechaExpiracion());
                    //.sign(algorithm);

            // ✅ Agregar empleadoId si existe
            if (usuario.getEmpleado() != null) {
                jwtBuilder.withClaim("empleadoId", usuario.getEmpleado().getId());
            }

            return jwtBuilder.sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error al generar el token", exception);
        }
    }

    public Object getSubject(String token) {
        if (token == null){
            throw new RuntimeException("El token es nulo");
        }
        try{
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            DecodedJWT verifier = JWT.require(algorithm)
                    .withIssuer("Registradora")
                    .build()
                    .verify(token);
            String subject = verifier.getSubject();
            if (subject == null) {
                throw new RuntimeException("El subject es inválido");
            }
            return subject;
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Error al verificar el token: " + exception.getMessage());
        }
    }

    private Instant generarFechaExpiracion (){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-05:00"));
    }

    private String mapearRol(String rolDb) {
        return switch (rolDb.toUpperCase()) {
            case "ADMIN" -> "ADMINISTRADOR";
            case "VENDEDOR" -> "VENDEDOR";
            case "ALMACENISTA" -> "ALMACENISTA";
            default -> throw new IllegalArgumentException("Rol desconocido: " + rolDb);
        };
    }

}
