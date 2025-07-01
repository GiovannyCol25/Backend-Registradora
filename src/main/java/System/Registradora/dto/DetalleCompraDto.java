package System.Registradora.dto;

import System.Registradora.domain.Compra;

public record DetalleCompraDto(
        Long id,
        String nombreProducto,
        Integer cantidad,
        Double precioUnitario
) {
}
