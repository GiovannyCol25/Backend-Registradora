package System.Registradora.dto;

import java.util.Date;

public record TotalComprasPorDiaDto(
        Date fechaCompra,
        Double totalCompras
) {
}
