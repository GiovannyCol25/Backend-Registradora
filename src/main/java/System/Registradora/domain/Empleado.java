package System.Registradora.domain;

import System.Registradora.domain.usuario.Usuario;
import System.Registradora.dto.EmpleadoDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_empleado")
    private String nombreEmpleado;

    private String cargo;
    private long telefono;

    @OneToMany(mappedBy = "empleado")
    private List<Venta> ventas;

    @OneToMany(mappedBy = "empleado")
    private List<Compra> compras = new ArrayList<>();

    @OneToOne(mappedBy = "empleado")
    private Usuario usuario;

    public Empleado(){}

    public Empleado(EmpleadoDto datos){
        this.id = datos.id();
        this.nombreEmpleado = datos.nombreEmpleado();
        this.cargo = datos.cargo();
        this.telefono = datos.telefono();
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
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

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }
}
