package System.Registradora.domain.usuario;

import System.Registradora.infra.security.AutenticationService;
import System.Registradora.infra.security.DatosJWTToken;
import System.Registradora.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AutenticationService autenticationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<?> inicioSesion(@RequestBody AutenticacionUsuarioDto autenticacionUsuarioDto){
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    autenticacionUsuarioDto.login(),
                    autenticacionUsuarioDto.clave());
            var auth = authenticationManager.authenticate(authToken);
            var tokenJWT = tokenService.generarToken((Usuario) auth.getPrincipal());

            return ResponseEntity.ok(new DatosJWTToken(tokenJWT));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autenticación inválida");
        }
    }
}
