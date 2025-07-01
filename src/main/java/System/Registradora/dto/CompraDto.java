package System.Registradora.dto;

import System.Registradora.domain.DetalleCompra;

import java.util.Date;
import java.util.List;

public record CompraDto(
        Long id,
        Date fechaCompra,
        Double totalCompra,
        String numeroFactura,
        List<DetalleCompraDto> detalleCompraDtoList
) {
}
