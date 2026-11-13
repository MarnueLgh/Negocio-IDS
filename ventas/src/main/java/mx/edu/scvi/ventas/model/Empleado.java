package mx.edu.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

/** Empleado de ventas. Vendedor: pertenece a una zona. Cajero (VendedorCaja): sin zona. */
@Entity //Jakarta
//Lombok
@Data //Getters and setters
@NoArgsConstructor //Constructor vacío
@AllArgsConstructor //Constructor con todos
@ToString //To String
public class Empleado {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long empleadoId;
    String nombre;
    String telefono;
    String domicilio;
    @Enumerated(EnumType.STRING)//Jakarta
    TipoEmpleado tipo;
    BigDecimal porcentajeComision;
    boolean activo;
    Long zonaId;
}
