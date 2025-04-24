package System.Registradora.controller;

import System.Registradora.domain.Empleado;
import System.Registradora.domain.repositories.EmpleadoRepository;
import System.Registradora.dto.EmpleadoDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

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

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<EmpleadoDto> actualizar(@PathVariable Long id, @RequestBody EmpleadoDto empleadoDTO) {
        Optional<Empleado> optionalEmpleado = empleadoRepository.findById(id);
        if (optionalEmpleado.isPresent()) {
            Empleado empleado = optionalEmpleado.get();
            empleado.actualizarEmpleado(empleadoDTO);
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
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        empleadoRepository.deleteById(id);
    }
}
