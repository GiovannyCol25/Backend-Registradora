package System.Registradora.domain;

import System.Registradora.dto.CompraDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date fechaCompra;
    @NotNull
    private Double totalCompra;

    @Column(name = "numero_factura")
    private String numeroFactura;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleCompra> detalleCompraList;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    // Relación con empleado
    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

/*
    public Compra(){}

    public Compra(CompraDto datos){
        this.id = datos.id();
        this.fechaCompra = datos.fechaCompra();
        this.totalCompra = datos.totalCompra();
    }
*/
}