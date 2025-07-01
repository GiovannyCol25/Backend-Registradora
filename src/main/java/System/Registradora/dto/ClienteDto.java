package System.Registradora.dto;

import java.util.List;

public record ClienteDto(
        Long id,
        String nombre,
        Long telefono
) {
}
