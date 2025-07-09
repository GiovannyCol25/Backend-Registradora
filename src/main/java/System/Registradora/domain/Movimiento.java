package System.Registradora.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movimientos")
@Entity
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fecha_movimiento")
    private Date fechaMovimiento;
    private int cantidad;
    @Enumerated(EnumType.STRING) // Almacena como texto: 'ENTRADA' o 'SALIDA'
    private TipoMovimiento tipoMovimiento;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_detalle_venta")
    private DetalleVenta detalleVenta;

    @ManyToOne
    @JoinColumn(name = "id_detalle_compra")
    private DetalleCompra detalleCompra;

}
