package System.Registradora.controller;

import System.Registradora.domain.Empleado;
import System.Registradora.domain.repositories.EmpleadoRepository;
import System.Registradora.domain.usuario.AutenticacionUsuarioDto;
import System.Registradora.domain.usuario.Usuario;
import System.Registradora.domain.usuario.UsuarioRepository;
import System.Registradora.dto.EmpleadoDto;
import System.Registradora.dto.EmpleadoUsuarioDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping
    public ResponseEntity<List<EmpleadoDto>> listarEmpleados() {
        List<EmpleadoDto> listadoEmpleados = empleadoRepository.findAll().stream()
                .map(empleado -> new EmpleadoDto(
                        empleado.getId(),
                        empleado.getNombreEmpleado(),
                        empleado.getCargo(),
                        empleado.getTelefono()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listadoEmpleados);
    }
/*
    @PostMapping
    @Transactional
    public ResponseEntity<EmpleadoDto> crearEmpleado(@RequestBody EmpleadoDto empleadoDTO) {
        Empleado empleado = new Empleado(empleadoDTO);
        empleado = empleadoRepository.save(empleado);

        EmpleadoDto empleadoCreado = new EmpleadoDto(
                empleado.getId(),
                empleado.getNombreEmpleado(),
                empleado.getCargo(),
                empleado.getTelefono()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoCreado);
    }
*/

    @PostMapping
    @Transactional
    public ResponseEntity<EmpleadoUsuarioDto> crearEmpleado(@RequestBody EmpleadoUsuarioDto dto) {
        // Crear entidad Empleado
        Empleado empleado = new Empleado();
        empleado.setNombreEmpleado(dto.nombreEmpleado());
        empleado.setCargo(dto.cargo());
        empleado.setTelefono(dto.telefono());

        // Extraer datos del DTO del usuario
        AutenticacionUsuarioDto usuarioDto = dto.usuario();
        if (usuarioDto == null || usuarioDto.login() == null || usuarioDto.clave() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Crear entidad Usuario con datos reales
        Usuario usuario = new Usuario();
        usuario.setLogin(usuarioDto.login());
        usuario.setClave(passwordEncoder.encode(usuarioDto.clave())); // cifrar clave
        usuario.setRol(usuarioDto.rol());
        usuario.setEmpleado(empleado); // vínculo empleado -> usuario

        // Establecer relación bidireccional empleado -> usuario
        empleado.setUsuario(usuario);

        // 🔥 Guardar ambas entidades
        usuarioRepository.save(usuario); // guardas usuario primero
        Empleado empleadoGuardado = empleadoRepository.save(empleado); // luego empleado

        // Crear DTO de respuesta ocultando la clave
        AutenticacionUsuarioDto usuarioRespuesta = new AutenticacionUsuarioDto(
                usuario.getId(),
                usuario.getLogin(),
                "********", // ocultamos clave
                usuario.getRol()
        );

        EmpleadoUsuarioDto respuesta = new EmpleadoUsuarioDto(
                empleadoGuardado.getId(),
                empleadoGuardado.getNombreEmpleado(),
                empleadoGuardado.getCargo(),
                empleadoGuardado.getTelefono(),
                usuarioRespuesta
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }


    @Transactional
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoUsuarioDto empleadoUsuarioDto) {
        Optional<Empleado> optionalEmpleado = empleadoRepository.findById(id);
        if (optionalEmpleado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Empleado empleado = optionalEmpleado.get();
        empleado.actualizarEmpleado(empleadoUsuarioDto);

        //Actualiza entidad usuario
        if (empleadoUsuarioDto.usuario() != null) {
            Usuario usuario = empleado.getUsuario();
            if (usuario == null) {
                usuario = new Usuario();
                usuario.setEmpleado(empleado);
            }
            usuario.setId(empleadoUsuarioDto.usuario().id());
            usuario.setLogin(empleadoUsuarioDto.usuario().login());
            usuario.setRol(empleadoUsuarioDto.usuario().rol());

            if (empleadoUsuarioDto.usuario().clave() != null && !empleadoUsuarioDto.usuario().clave().isBlank()) {
                usuario.setClave(passwordEncoder.encode(empleadoUsuarioDto.usuario().clave())); // Reencripta la nueva clave
            }
            usuarioRepository.save(usuario);
        }
        empleadoRepository.save(empleado);
        return ResponseEntity.ok(empleado);
    }

    /*
        empleado.actualizarEmpleado(new EmpleadoUsuarioDto());
            empleadoRepository.save(empleado);
    var datosEmpleado = new EmpleadoDto(
            empleado.getId(),
            empleado.getNombreEmpleado(),
            empleado.getCargo(),
            empleado.getTelefono()
    );
            return ResponseEntity.ok(datosEmpleado);
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
     */

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        empleadoRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoUsuarioDto>consultaEmpleadoId(@PathVariable Long id){
        Optional<Empleado>empleadoConsultado = empleadoRepository.findById(id);
        if (empleadoConsultado.isPresent()){
            Empleado empleado = empleadoConsultado.get();
            Usuario usuario = empleado.getUsuario(); // Obtener la entidad relacionada
            AutenticacionUsuarioDto usuarioDto = null;
            if (usuario != null) {
                usuarioDto = new AutenticacionUsuarioDto(
                        usuario.getId(),
                        usuario.getLogin(),
                        "********", // nunca envíes la clave real
                        usuario.getRol()
                );
            }
            EmpleadoUsuarioDto empleadoUsuarioDto = new EmpleadoUsuarioDto(
                    empleado.getId(),
                    empleado.getNombreEmpleado(),
                    empleado.getCargo(),
                    empleado.getTelefono(),
                    usuarioDto
            );
            return ResponseEntity.ok(empleadoUsuarioDto);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
