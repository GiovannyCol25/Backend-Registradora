package System.Registradora.dto;

public record EmpleadoDto(
        Long id,
        String nombreEmpleado,
        String cargo,
        Long telefono
) {
}
