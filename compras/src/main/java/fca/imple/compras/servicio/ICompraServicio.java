package fca.imple.compras.servicio;

import fca.imple.compras.modelo.*;
import java.util.List;

/**
 - Interfaz que define las operaciones del servicio de las compras
 - Propósito: Establecer el contrato estándar de los métodos de negocio para la gestión de compras y administración del catálogo de proveedores.
 - Responsabilidad principal: Definir las firmas de los métodos orientados al alta, modificación, eliminación y consulta de proveedores, así como el manejo de artículos asociados en el subsistema.
 - Subsistema: Compras
 - Relación con otros componentes: Es implementada por la clase CompraServicio y establece la comunicación directa con las entidades de modelo y los repositorios correspondientes.
 - Reglas de negocio: Garantiza que cualquier clase de servicio que gestione compras y proveedores cumpla con las reglas de validación de existencia, control de catálogos y operaciones transaccionales permitidas.
 @author Becerra Cruz Lizeth Adriana Equipo 1
 - Fecha creación: 08-11-2026
 - Fecha modificación: 09-11-2026
 */

public interface ICompraServicio {

    boolean insertarProveedor(Proveedor proveedor);
    boolean modificarProveedor(Proveedor proveedor);
    boolean borrarProveedor(int idProveedor);
    List<Proveedor> consultarListaProveedores();
    Proveedor consultarProveedorPorIdONombre(int idProveedor, String nombre);
    List<Integer> consultarArticulosPorProveedor(int idProveedor);
}
