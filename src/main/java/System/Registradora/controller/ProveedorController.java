package System.Registradora.controller;

import System.Registradora.domain.Proveedor;
import System.Registradora.domain.repositories.ProveedorRepository;
import System.Registradora.dto.ProveedorDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<ProveedorDto> registrarProveedor(@RequestBody ProveedorDto proveedorDto) {
        // Convertir el DTO a entidad
        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(proveedorDto.razonSocial());
        proveedor.setNit(proveedorDto.nit());
        proveedor.setTelefono(proveedorDto.telefono());

        // Guardar el proveedor en la base de datos
        proveedor = proveedorRepository.save(proveedor);

        // Convertir la entidad guardada a DTO para la respuesta
        ProveedorDto proveedorCreadoDto = new ProveedorDto(
                proveedor.getId(),
                proveedor.getRazonSocial(),
                proveedor.getNit(),
                proveedor.getTelefono()
        );

        // Retornar la respuesta con estado 201 (CREATED)
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreadoDto);
    }

    @GetMapping
    public ResponseEntity <List<ProveedorDto>> listarProveedores(){
        List<ProveedorDto> listaProveedores = proveedorRepository.findAll().stream()
                .map(proveedor -> new  ProveedorDto(
                        proveedor.getId(),
                        proveedor.getRazonSocial(),
                        proveedor.getNit(),
                        proveedor.getTelefono()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaProveedores);
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDto> actualizar(@PathVariable Long id, @RequestBody ProveedorDto proveedorDto){
        Optional<Proveedor> optionalProveedor = proveedorRepository.findById(id);
        if (optionalProveedor.isPresent()) {
            Proveedor proveedor = optionalProveedor.get();
            proveedor.actualizarProveedor(proveedorDto);
            proveedorRepository.save(proveedor);
            var datosProveedor = new ProveedorDto(
                    proveedor.getId(),
                    proveedor.getRazonSocial(),
                    proveedor.getNit(),
                    proveedor.getTelefono()
            );
            return ResponseEntity.ok(datosProveedor);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ProveedorDto>> consultarProveedorNombre (@PathVariable ("nombre") String razonSocial){
        String nombreBuscado = razonSocial.trim().toLowerCase();
        List<Proveedor> proveedorConsultado = proveedorRepository.findByRazonSocialContainingIgnoreCase(razonSocial);

        if (proveedorConsultado.isEmpty()){
            return ResponseEntity.notFound().build();
        }else {
            List<ProveedorDto> proveedorDtos = proveedorConsultado.stream()
                    .map(proveedor -> new ProveedorDto(
                            proveedor.getId(),
                            proveedor.getRazonSocial(),
                            proveedor.getNit(),
                            proveedor.getTelefono()
                    )).collect(Collectors.toList());
            return ResponseEntity.ok(proveedorDtos);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDto> consultarProveedorId(@PathVariable Long id){
        Optional<Proveedor> proveedorConsultado = proveedorRepository.findById(id);
        if (proveedorConsultado.isPresent()){
            Proveedor proveedor = proveedorConsultado.get();
            ProveedorDto proveedorDto = new ProveedorDto(
                    proveedor.getId(),
                    proveedor.getRazonSocial(),
                    proveedor.getNit(),
                    proveedor.getTelefono()
            );
            return ResponseEntity.ok(proveedorDto);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void eliminar (@PathVariable Long id){proveedorRepository.deleteById(id);}
}
