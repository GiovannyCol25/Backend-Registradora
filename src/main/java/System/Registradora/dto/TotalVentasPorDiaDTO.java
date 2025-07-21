package System.Registradora.dto;

import java.util.Date;

public record TotalVentasPorDiaDTO(
        Date fecha,
        Double totalVentas
) {
}
