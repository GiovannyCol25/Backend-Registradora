package System.Registradora.domain.repositories;

import System.Registradora.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query("""
    SELECT e.id FROM Empleado e 
    WHERE e.usuario.login = :login
    """)
    Long findEmpleadoIdByUsuarioLogin(@Param("login") String login);

    Empleado findByUsuarioLogin(String login);
}
