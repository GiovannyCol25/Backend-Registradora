package System.Registradora.dto;

import java.util.Date;

public record ConsultaVentasPorProductoDto(
        String nombreProducto,
        int cantidad,
        double precioUnitario,
        long id,
        Date fechaVenta
) {
}
