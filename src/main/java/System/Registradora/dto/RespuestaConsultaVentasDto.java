package System.Registradora.dto;

import java.util.List;

public record RespuestaConsultaVentasDto(
        String mensaje,
        List<ConsultaVentasPorProductoDto> ventas
) {
}
