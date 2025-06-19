package System.Registradora.controller;

import System.Registradora.domain.Producto;
import System.Registradora.domain.repositories.ProductoRepository;
import System.Registradora.dto.ProductoDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

        @PostMapping
        @Transactional
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<ProductoDto>> registrarProducto (@RequestBody List<ProductoDto> productoDtos){
            List<ProductoDto> productosCreados = productoDtos.stream().map(productoDto -> {
                Producto producto = new Producto(productoDto);

                producto = productoRepository.save(producto);

                return new ProductoDto(
                        producto.getId(),
                        producto.getNombreProducto(),
                        producto.getPrecioVenta(),
                        producto.getCodigoBarras()
                );
            }).toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(productosCreados);
        }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ProductoDto>actualizarProductos(@PathVariable Long id, @RequestBody ProductoDto productoDto){
        Optional<Producto> optionalProducto = productoRepository.findById(id);
        if (optionalProducto.isPresent()){
            Producto producto = optionalProducto.get();
            producto.actualizarDatos(productoDto);
            productoRepository.save(producto);
            ProductoDto productoActualizado = new ProductoDto(
                    producto.getId(),
                    producto.getNombreProducto(),
                    producto.getPrecioVenta(),
                    producto.getCodigoBarras()
            );
            return ResponseEntity.ok(productoActualizado);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto>consultaProductoId(@PathVariable Long id){
        Optional<Producto> productoConsultado = productoRepository.findByIdProducto(id);
        if (productoConsultado.isPresent()){
            Producto producto = productoConsultado.get();
            ProductoDto productoDto = new ProductoDto(
                    producto.getId(),
                    producto.getNombreProducto(),
                    producto.getPrecioVenta(),
                    producto.getCodigoBarras()
            );
            return ResponseEntity.ok(productoDto);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ProductoDto>> consultaProductoVariosNombres(@PathVariable String nombre) {
        String productoProcesado = nombre.trim().toLowerCase();
        List<Producto> productosConsultados = productoRepository.findByNombreProductoContainingIgnoreCase(nombre);

        if (!productosConsultados.isEmpty()) {
            List<ProductoDto> productosDto = productosConsultados.stream()
                    .map(producto -> new ProductoDto(
                            producto.getId(),
                            producto.getNombreProducto(),
                            producto.getPrecioVenta(),
                            producto.getCodigoBarras()
                    )).collect(Collectors.toList());

            return ResponseEntity.ok(productosDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @GetMapping("/nombre/{nombre}")
//    public ResponseEntity<ProductoDto>consultaProductoNombre(@PathVariable String nombre){
//        String productoProcesado = nombre.trim().toLowerCase();
//        Optional<Producto> productoConsultado = productoRepository.findByNombreProducto(nombre);
//        if (productoConsultado.isPresent()){
//            Producto producto = productoConsultado.get();
//            ProductoDto productoDto = new ProductoDto(
//                    producto.getId(),
//                    producto.getNombreProducto(),
//                    producto.getPrecioVenta(),
//                    producto.getCodigoBarras()
//            );
//            return ResponseEntity.ok(productoDto);
//        }else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/codigoBarras/{codigoBarras}")
    public ResponseEntity<ProductoDto>consultaProductoCodigoBarras(@PathVariable("codigoBarras") Long codigoBarras){
        Optional<Producto>productoBuscado = productoRepository.findByCodigoBarras(codigoBarras);
        if (productoBuscado.isPresent()){
            Producto producto = productoBuscado.get();
            ProductoDto productoDto = new ProductoDto(
                    producto.getId(),
                    producto.getNombreProducto(),
                    producto.getPrecioVenta(),
                    producto.getCodigoBarras()
            );
            return ResponseEntity.ok(productoDto);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
