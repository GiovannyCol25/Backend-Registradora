package System.Registradora.dto;

public record DetalleCompraDto(
        Long id,
        String nombreProducto,
        Integer cantidad,
        Double precioUnitario
) {
}
