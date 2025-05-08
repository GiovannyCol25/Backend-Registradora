package System.Registradora.infra.security;

public record LoginRespuestaDTO(
        String jwtToken,
        String rol) {
}
