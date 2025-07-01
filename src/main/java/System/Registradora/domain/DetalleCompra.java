package System.Registradora.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_compra")
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int cantidad;
    private Double precioUnitario;

    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /*
    public DetalleCompra (){
        this.compra = compra;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

     */
}
