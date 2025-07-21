package System.Registradora.domain.repositories;

import System.Registradora.domain.Compra;
import System.Registradora.dto.CompraDto;
import System.Registradora.dto.TotalComprasPorDiaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long>, JpaSpecificationExecutor<Compra> {

    @Query("SELECT c FROM Compra c ORDER BY c.fechaCompra DESC")
    Page<Compra> listarCompras(Pageable paginacion);

    @Query("SELECT new System.Registradora.dto.TotalComprasPorDiaDto(c.fechaCompra, SUM(c.totalCompra)) " +
    "FROM Compra c WHERE DATE(c.fechaCompra) = :fecha GROUP BY c.fechaCompra")
    List<TotalComprasPorDiaDto> obtenerTotalComprasPorFecha(@Param("fecha")Date fecha);
}
