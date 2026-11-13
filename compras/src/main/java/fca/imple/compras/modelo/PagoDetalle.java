package fca.imple.compras.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
  * Representar el detalle de un pago en el subsistema de compras.
  * Responsabilidad principal: Registrar de forma específica cómo se distribuye cada abono
  * dentro de un pago general, separando el dinero destinado a cubrir la deuda principal
  * de aquel que va dirigido a los intereses
  * Subsistema: Compras
  * Relacióm con otros componentes:
  * - Forma parte de un pago general realizado al proveedor.
  * - Se asocia directamente con la cuenta por pagar específica a la que se le está aplicando el abono.
  * Reglas de negocio:
  * - Cada línea de detalle debe estar vinculada obligatoriamente a una cuenta por pagar existente.
  * - El monto del abono se desglosa de manera independiente entre el saldo principal y los intereses acumulados.
  * @author Islas López Isis Jezabel Equipo 1
  * Fecha de Creación: 23-20-2026
  * Fecha de Modificación: 02-11-2026
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class PagoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPagoDetalle;

    private int idPago;

    private int idCuentaPorPagar;

    private double abonoAlSaldo;

    private double abonoAlInteres;

}
