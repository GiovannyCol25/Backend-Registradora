package System.Registradora.dto;

import java.util.Date;
import java.util.List;

public record VentaDto(
        Long id,
        Date fechaVenta,
        Double totalVenta,
        Double descuento,
        String formaDePago,
        List<DetalleVentaDto> detalles // ✅ Lista de productos en la venta
) {
}
