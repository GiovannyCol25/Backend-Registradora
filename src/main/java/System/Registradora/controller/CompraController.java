package System.Registradora.controller;

import System.Registradora.domain.*;
import System.Registradora.domain.repositories.CompraRepository;
import System.Registradora.domain.repositories.DetalleCompraRepository;
import System.Registradora.domain.repositories.ProductoRepository;
import System.Registradora.dto.CompraDto;
import System.Registradora.dto.DetalleCompraDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<CompraDto> registrarCompra (@RequestBody CompraDto compraDto){
        Compra compra = new Compra();
        compra = compraRepository.save(compra);
        final Compra compraFinal = compra;
        AtomicReference<Double> totalCompra = new AtomicReference<>(0.0);

        List<DetalleCompra> detalleCompra = compraDto.detalleCompraDtoList().stream().map(detalleCompraDto -> {
            Optional<Producto> productoOptional = productoRepository.findByNombreProducto(detalleCompraDto.nombreProducto());
            if (productoOptional.isPresent()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado: " + detalleCompraDto.nombreProducto());
            }
            Producto producto = productoOptional.get();

            double subtotal = detalleCompraDto.precioUnitario() * detalleCompraDto.cantidad();
            totalCompra.updateAndGet(c -> c + subtotal);

            DetalleCompra detalleCompra1 = new DetalleCompra();
            detalleCompra1.setCompra(compraFinal);
            detalleCompra1.setProducto(producto);
            detalleCompra1.setCantidad(detalleCompraDto.cantidad());
            detalleCompra1.setPrecioUnitario(detalleCompraDto.precioUnitario());

            return detalleCompra1;
        }).toList();

        detalleCompraRepository.saveAll(detalleCompra);

        compra.setTotalCompra(totalCompra.get());
        compraRepository.save(compra);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CompraDto(
                        compra.getId(),
                        compra.getFechaCompra(),
                        compra.getTotalCompra(),
                        detalleCompra.stream().map(detalleCompra1 -> new DetalleCompraDto(
                                detalleCompra1.getId(),
                                detalleCompra1.getProducto().getNombreProducto(),
                                detalleCompra1.getCantidad(),
                                detalleCompra1.getPrecioUnitario()
                        )).toList()
                )
        );
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarCompras(@PageableDefault(size = 10)Pageable paginacion){
        Page<CompraDto> compras = compraRepository.listarCompras(paginacion).map(compra ->
                new CompraDto(
                        compra.getId(),
                        compra.getFechaCompra(),
                        compra.getTotalCompra(),
                        compra.getDetalleCompraList().stream().map(detalle ->
                                new DetalleCompraDto(
                                        detalle.getId(),
                                        detalle.getProducto().getNombreProducto(),
                                        detalle.getCantidad(),
                                        detalle.getPrecioUnitario()
                                )).collect(Collectors.toList())
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("contenido", compras.getContent());
        response.put("paginaActual", compras.getNumber());
        response.put("totalPaginas", compras.getTotalPages());
        response.put("totalElementos", compras.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraDto> listarComprasId (@PathVariable Long id){
        Optional<Compra> compraConsultada = compraRepository.findById(id);
        if (compraConsultada.isPresent()) {
            Compra compra = compraConsultada.get();
            CompraDto compraDto = new CompraDto(
                    compra.getId(),
                    compra.getFechaCompra(),
                    compra.getTotalCompra(),
                    compra.getDetalleCompraList().stream().map(detalleCompra ->
                            new DetalleCompraDto(
                                    detalleCompra.getId(),
                                    detalleCompra.getProducto().getNombreProducto(),
                                    detalleCompra.getCantidad(),
                                    detalleCompra.getPrecioUnitario())
                    ).collect(Collectors.toList())
            );
            return ResponseEntity.ok(compraDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
