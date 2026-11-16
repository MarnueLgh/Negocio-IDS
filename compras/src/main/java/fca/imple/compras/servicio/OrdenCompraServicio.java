package fca.imple.compras.servicio;

import fca.imple.compras.modelo.OrdenCompra;
import fca.imple.compras.modelo.OrdenCompraDetalle;
import fca.imple.compras.repositorio.OrdenCompraDetalleRepositorio;
import fca.imple.compras.repositorio.OrdenCompraRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase de servicio que implementa la lógica de negocio y persistencia para OrdenCompra.
 * Propósito: Administrar la gestión de solicitudes de compra y consultas
 * Responsabilidad principal: Empezar con una orden solicitada antes de que se realice en una compra.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Implementa la interfaz IOrdenCompraServicio.
 * - Usa OrdenCompraRepositorio y OrdenCompraDetalleRepositorio.
 * Reglas de negocio:
 * - Verifica la existencia previa de la orden antes de actualizar o consultar.
 *
 * @author Óscar González Saucedo Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 08-11-2026
 */
@Service
public class OrdenCompraServicio implements IOrdenCompraServicio {

    @Autowired
    private OrdenCompraRepositorio ordenCompraRepositorio;

    @Autowired
    private OrdenCompraDetalleRepositorio ordenCompraDetalleRepositorio;

    /**
     * Descripción: Registra y persiste una nueva orden de compra en la base de datos.
     * Información de Modificación: Inserta un nuevo registro en la tabla OrdenCompra.
     * Información de Tablas, Entidades o Componentes: Tabla OrdenCompra y repositorio OrdenCompraRepositorio.
     * Reglas de Negocio: No se permite registrar órdenes con referencia nula.
     *
     * @param ordenCompra objeto cabecera de la orden de compra a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean insertarOrdenCompra(OrdenCompra ordenCompra) {
        if (ordenCompra == null) return false;
        ordenCompraRepositorio.save(ordenCompra);
        return true;
    }

    /**
     * Descripción: Actualiza la información de una orden de compra existente en el sistema.
     * Información de Modificación: Modifica el registro correspondiente en la tabla OrdenCompra.
     * Información de Tablas, Entidades o Componentes: Tabla OrdenCompra y repositorio OrdenCompraRepositorio.
     * Reglas de Negocio: La orden y su identificador no deben ser nulos.
     *
     * @param ordenCompra objeto con los datos modificados de la orden.
     * @return true si la actualización se aplicó correctamente, false si no se encontró o el objeto es nulo.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean modificarOrdenCompra(OrdenCompra ordenCompra) {
        if (ordenCompra == null || ordenCompra.getIdOrdenCompra() == null) return false;
        if (ordenCompraRepositorio.existsById(ordenCompra.getIdOrdenCompra())) {
            ordenCompraRepositorio.save(ordenCompra);
            return true;
        }
        return false;
    }

    /**
     * Descripción: Elimina del sistema una orden de compra identificada por su identificador primario.
     * Información de Modificación: Elimina el registro correspondiente en la tabla OrdenCompra.
     * Información de Tablas, Entidades o Componentes: Tabla OrdenCompra y repositorio OrdenCompraRepositorio.
     * Reglas de Negocio: Valida la existencia previa del identificador antes de invocar la eliminación.
     *
     * @param idOrdenCompra identificador numérico de la orden a borrar.
     * @return true si se eliminó con éxito, false si el registro no fue localizado.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean borrarOrdenCompra(int idOrdenCompra) {
        if (ordenCompraRepositorio.existsById(idOrdenCompra)) {
            ordenCompraRepositorio.deleteById(idOrdenCompra);
            return true;
        }
        return false;
    }
    /**
     * Descripción: Consulta la información de cabecera de una orden de compra mediante su llave primaria.
     * Información de Modificación: Operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Tabla OrdenCompra y repositorio OrdenCompraRepositorio.
     * Reglas de Negocio: Recupera la entidad OrdenCompra coincidente o null si no existe.
     *
     * @param idOrdenCompra identificador numérico de la orden solicitada.
     * @return entidad OrdenCompra recuperada o null en caso de no existir.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public OrdenCompra consultarDetalleOrdenCompra(int idOrdenCompra) {
        return ordenCompraRepositorio.findById(idOrdenCompra).orElse(null);
    }

    /**
     * Descripción: Consulta la lista de partidas o artículos desglosados dentro de una orden de compra.
     * Información de Modificación: Operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Tabla OrdenCompraDetalle y repositorio OrdenCompraDetalleRepositorio.
     * Reglas de Negocio: Recupera las líneas de partidas asociadas al identificador de la orden de compra.
     *
     * @param idOrdenCompra identificador numérico de la orden a desglosar.
     * @return lista de renglones u objetos OrdenCompraDetalle pertenecientes a la orden.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public List<OrdenCompraDetalle> consultarPartidasOrdenCompra(int idOrdenCompra) {
        return ordenCompraDetalleRepositorio.findByIdOrdenCompra(idOrdenCompra);
    }
    /**
     * Descripción: Recupera las órdenes de compra asociadas a un proveedor que se encuentran en estado Pendiente.
     * Información de Modificación: Operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Tabla OrdenCompra y repositorio OrdenCompraRepositorio.
     * Reglas de Negocio: Filtra exclusivamente por el identificador del proveedor y el estado comercial 'Pendiente'.
     *
     * @param idProveedor identificador numérico del proveedor.
     * @return lista de órdenes de compra pendientes por surtir.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public List<OrdenCompra> consultarOrdenesPendientesPorProveedor(int idProveedor) {
        return ordenCompraRepositorio.findByIdProveedorAndEstado(idProveedor, "Pendiente");
    }
    /**
     * Descripción: Localiza las órdenes de compra en estado Pendiente que solicitan un artículo determinado.
     * Información de Modificación: Operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Tablas OrdenCompra y OrdenCompraDetalle mediante consulta nativa.
     * Reglas de Negocio: Cruza los renglones pendientes de recepción asociados al identificador del artículo.
     *
     * @param idArticulo identificador del artículo en inventario.
     * @return lista de órdenes de compra pendientes vinculadas a dicho artículo.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public List<OrdenCompra> consultarOrdenesPendientesPorArticulo(int idArticulo) {
        return ordenCompraRepositorio.consultarOrdenesPendientesPorArticulo(idArticulo);
    }

    /**
     * Descripción: Consulta el histórico de órdenes de compra canceladas para un proveedor dentro de un rango de fechas.
     * Información de Modificación: Operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Tabla OrdenCompra y repositorio OrdenCompraRepositorio.
     * Reglas de Negocio: Filtra por estado 'Cancelada' y rango de fechas de creación para el proveedor especificado.
     *
     * @param inicio fecha inicial del rango en formato LocalDate.
     * @param fin fecha final del rango en formato LocalDate.
     * @param idProveedor identificador único del proveedor.
     * @return lista de órdenes de compra canceladas durante el periodo.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public List<OrdenCompra> consultarOrdenesCanceladas(LocalDate inicio, LocalDate fin, int idProveedor) {
        return ordenCompraRepositorio.findByFechaCreacionBetweenAndIdProveedorAndEstado(inicio, fin, idProveedor, "Cancelada");
    }
}