package System.Registradora.domain;

import System.Registradora.dto.CompraDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "compras")
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
    @JsonManagedReference
    private List<DetalleCompra> detalleCompraList;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    // Relación con empleado
    @ManyToOne(optional = true)
    @JoinColumn(name = "empleado_id")
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