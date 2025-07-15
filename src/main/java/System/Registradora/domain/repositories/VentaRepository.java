package System.Registradora.domain.repositories;

import System.Registradora.domain.Venta;
import System.Registradora.dto.TotalVentasPorDiaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT v FROM Venta v ORDER BY v.fechaVenta DESC")
    Page<Venta> listarVentas(Pageable paginacion);

    /*
    // Ventas Diarias
    @Query("SELECT v FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    List<Venta> obtenerVentasDiarias();
    */

    // Total de ventas por día
    @Query("SELECT new System.Registradora.dto.TotalVentasPorDiaDTO(v.fechaVenta, SUM(v.totalVenta)) " +
            "FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE GROUP BY v.fechaVenta")
    TotalVentasPorDiaDTO obtenerTotalVentasDiarias();

    @Query("SELECT new System.Registradora.dto.TotalVentasPorDiaDTO(v.fechaVenta, SUM(v.totalVenta)) " +
            "FROM Venta v WHERE DATE(v.fechaVenta) = :fecha GROUP BY v.fechaVenta")
    List<TotalVentasPorDiaDTO> obtenerTotalVentasPorFecha(@Param("fecha") Date fecha);

    Page<Venta> findAll(Object o, Pageable paginacion);
}
