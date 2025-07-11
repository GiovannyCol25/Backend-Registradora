package System.Registradora.domain.usuario;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "empleado")
    UserDetails findByLogin(String username);

    @EntityGraph(attributePaths = "empleado")
    Usuario findUsuarioByLogin(String login);

}
