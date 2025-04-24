package System.Registradora.dto;

public record ProductoDto(
        Long id,
        String nombreProducto,
        Double precioVenta,
        String codigoBarras
) {
}
