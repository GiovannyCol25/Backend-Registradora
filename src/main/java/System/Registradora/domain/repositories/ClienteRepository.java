package System.Registradora.domain.repositories;

import System.Registradora.domain.Cliente;
import System.Registradora.dto.ClienteDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT p FROM Cliente p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    boolean existsByName(String nombre);

    @Query("SELECT c FROM Cliente c")
    List<ClienteDto> listarClientes();
}
