package System.Registradora.domain;

import System.Registradora.dto.ProductoDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "productos")
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties("ventas")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombreProducto;
    private Double precioVenta;
    @Column(unique = true)
    private String codigoBarras;

    @Column(nullable = false)
    private Integer stock = 0;


    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DetalleVenta> detallesVenta;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DetalleCompra> detalleCompras;

    /*
    public Producto(){}
     */

    public Producto(ProductoDto datos) {
        this.id = datos.id();
        this.nombreProducto = datos.nombreProducto();
        this.precioVenta = datos.precioVenta();
        this.codigoBarras = datos.codigoBarras();
    }

    public void actualizarDatos(ProductoDto datos){
        if (datos.nombreProducto() != null && !datos.nombreProducto().isEmpty()){
            this.nombreProducto = datos.nombreProducto();
        }
        if (datos.precioVenta() != null){
            this.precioVenta = datos.precioVenta();
        }
        if (datos.codigoBarras() != null){
            this.codigoBarras = datos.codigoBarras();
        }
    }
}