package System.Registradora.controller;

import System.Registradora.domain.*;
import System.Registradora.domain.repositories.*;
import System.Registradora.domain.usuario.Usuario;
import System.Registradora.domain.usuario.UsuarioRepository;
import System.Registradora.dto.*;
import System.Registradora.infra.security.AuthUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5500") // Ajusta el puerto si es diferente
@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

//    public VentaController(VentaRepository ventaRepository, ProductoRepository productoRepository){
//        this.ventaRepository = ventaRepository;
//        this.productoRepository = productoRepository;
//    }

    @PostMapping
    @Transactional
    public ResponseEntity<VentaDto> registrarVenta(@RequestBody VentaDto ventaDto) {
        // 1️⃣ Crear la venta sin detalles y guardarla
        Venta venta = new Venta();
        venta.setDescuento(ventaDto.descuento());
        venta.setFormaDePago(ventaDto.formaDePago());

        Cliente cliente = clienteRepository.findById(ventaDto.clienteId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no encontrado"));
        venta.setCliente(cliente);

        String login = AuthUtils.getLoginFromToken();
        Usuario usuario = usuarioRepository.findUsuarioByLogin(login);

        if (usuario == null || usuario.getEmpleado() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario sin empleado asignado");
        }
        Empleado empleado = usuario.getEmpleado();
        venta.setEmpleado(empleado);

        venta = ventaRepository.save(venta); // ✅ Guardar antes de usar en DetalleVenta
        final Venta ventaFinal = venta; // ✅ Asegurar que `venta` es reconocida en la lambda

        // 2️⃣ Crear los detalles de la venta y calcular totalVenta
        AtomicReference<Double> totalVenta = new AtomicReference<>(0.0); // ✅ Manejo seguro en lambda

        List<DetalleVenta> detalles = ventaDto.detalles().stream().map(detalleDto -> {
            Optional<Producto> productoOptional = productoRepository.findByNombreProducto(detalleDto.nombreProducto());
            if (productoOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado: " + detalleDto.nombreProducto());
            }
            Producto producto = productoOptional.get();

            // 🛠 Verificar qué valores se están recibiendo en el backend
            System.out.println("Procesando producto: " + detalleDto.nombreProducto());
            System.out.println("Precio en BD: " + producto.getPrecioVenta());
            System.out.println("Precio enviado por el frontend: " + detalleDto.precioUnitario());

            // 🛠 Si el precio en la BD es 0, tomar el precio desde el frontend (detalleDto.precioUnitario)
            Double precioUnitario = producto.getPrecioVenta();
            if (precioUnitario == 0) {
                if (detalleDto.precioUnitario() == null || detalleDto.precioUnitario() <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe ingresar un precio válido para el producto: " + detalleDto.nombreProducto());
                }
                precioUnitario = detalleDto.precioUnitario(); // ✅ Usar el precio ingresado manualmente
            }

            if (producto.getStock() < detalleDto.cantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para el producto: " + producto.getNombreProducto());
            }

            double subtotal = precioUnitario * detalleDto.cantidad(); // ✅ Calcular subtotal
            totalVenta.updateAndGet(v -> v + subtotal); // ✅ Actualizar total de forma segura

            // ✅ Crear DetalleVenta correctamente asociado a la Venta
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVenta(ventaFinal); // ✅ Relacionar con la Venta
            detalleVenta.setProducto(producto);
            detalleVenta.setCantidad(detalleDto.cantidad());
            detalleVenta.setPrecioUnitario(precioUnitario);

            producto.setStock(producto.getStock() - detalleDto.cantidad());
            productoRepository.save(producto);

            // Registrar movimiento de salida
            Movimiento movimiento = new Movimiento();
            movimiento.setCantidad(detalleDto.cantidad());
            movimiento.setTipoMovimiento(TipoMovimiento.Salida);
            movimiento.setFechaMovimiento(new Date());
            movimiento.setDetalleVenta(detalleVenta);
            movimiento.setProducto(producto);
            movimientoRepository.save(movimiento);

            return detalleVenta;
        }).toList();

        // 3️⃣ Guardar los detalles de la venta
        detalleVentaRepository.saveAll(detalles);

        // 4️⃣ Actualizar totalVenta y guardar cambios
        venta.setTotalVenta(totalVenta.get()); // ✅ Obtener valor final de la venta
        ventaRepository.save(venta); // ✅ Guardar cambios en la base de datos

        // 5️⃣ Crear respuesta DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new VentaDto(
                        venta.getId(),
                        venta.getFechaVenta(),
                        venta.getTotalVenta(),
                        venta.getDescuento(),
                        venta.getFormaDePago(),
                        venta.getCliente().getId(),
                        detalles.stream().map(detalle -> new DetalleVentaDto(
                                detalle.getId(),
                                detalle.getProducto().getNombreProducto(),
                                detalle.getCantidad(),
                                detalle.getPrecioUnitario()
                        )).toList()
                )
        );
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarVentas(@PageableDefault(size = 20) Pageable paginacion) {
        Page<VentaDto> ventas = ventaRepository.listarVentas(paginacion).map(venta ->
                new VentaDto(
                        venta.getId(),
                        venta.getFechaVenta(),
                        venta.getTotalVenta(),
                        venta.getDescuento(),
                        venta.getFormaDePago(),
                        venta.getCliente().getId(),
                        venta.getDetalles().stream().map(detalle ->
                                new DetalleVentaDto(
                                        detalle.getId(),
                                        detalle.getProducto().getNombreProducto(),
                                        detalle.getCantidad(),
                                        detalle.getPrecioUnitario())
                        ).collect(Collectors.toList())
                ));

        // Construir una estructura JSON más estable
        Map<String, Object> response = new HashMap<>();
        response.put("contenido", ventas.getContent());  // Lista de ventas
        response.put("paginaActual", ventas.getNumber());
        response.put("totalPaginas", ventas.getTotalPages());
        response.put("totalElementos", ventas.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDto> listarVentasId(@PathVariable Long id) {
        Optional<Venta> ventaConsultada = ventaRepository.findById(id);
        if (ventaConsultada.isPresent()) {
            Venta venta = ventaConsultada.get();
            VentaDto ventaDto = new VentaDto(
                    venta.getId(),
                    venta.getFechaVenta(),
                    venta.getTotalVenta(),
                    venta.getDescuento(),
                    venta.getFormaDePago(),
                    venta.getCliente().getId(),
                    venta.getDetalles().stream().map(detalle ->
                            new DetalleVentaDto(
                                    detalle.getId(),
                                    detalle.getProducto().getNombreProducto(),
                                    detalle.getCantidad(),
                                    detalle.getPrecioUnitario())
                    ).collect(Collectors.toList())
            );
            return ResponseEntity.ok(ventaDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ventas-diarias/{fecha}")
    public ResponseEntity<List<TotalVentasPorDiaDTO>> obtenerVentasPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha) {

        List<TotalVentasPorDiaDTO> ventas = ventaRepository.obtenerTotalVentasPorFecha(fecha);

        return ventas.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(ventas);
    }

    /*
    // Endpoint para obtener las ventas diarias
    @GetMapping("/diarias")
    public ResponseEntity<List<VentaDto>> obtenerVentasDiarias() {
        List<Venta> ventasDiarias = ventaRepository.obtenerTotalVentasDiarias();

        if (!ventasDiarias.isEmpty()) {
            List<VentaDto> ventasDto = ventasDiarias.stream().map(venta ->
                    new VentaDto(
                            venta.getId(),
                            venta.getFechaVenta(),
                            venta.getTotalVenta(),
                            venta.getDescuento(),
                            venta.getFormaDePago(),
                            venta.getDetalles().stream().map(detalle ->
                                    new DetalleVentaDto(
                                            detalle.getId(),
                                            detalle.getProducto().getNombreProducto(),
                                            detalle.getCantidad(),
                                            detalle.getPrecioUnitario()
                                    )).collect(Collectors.toList())
                    )).collect(Collectors.toList());
            return ResponseEntity.ok(ventasDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    */

    @GetMapping("/ventas-diarias/total/{fecha}")
    public ResponseEntity<TotalVentasPorDiaDTO> obtenerTotalVentasFecha(@PathVariable @DateTimeFormat
            (iso = DateTimeFormat.ISO.DATE) Date fecha){
        List<TotalVentasPorDiaDTO> totalVentas = ventaRepository.obtenerTotalVentasPorFecha(fecha);
        return totalVentas.isEmpty() ? ResponseEntity.notFound().build() :
                ResponseEntity.ok(totalVentas.get(0));
    }

    // Endpoint para obtener el total de ventas del día
    @GetMapping("/diarias/total")
    public ResponseEntity<TotalVentasPorDiaDTO> obtenerTotalVentasDiarias() {
        TotalVentasPorDiaDTO totalVentas = ventaRepository.obtenerTotalVentasDiarias();

        if (totalVentas != null) {
            return ResponseEntity.ok(totalVentas);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/por-producto")
    public ResponseEntity<List<ConsultaVentasPorProductoDto>> consultaVentasPorProducto(
            @RequestParam(value = "nombreProducto") String nombreProducto) {

        // 1️⃣ Normalizar el nombre del producto buscado
        String nombreProductoNormalizado = normalizarTexto(nombreProducto);

        // 2️⃣ Obtener la fecha actual en formato Date
        Date fechaActual = new Date();

        // 3️⃣ Consultar todas las ventas
        Page<Venta> todasLasVentas = ventaRepository.listarVentas(PageRequest.of(0, 100));

        // 4️⃣ Filtrar solo las ventas del día actual y por producto
        List<ConsultaVentasPorProductoDto> ventasFiltradas = todasLasVentas.stream()
                .filter(venta -> esMismaFecha(venta.getFechaVenta(), fechaActual)) // Compara las fechas
                .flatMap(venta -> venta.getDetalles().stream()
                        .filter(detalle -> normalizarTexto(detalle.getProducto().getNombreProducto())
                                .contains(nombreProductoNormalizado))
                        .map(detalle -> new ConsultaVentasPorProductoDto(
                                detalle.getProducto().getNombreProducto(),
                                detalle.getCantidad(),
                                detalle.getPrecioUnitario(),
                                detalle.getId(),
                                detalle.getVenta().getFechaVenta()
                        )))
                .collect(Collectors.toList());

        System.out.println("Ventas filtradas: " + ventasFiltradas);
        // 5️⃣ Validar si hay resultados
        if (ventasFiltradas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

//        // 6️⃣ Calcular el total vendido
//        double totalVendido = ventasFiltradas.stream()
//                .mapToDouble(detalle -> detalle.cantidad() * detalle.precioUnitario())
//                .sum();
//
//        // 7️⃣ Construir la respuesta con el mensaje
//        RespuestaConsultaVentasDto respuesta = new RespuestaConsultaVentasDto(
//                "El total del producto vendido hoy es de: " + totalVendido,
//                ventasFiltradas
//        );

        return ResponseEntity.ok(ventasFiltradas);
    }

    private boolean esMismaFecha(Date fechaVenta, Date fechaActual) {
        LocalDate fechaVentaLD = fechaVenta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fechaActualLD = fechaActual.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return fechaVentaLD.isEqual(fechaActualLD);
    }

    /**
     * Método para normalizar el texto:
     * - Convierte a minúsculas
     * - Elimina espacios al inicio y final
     * - Quita espacios internos
     */
    private String normalizarTexto(String texto) {
        return texto.trim().toLowerCase().replaceAll("\\s+", "");
    }

//    @GetMapping("/por-producto")
//    public ResponseEntity<List<RespuestaConsultaVentasDto>> consultaVentasPorProducto(@RequestParam(value = "nombreProducto") String nombreProducto) {
//        // 1️⃣ Reutilizamos listarVentas() sin filtro
//        Page<Venta> todasLasVentas = ventaRepository.listarVentas(PageRequest.of(0, 100));
//        // 2️⃣ Filtramos las ventas que contienen el producto buscado
//        List<ConsultaVentasPorProductoDto> ventasFiltradas = todasLasVentas.stream()
//                .flatMap(venta -> venta.getDetalles().stream()
//                        .filter(detalle -> detalle.getProducto().getNombreProducto().equalsIgnoreCase(nombreProducto))
//                        .map(detalle -> new ConsultaVentasPorProductoDto(
//                                detalle.getProducto().getNombreProducto(),
//                                detalle.getCantidad(),
//                                detalle.getPrecioUnitario(),
//                                detalle.getId(),
//                                detalle.getVenta().getFechaVenta()
//                        )))
//                .toList();
//
//        // 3️⃣ Validamos si hay resultados
//        if (ventasFiltradas.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        // 4️⃣ Calculamos el total sumando (cantidad * precioUnitario)
//        double totalVendido = ventasFiltradas.stream()
//                .mapToDouble(detalle -> detalle.cantidad() * detalle.precioUnitario())
//                .sum();
//
//        // 5️⃣ Construimos la respuesta con el mensaje y el total
//        RespuestaConsultaVentasDto respuesta = new RespuestaConsultaVentasDto(
//                "El total del producto vendido hoy es de: " + totalVendido,
//                ventasFiltradas
//        );
//
//        return ResponseEntity.ok(Collections.singletonList(respuesta));
//    }

//    @DeleteMapping("/{id}")
//    @Transactional
//    public ResponseEntity<VentaDto>EliminarVenta(@PathVariable Long id){
//        Optional<Venta>optionalVenta = ventaRepository.findById(id);
//        if (optionalVenta.isPresent()){
//            Venta venta = optionalVenta.get();
//            venta.
//        }
//    }
}
