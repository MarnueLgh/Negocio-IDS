package fca.imple.compras.servicio;

import fca.imple.compras.modelo.CuentaPorPagar;

import java.sql.Date;
import java.util.List;

/**
 - Interfaz que define las operaciones del servicio de las Cuentas por Pagar
 - Propósito: Establecer el contrato formal de los métodos de negocio para la administración, registro y control de los adeudos y cuentas por pagar a proveedores.
 - Responsabilidad principal: Definir las firmas de los métodos orientados a la generación automática de cuentas, cancelaciones autorizadas con trazabilidad de motivos y consultas filtradas por periodos y proveedores.
 - Subsistema: Compras
 - Relación con otros componentes: Es implementada por la clase CuentasPorPagarServicio y establece el estándar de comunicación con las entidades del modelo financiero y los repositorios de datos.
 - Reglas de negocio: Asegura que cualquier implementación cumpla con las normas de validación de adeudos, exigencia de justificación para cancelaciones y control cronológico de vencimientos.
 @author Becerra Cruz Lizeth Adriana Equipo 1
 - Fecha creación: 08-11-2026
 - Fecha modificación: 09-11-2026
 */

public interface ICuentasPorPagarServicio {

    public boolean generarCuenta(CuentaPorPagar cuenta);
    public boolean cancelarCuenta(int idCuenta, String motivo, int idGerenteCompras);
    public CuentaPorPagar consultarDetalleCuentaPorPagar(int idCuentaPorPagar);
    public List<CuentaPorPagar> consultarCuentasPendientesPorPeriodo(Date inicio, Date fin);
    public List<CuentaPorPagar> consultarCuentasPendientesPorProveedorYPeriodo(int idProveedor, Date inicio, Date  fin);

}
