package System.Registradora.dto;

import System.Registradora.domain.usuario.AutenticacionUsuarioDto;

public record EmpleadoUsuarioDto(
        Long id,
        String nombreEmpleado,
        String cargo,
        Long telefono,
        AutenticacionUsuarioDto usuario
) {
}
