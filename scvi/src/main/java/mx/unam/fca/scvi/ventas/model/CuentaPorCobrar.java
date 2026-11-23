package mx.unam.fca.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Entidad que mapea la tabla CuentaPorCobrar.
 * Se genera al facturar un pedido a crédito y controla el saldo pendiente y la fecha límite de pago.
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
public class CuentaPorCobrar {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long cuentaId;
    LocalDate fechaCuenta;
    LocalDate fechaFacturacion;
    LocalDate fechaLimite;
    @Enumerated(EnumType.STRING)//Jakarta
    EstadoCuenta estado;
    BigDecimal importeOriginal;
    BigDecimal importePendiente;
    Long facturaId;
    Long clienteId;

    /** Días de retraso respecto a la fecha límite de pago (0 si aún está en tiempo). */
    public long calcularDiasRetraso(LocalDate hoy) {
        return hoy.isAfter(fechaLimite) ? ChronoUnit.DAYS.between(fechaLimite, hoy) : 0;
    }
}
