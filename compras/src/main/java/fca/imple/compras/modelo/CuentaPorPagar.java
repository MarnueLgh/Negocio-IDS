package fca.imple.compras.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Date;

/**
  * Representa una cuenta por pagar en el subsistema de compras.
  * Responsabilidad principal: Registra y administra los datos y procesos financieros de los datos 
  de los proveedores en las cuentas por pagar.
  * Subsistema: Compras
  * Relación con otros componentes: 
  - Se relaciona con las clases "CuentasPorPagar", "PagoProveedor" y "PagoDetalle"
  - Tiene relacion con "CuentasPorPagarDAO", los cuales su objeto de Acceso de Datos para consultar,
    eliminar y dar de alta (dependiendo de la actividad que se quiera hacer en las cuentas por pagar)
  * Reglas de negocio:
  - Calcula la fecha límite de pago considerando las condiciones y días de crédito del proveedor
  - Se genera de forma automatica al registrar la cuenta
  - Toda alta de cuenta por pagar se genera en automatico al registrar una compra.
  - Se exige un motivación de cancelación registrando el usuario y la fecha de la cancelación.
  * @author Islas López Isis Jezabel, Becerra Cruz Lizeth Adriana Equipo 1
  * Fecha de Creación: 23-10-2026
  * Fecha de Modificación: 07-11-2026
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class CuentaPorPagar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCuentaPorPagar;

    private Date fechaGeneracion;

    private Date fechaLimite;

    private double montoOriginal;

    private double interesAcomulado;

    private double saldo;

    private String estado;

    private int idCompra;

    private int idProveedor;

    private String motivoCancelacion;

    private Date fechaCancelacion;

    private int idUsuarioCancelo;

    /**
     * Descripción: Calcula el interes acumulado de la cuenta por pagar.
     * @throws: ArithmeticException Excepción lanzada en caso de error durante la operación aritmética con montos.
     * Información de Modificación: Creación de la lógica inicial de cálculo de recargos
     * Información de Tablas, Entidades o Componentes: Entidad CuentaPorPagar
     * Reglas de Negocio: Cálculo basado en tasas e intereses acumulados del proveedor.
     * Reglas de Negocio
     * Fecha de modificación: 07-11-2026
     */
    public double calcularInteresAcomulado(){

        return 0;

    }

    /**
     * Descripción: Aplica un abono directo al saldo pendiente de la cuenta por pagar reduciendo la deuda vigente.
     * @param monto El valor monetario que se abonará al saldo actual de la cuenta.
     * @return void No retorna valor, modifica directamente el atributo de saldo.
     * @throws IllegalArgumentException Si el monto ingresado es negativo o mayor al saldo actual.
     * Información de Modificación: Implementación de la lógica de pagos parciales y abonos al saldo.
     * Información de Tablas, Entidades o Componentes: Tabla cuentasporpagar, entidad CuentaPorPagar.
     * Reglas de Negocio: El abono disminuye el saldo total de la cuenta por pagar de forma proporcional.
     * @author: Islas López Isis Jezabel, Becerra Cruz Lizeth Adriana Equipo 1.
     * Fecha de creación: 23-10-2026
     * Fecha de modificación: 07-11-2026
     */

    public void abonarAlSaldo(double monto){

    }

    /**
     * Descripción: Establece o actualiza el valor del interés acumulado en la cuenta por pagar.
     * @param v El nuevo valor monetario para el interés acumulado.
     * @return void No retorna valor.
     * @throws IllegalArgumentException Si el valor proporcionado es menor a cero.
     * Información de Modificación: Creación del método modificador para la gestión de intereses.
     * Información de Tablas, Entidades o Componentes: Tabla cuentasporpagar, entidad CuentaPorPagar.
     * Reglas de Negocio: Los intereses acumulados se actualizan con base en las políticas de crédito del proveedor.
     * @author: Islas López Isis Jezabel, Becerra Cruz Lizeth Adriana Equipo 1.
     * Fecha de creación: 23-10-2026
     * Fecha de modificación: 07-11-2026
     **/

    public void setInteresAcumulado(double v) {
    }
}
