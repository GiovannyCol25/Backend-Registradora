package System.Registradora.domain;

import System.Registradora.dto.ClienteDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cliente")
@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private long telefono;

    @OneToMany(mappedBy = "cliente")
    private List<Venta> ventas;

    /*
    public Cliente(){}

    public Cliente(ClienteDto datos){
        this.id = datos.id();
        this.nombre = datos.nombre();
        this.telefono = datos.telefono();
    }

     */

    public void actualizarCliente(ClienteDto datos){
        if (datos.nombre() != null && !datos.nombre().isEmpty()){
            this.nombre = datos.nombre();
        }
        if (datos.telefono() != 0){
            this.telefono = datos.telefono();
        }
    }
}
