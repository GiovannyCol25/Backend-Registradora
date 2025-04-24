package System.Registradora.dto;

public record DetalleVentaDto(
        Long id,
        String nombreProducto,
        Integer cantidad,
        Double precioUnitario
) {}