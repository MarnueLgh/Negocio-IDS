package fca.imple.compras.servicio;

import fca.imple.compras.modelo.CuentaPorPagar;
import fca.imple.compras.repositorio.CuentasPorPagarRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

/**
 *
 - Propósito: Proveer los servicios y la lógica de negocio para la gestión, control y consulta de las cuentas por pagar del subsistema de compras.
 - Responsabilidad principal: Orquestar las operaciones de persistencia relacionadas con la generación de adeudos, cancelaciones autorizadas y consultas filtradas por periodos y proveedores.
 - Subsistema: Compras
 - Relación con otros componentes: Se comunica directamente con la interfaz CuentasPorPagarRepositorio para interactuar con la base de datos y manipular la entidad CuentaPorPagar.
 - Reglas de negocio: Valida la existencia de las cuentas antes de cancelarlas, registra de forma obligatoria el motivo y el usuario responsable de la cancelación, y gestiona los filtros por fechas límite y estados financieros.
 @author Becerra Cruz Lizeth Adriana Equipo 1
 - Fecha creación: 07-11-2026
 - Fecha modificación: 09-11-2026
 */

@Service
public class CuentasPorPagarServicio {

    @Autowired
    private CuentasPorPagarRepositorio cuentasPorPagarRepositorio;

    /**
     - Inserta y almacena una nueva cuenta por pagar en el sistema.
     - Información de Modificación: Creación de la lógica inicial para el registro transaccional de cuentas por pagar.
     - Información de Tablas, Entidades o Componentes: Tabla cuentasporpagar, entidad CuentaPorPagar, repositorio CuentasPorPagarRepositorio.
     - Reglas de Negocio: Se registra automáticamente al concretar una compra, estableciendo su saldo inicial y estado pendiente.
     @param cuenta Objeto de tipo CuentaPorPagar con la información financiera a registrar.
     @return boolean Retorna true si la cuenta fue guardada exitosamente, o false si ocurre un error.
     @author Becerra Cruz Lizeth Adriana Equipo1
     - Fecha creación: 08-11-2026
     - Fecha modificación: 09-11-2026
     */

    public boolean generarCuenta(CuentaPorPagar cuenta) {
        try {
            cuentasPorPagarRepositorio.save(cuenta);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     - Cancela una cuenta por pagar existente actualizando su estado, motivo, usuario responsable y fecha de cancelación.
     - Información de Modificación: Implementación de la lógica de cancelación con trazabilidad de usuario y motivos.
     - Información de Tablas, Entidades o Componentes: Tabla cuentasporpagar, entidad CuentaPorPagar.
     - Reglas de Negocio: Se exige un motivo de cancelación, el registro del ID del gerente de compras que autoriza y la fecha exacta en la que se efectúa.
     @param idCuenta Identificador único de la cuenta por pagar a cancelar.
     @param motivo Razón o justificación por la cual se cancela la cuenta.
     @param idGerenteCompras Identificador del gerente de compras que ejecuta la cancelación.
     @return boolean Retorna true si la cancelación se aplicó con éxito, o false si la cuenta no existe o falla la operación.
     @author Becerra Cruz Lizeth Adriana Equipo1
     - Fecha creación: 08-11-2026
     - Fecha modificación: 09-11-2026
     */

    public boolean cancelarCuenta(int idCuenta, String motivo, int idGerenteCompras) {
        try {
            CuentaPorPagar cuenta = cuentasPorPagarRepositorio.findById(idCuenta).orElse(null);
            if (cuenta != null) {
                cuenta.setEstado("CANCELADA");
                cuenta.setMotivoCancelacion(motivo);
                cuenta.setIdUsuarioCancelo(idGerenteCompras);
                cuenta.setFechaCancelacion(new java.sql.Date(System.currentTimeMillis()));
                cuentasPorPagarRepositorio.save(cuenta);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     - Consulta y obtiene el detalle completo de una cuenta por pagar a partir de su identificador.
     - Información de Modificación: Creación de la consulta individual por ID.
     - Información de Tablas, Entidades o Componentes: Tabla cuentasporpagar, entidad CuentaPorPagar.
     - Reglas de Negocio: Permite visualizar los saldos, fechas límite y estados actuales de un adeudo específico.
     @param idCuentaPorPagar Identificador único de la cuenta por pagar a consultar.
     @return CuentaPorPagar Objeto con la información de la cuenta encontrada, o null si no existe.
     @author Becerra Cruz Lizeth Adriana Equipo1
     - Fecha creación: 08-11-2026
     -Fecha modificación: 09-11-2026
     */
    public CuentaPorPagar consultarDetalleCuentaPorPagar(int idCuentaPorPagar) {
        return cuentasPorPagarRepositorio.findById(idCuentaPorPagar).orElse(null);
    }

    /**
     - Consulta la lista de cuentas por pagar pendientes filtradas por un periodo de fechas límite.
     - Información de Modificación: Integración de consulta basada en rangos de fechas (Between).
     - Información de Tablas, Entidades o Componentes: Tabla cuentasporpagar, método del repositorio findByFechaLimiteBetween.
     - Reglas de Negocio: Facilita la auditoría y control de adeudos que vencen dentro de un intervalo de tiempo determinado.
     @param inicio Fecha de inicio del periodo de búsqueda.
     @param fin Fecha de término del periodo de búsqueda.
     @return List<CuentaPorPagar> Lista de cuentas por pagar comprendidas en el periodo indicado.
     @author Becerra Cruz Lizeth Adriana Equipo1
     - Fecha creación: 08-11-2026
     - Fecha modificación: 09-11-2026
     */
    public List<CuentaPorPagar> consultarCuentasPendientesPorPeriodo(Date inicio, Date fin) {
        return cuentasPorPagarRepositorio.findByFechaLimiteBetween(inicio.toLocalDate(), fin.toLocalDate());
    }

    /**
     - Consulta las cuentas por pagar en estado pendiente asociadas a un proveedor específico dentro de un periodo de fechas límite.
     - Información de Modificación: Integración de consulta compuesta por proveedor, estado y rango de fechas.
     - Información de Tablas, Entidades o Componentes: Tabla cuentasporpagar, método findByIdProveedorAndEstadoAndFechaLimiteBetween.
     - Reglas de Negocio: Permite restringir la búsqueda de adeudos vigentes por cada proveedor comercial y un rango de vencimiento.
     @param idProveedor Identificador único del proveedor a consultar.
     @param inicio Fecha de inicio del periodo.
     @param fin Fecha de fin del periodo.
     @return List<CuentaPorPagar> Lista de cuentas pendientes que cumplen con los criterios de proveedor y fechas.
     @author Becerra Cruz Lizeth Adriana Equipo1
     - Fecha creación: 08-11-2026
     - Fecha modificación: 09-11-2026
     */

    public List<CuentaPorPagar> consultarCuentasPendientesPorProveedorYPeriodo(int idProveedor, Date inicio, Date fin) {
        return cuentasPorPagarRepositorio.findByIdProveedorAndEstadoAndFechaLimiteBetween(
                idProveedor, "PENDIENTE", inicio.toLocalDate(), fin.toLocalDate()
        );
    }
}