package System.Registradora.domain.repositories;

import System.Registradora.domain.DetalleVenta;
import System.Registradora.dto.DetalleVentaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

        @Query("""
        SELECT new System.Registradora.dto.DetalleVentaDto(d.id, d.producto.nombreProducto, d.cantidad, d.precioUnitario) 
        FROM DetalleVenta d 
        WHERE d.producto.nombreProducto = :nombreProducto
    """)
        List<DetalleVentaDto> findByNombreProducto(@Param("nombreProducto") String nombreProducto);
}
