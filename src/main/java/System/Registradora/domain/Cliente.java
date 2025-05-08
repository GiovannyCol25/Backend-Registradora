package System.Registradora.domain;

import System.Registradora.dto.ClienteDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
}
