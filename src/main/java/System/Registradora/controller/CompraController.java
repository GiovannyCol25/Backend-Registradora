package System.Registradora.controller;

import System.Registradora.domain.*;
import System.Registradora.domain.repositories.*;
import System.Registradora.dto.CompraDto;
import System.Registradora.dto.DetalleCompraDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
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

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<CompraDto> registrarCompra (@RequestBody CompraDto compraDto){
        System.out.println("⚙️ DTO recibido: empleadoId = " + compraDto.empleadoId());

        if (compraDto.empleadoId() == null || compraDto.empleadoId() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El empleadoId es null");
        }

        Compra compra = new Compra();
        compra.setFechaCompra(new Date());
        compra.setNumeroFactura(compraDto.numeroFactura());

        Proveedor proveedor = proveedorRepository.findById(compraDto.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveedor no encontrado"));
        compra.setProveedor(proveedor);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Empleado empleado = empleadoRepository.findById(compraDto.empleadoId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Empleado no autenticado"));
        compra.setEmpleado(empleado);

        compra = compraRepository.save(compra);

        double totalCompra = 0.0;

        List<DetalleCompra> detalles = new ArrayList<>();
        for (DetalleCompraDto dto : compraDto.detalleCompraDtoList()) {
            Producto producto = productoRepository.findById(dto.productoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado"));

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(dto.cantidad());
            detalle.setPrecioUnitario(dto.precioUnitario());

            detalleCompraRepository.save(detalle);
            detalles.add(detalle);

            producto.setStock(producto.getStock() + dto.cantidad());
            productoRepository.save(producto);

            //Registrar movimiento de entrada
            Movimiento movimiento = new Movimiento();
            movimiento.setProducto(producto);
            movimiento.setDetalleCompra(detalle);
            movimiento.setCantidad(dto.cantidad());
            movimiento.setTipoMovimiento(TipoMovimiento.Entrada);
            movimiento.setFechaMovimiento(new Date());
            movimientoRepository.save(movimiento);

            totalCompra += dto.cantidad() * dto.precioUnitario();
        }

        compra.setTotalCompra(totalCompra);
        compraRepository.save(compra);

        List<DetalleCompraDto> detallesRespuesta = detalles.stream().map(detalle -> new DetalleCompraDto(
                detalle.getId(),
                detalle.getProducto().getId(),
                detalle.getProducto().getNombreProducto(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario()
        )).toList();

        CompraDto respuesta = new CompraDto(
                compra.getId(),
                compra.getFechaCompra(),
                compra.getTotalCompra(),
                compra.getNumeroFactura(),
                proveedor.getId(),
                empleado.getId(),
                detallesRespuesta
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarCompras(@PageableDefault(size = 10)Pageable paginacion){
        Page<CompraDto> compras = compraRepository.listarCompras(paginacion).map(compra ->
                new CompraDto(
                        compra.getId(),
                        compra.getFechaCompra(),
                        compra.getTotalCompra(),
                        compra.getNumeroFactura(),
                        compra.getProveedor().getId(),
                        compra.getEmpleado().getId(),
                        compra.getDetalleCompraList().stream().map(detalle ->
                                new DetalleCompraDto(
                                        detalle.getId(),
                                        detalle.getProducto().getId(),
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
                    compra.getNumeroFactura(),
                    compra.getProveedor().getId(),
                    compra.getEmpleado().getId(),
                    compra.getDetalleCompraList().stream().map(detalleCompra ->
                            new DetalleCompraDto(
                                    detalleCompra.getId(),
                                    detalleCompra.getProducto().getId(),
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
