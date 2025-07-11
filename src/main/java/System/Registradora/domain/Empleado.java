package System.Registradora.domain;

import System.Registradora.domain.usuario.Usuario;
import System.Registradora.dto.EmpleadoDto;
import System.Registradora.dto.EmpleadoUsuarioDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_empleado")
    private String nombreEmpleado;

    private String cargo;
    private Long telefono;

    @OneToMany(mappedBy = "empleado")
    private List<Venta> ventas;

    @OneToMany(mappedBy = "empleado")
    private List<Compra> compras = new ArrayList<>();

    @OneToOne(mappedBy = "empleado")
    @JsonManagedReference
    private Usuario usuario;

    /*public Empleado(){}

    public Empleado(EmpleadoDto datos){
        this.id = datos.id();
        this.nombreEmpleado = datos.nombreEmpleado();
        this.cargo = datos.cargo();
        this.telefono = datos.telefono();
    }
     */

    /*
    public void actualizarEmpleado(EmpleadoDto datos){
        if (datos.nombreEmpleado() != null && datos.nombreEmpleado().isEmpty()){
            this.nombreEmpleado = datos.nombreEmpleado();
        }
        if (datos.cargo() != null && datos.cargo().isEmpty()){
            this.cargo = datos.cargo();
        }
        if (datos.telefono() != 0){
            this.telefono = datos.telefono();
        }
    }

     */

    public void actualizarEmpleado(EmpleadoUsuarioDto datos) {
        if (datos.nombreEmpleado() != null && !datos.nombreEmpleado().isBlank()) {
            this.nombreEmpleado = datos.nombreEmpleado();
        }
        if (datos.cargo() != null && !datos.cargo().isBlank()) {
            this.cargo = datos.cargo();
        }
        if (datos.telefono() != null && datos.telefono() != 0){
            this.telefono = datos.telefono();
        }
    }
}
