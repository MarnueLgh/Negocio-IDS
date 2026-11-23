package mx.unam.fca.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

/**
 * Empleado de ventas. Vendedor: pertenece a una zona. Cajero (VendedorCaja): sin zona.
 *
 * Subsistema: Ventas.
 *
 * @author Álvarez Aguilar Saúl Antonio Equipo 2
 * @author Pérez Flores Roberto Carlos Equipo 2
 * @author Pérez Muñoz Diego Equipo 2
 *
 * Fecha creación: 15-11-2026
 *
 * Fecha modificación: 23-11-2026
 */
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
