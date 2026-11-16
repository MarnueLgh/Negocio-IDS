package fca.imple.compras.servicio;

import fca.imple.compras.modelo.PagoDetalle;
import fca.imple.compras.modelo.PagoProveedor;
import fca.imple.compras.repositorio.OrdenCompraRepositorio;
import fca.imple.compras.repositorio.PagoProveedorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase de servicio que implementa la lógica de negocio y operaciones de persistencia para el Pago a un Proveedor
 * Propósito: Gestionar la inserción y consultas del Pago a un Proveedor implementando Spring Data JPA.
 * Responsabilidad principal:
 * Subsistema: Compras
 * Relación con otros componentes:
 * -
 * Reglas de negocio:
 * -
 *
 * @author Islas López Isis Jezabel Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 08-11-2026
 */
@Service
public class PagoProveedorServicio implements IPagoProveedorServicio{

    @Autowired
    private PagoProveedorRepositorio pagoProveedorRepositorio;

    /**
     * Registra un Pago a un proveedor junto con su respectivo desglose de detalles.
     * Información de Modificación: Ninguna, sólo operación de registro.
     * Información de Tablas, Entidades o Componentes: Persiste la entidad PagoProveedor y sus partidas asociadas mediante el repositorio.
     * Reglas de Negocio: Valida que tanto el objeto de pago como la lista de detalles proporcionada no se encuentren nulos o vacíos.
     *
     * @param pago Objeto de tipo PagoProveedor que contiene la información general de la transacción.
     * @param detalles Lista de objetos PagoDetalle que especifican las cuentas y montos abonados.
     * @return true si el registro se completó exitosamente; false si los datos de entrada son inválidos.
     * @author Islas López Isis Jezabel Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean registrarPago(PagoProveedor pago, List<PagoDetalle> detalles){

       if (pago == null || detalles == null || detalles.isEmpty()) {

           return false;

       }

       pagoProveedorRepositorio.save(pago);

       return true;

    }

    /**
     * Consulta el detalle de un Pago a un Proveedor a partir de su ID.
     * Información de Modificación: Ninguna, sólo operación de lectura.
     * Información de Tablas, Entidades o Componentes: Consulta la existencia y el desglose de conceptos en la tabla correspondiente mediante el repositorio.
     * Reglas de Negocio: Permite recuperar todas las partidas y abonos desglosados correspondientes a una transacción de pago específica.
     *
     * @param idPago Identificador único del pago a consultar.
     * @return Lista de objetos PagoDetalle asociados al identificador del pago proporcionado.
     * @author Islas López Isis Jezabel Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public List<PagoDetalle> consultarDetallePago(int idPago){

        return pagoProveedorRepositorio.consultarDetallesPorPago(idPago);

    }

    /**
     * Consulta el historial de pagos realizados a un proveedor específico dentro de un rango de fechas determinado.
     * Información de Modificación: Ninguna, sólo operación de lectura.
     * Información de Tablas, Entidades o Componentes: Consulta los registros en la tabla de pagos filtrando por proveedor y periodo mediante el repositorio.
     * Reglas de Negocio: Valida que las fechas de inicio y fin del periodo no sean nulas antes de ejecutar la consulta de auditoría y control financiero.
     *
     * @param idProveedor Identificador único del proveedor a consultar.
     * @param inicio Fecha inicial del periodo de búsqueda.
     * @param fin Fecha final del periodo de búsqueda.
     * @return Lista de objetos PagoProveedor que coinciden con el proveedor y el rango de fechas, o null si los parámetros de fecha son inválidos.
     * @author Islas López Isis Jezabel Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public List<PagoProveedor> consultarPagosPorProveedorYPeriodo(int idProveedor, LocalDate inicio, LocalDate fin){

        if (inicio == null || fin == null) {

            return null;

        }

        return pagoProveedorRepositorio.findByProveedorAndFechaBetween(idProveedor, inicio, fin);

    }

}
