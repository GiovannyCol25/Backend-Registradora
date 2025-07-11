package System.Registradora.infra.security;

import System.Registradora.domain.usuario.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static String getLoginFromToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario usuario) {
            return usuario.getLogin();
        }
        return null;
    }
}

    /*public static Long getEmpleadoIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            return null;
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();
        if (usuario.getEmpleado() == null) {
            return null;
        }

        return usuario.getEmpleado().getId();
    }
}
*/