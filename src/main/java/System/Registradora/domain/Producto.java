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

    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public List<DetalleCompra> getDetalleCompras() {
        return detalleCompras;
    }

    public void setDetalleCompras(List<DetalleCompra> detalleCompras) {
        this.detalleCompras = detalleCompras;
    }
}