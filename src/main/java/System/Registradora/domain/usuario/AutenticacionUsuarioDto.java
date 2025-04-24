package System.Registradora.domain.usuario;

public record AutenticacionUsuarioDto(
        Long id,
        String login,
        String clave,
        String rol
) {
}
