package System.Registradora.domain;

import System.Registradora.dto.ProveedorDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proveedores")
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String razonSocial;
    private String nit;
    private long telefono;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    private List<Compra> compras = new ArrayList<>();

    /*
    public Proveedor(){}

    public Proveedor(ProveedorDto datos){
        this.id = datos.id();
        this.razonSocial = datos.razonSocial();
        this.nit = datos.nit();
        this.telefono = datos.telefono();
    }

     */

    public void actualizarProveedor (ProveedorDto datos){
        if (datos.razonSocial() != null && datos.razonSocial().isEmpty()){
            this.razonSocial = datos.razonSocial();
        }
        if (datos.nit() != null && datos.nit().isEmpty()){
            this.nit = datos.nit();
        }
        if (datos.telefono() != 0){
            this.telefono = datos.telefono();
        }
    }
}
