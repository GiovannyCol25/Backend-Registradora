package System.Registradora.domain.repositories;

import System.Registradora.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdProducto(@Param("id") Long id);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombreProducto) LIKE LOWER(CONCAT('%', :nombreProducto, '%'))")
    Optional<Producto> findByNombreProducto(@Param("nombreProducto") String nombreProducto);

    @Query("SELECT p FROM Producto p WHERE p.codigoBarras = :codigoBarras")
    Optional<Producto> findByCodigoBarras(@Param("codigoBarras") Long codigoBarras);

    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre);
}
