package fca.imple.compras.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;

/**
 * Representa un pago emitido a un proveedor en el subsistema de compras.
 * Responsabilidad principal: Llevar el registro global de la transacción monetaria realizada a un proveedor.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Pertenece a un Proveedor específico.
 * - Contiene una lista de PagoDetalle para desglosar el abono a cuentas por pagar.
 * Reglas de negocio:
 * - Todo pago registrado debe reflejar un monto total y una fecha válida.
 *
 * @author Islas López Isis Jezabel Equipo 1
 * Fecha creación: 22-10-2026
 * Fecha modificación: 07-11-2026
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PagoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    private int idProveedor;

    private LocalDate fechaPago;

    private double montoTotal;
}