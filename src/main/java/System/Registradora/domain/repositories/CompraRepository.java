package System.Registradora.domain.repositories;

import System.Registradora.domain.Compra;
import System.Registradora.dto.CompraDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Query("SELECT c FROM Compra c ORDER BY c.fechaCompra DESC")
    Page<Compra> listarCompras(Pageable paginacion);
}
