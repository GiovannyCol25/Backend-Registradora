package System.Registradora.dto;

import System.Registradora.domain.TipoMovimiento;

import java.util.Date;

public record MovimientoDto(
        Long id,
        Date fechaMovimiento,
        int cantidad,
        TipoMovimiento tipoMovimiento
) {
}
