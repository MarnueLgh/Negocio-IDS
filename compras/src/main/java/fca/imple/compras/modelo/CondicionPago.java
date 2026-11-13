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
 * Representa una condición de pago establecida por un proveedor en el subsistema de compras.
 * Responsabilidad principal: Almacenar las políticas comerciales (límite de crédito, plazo en días)
 * para ser aplicadas en las compras a crédito y la gestión de cuentas por pagar.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Está vinculada directamente a una entidad Proveedor.
 * - Sus reglas son consultadas por CuentaPorPagar para determinar la fecha límite de pago y el cálculo de intereses.
 * Reglas de negocio:
 * - El límite de crédito y los días de crédito no pueden ser valores negativos.
 * - La tasa de interés solo se aplica cuando una cuenta por pagar supera los días de crédito establecidos.
 *
 * @author Islas López Isis Jezabel, Óscar González Saucedo Equipo 1
 * Fecha creación: 23-10-2026
 * Fecha modificación: 07-11-2026
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CondicionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCondicion;

    private int idProveedor;

    private double limiteCredito;

    private int diasCredito;

    /**
     * Descripción: Determina el factor diario de recargo moratorio aplicable sobre las compras a crédito en función de la tasa de interés pactada con el proveedor.
     * Información de Modificación: No modifica información ni altera el estado de la clase.
     * Información de Tablas, Entidades o Componentes: Corresponde a la tabla CondicionPago y es consumido por CuentaPorPagar.
     * Reglas de Negocio: Solo genera factor de recargo si el proveedor otorga días de crédito (plazo > 0). Si la compra es de contado, devuelve 0.0.
     *
     * @return factor decimal diario de cobro de interés moratorio, o 0.0 si no aplica.
     * @throws IllegalStateException si los días de crédito o el límite de crédito contienen valores negativos.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 07-11-2026
     */
    public double cobroIntereses() {
        if (this.diasCredito < 0 || this.limiteCredito < 0) {
            throw new IllegalStateException("Las condiciones comerciales de crédito no pueden contener valores negativos.");
        }
        if (this.diasCredito > 0) {
            return this.tasa() / 30.0;
        }
        return 0.0;
    }

    /**
     * Descripción: Obtiene el porcentaje de tasa de interés mensual aplicable por morosidad acordada con el proveedor.
     * Información de Modificación: No modifica información ni altera el estado de la clase.
     * Información de Tablas, Entidades o Componentes: Entidad CondicionPago vinculada a CuentaPorPagar.
     * Reglas de Negocio: La tasa base moratoria estándar del subsistema es del 3% mensual para compras con crédito vigente.
     *
     * @return valor decimal de la tasa de interés moratorio mensual configurada.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 07-11-2026
     */
    public double tasa() {
        if (this.diasCredito > 0) {
            return 0.03;
        }
        return 0.0;
    }
}