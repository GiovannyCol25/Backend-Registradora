package System.Registradora.controller;

import System.Registradora.domain.Cliente;
import System.Registradora.domain.repositories.ClienteRepository;
import System.Registradora.dto.ClienteDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public ResponseEntity<List<ClienteDto>> listarClientes(){
        List<ClienteDto> consultaClientes = clienteRepository.findAll().stream()
                .map(cliente -> new ClienteDto(
                        cliente.getId(),
                        cliente.getNombre(),
                        cliente.getTelefono()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(consultaClientes);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ClienteDto> crearClientes(@RequestBody ClienteDto clienteDto){
        Cliente cliente = new Cliente(clienteDto);
        cliente = clienteRepository.save(cliente);

        ClienteDto clienteCreado = new ClienteDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getTelefono()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> actualizarCliente(@PathVariable Long id, @RequestBody ClienteDto clienteDto){
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);

        if (optionalCliente.isPresent()){
            Cliente cliente = optionalCliente.get();
            cliente.actualizarCliente(clienteDto);
            clienteRepository.save(cliente);
            var datosCliente = new ClienteDto(
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getTelefono()
            );
            return ResponseEntity.ok(datosCliente);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
