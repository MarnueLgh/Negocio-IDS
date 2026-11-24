package mx.unam.fca.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

/**
 * Entidad que mapea la tabla Cliente (script scvi/sql/04_crear_ventas_zonas_empleados_clientes.sql).
 * El RFC es único y el cliente pertenece a una sola zona.
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
public class Cliente {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long clienteId;
    String nombre;
    String rfc;
    String domicilio;
    String telefono;
    String telefonoAlterno;
    String contacto;
    BigDecimal limiteCredito;
    int diasCredito;
    boolean activo;
    Long zonaId;

    /** Un monto/plazo es válido si no rebasa el límite de crédito ni los días de crédito otorgados. */
    public boolean validarCredito(BigDecimal monto, int dias) {
        return monto.compareTo(limiteCredito) <= 0 && dias <= diasCredito;
    }
}
