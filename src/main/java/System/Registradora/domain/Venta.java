package System.Registradora.domain;

import System.Registradora.dto.VentaDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;


import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Entity
@Table(name = "ventas")
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonIgnoreProperties("producto")
public class Venta {
    @PrePersist
    protected void onCreate() {
        this.fechaVenta = new Date();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date fechaVenta;

    @NotNull
    private Double totalVenta;
    private Double descuento;
    @NotNull
    private String formaDePago;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalles; // ✅ Nueva relación con DetalleVenta

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

//    private Integer cantidad;
//    @ManyToOne
//    @JoinColumn(name = "producto_id", nullable = false)
//    @JsonBackReference
//    private Producto producto;

/*    public Venta(){}

    public Venta(VentaDto ventaDto){
        this.id = ventaDto.id();
        this.fechaVenta = ventaDto.fechaVenta();
        this.totalVenta = ventaDto.totalVenta();
        this.descuento = ventaDto.descuento();
        this.formaDePago = ventaDto.formaDePago();
    }

 */
}
