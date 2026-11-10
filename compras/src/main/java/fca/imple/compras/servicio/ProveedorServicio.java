package fca.imple.compras.servicio;

import fca.imple.compras.modelo.Proveedor;
import fca.imple.compras.repositorio.ProveedorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Clase de servicio que implementa la lógica de negocio y operaciones de persistencia para Proveedor.
 * Propósito: Gestionar el catálogo de proveedores.
 * Responsabilidad principal: Servir como intermediario entre la interfaz de usuario y el repositorio de acceso a datos.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Implementa la interfaz IProveedorServicio.
 * - Usa las operaciones en ProveedorRepositorio.
 * Reglas de negocio:
 * - Valida la existencia del registro antes de ejecutar actualizaciones o bajas.
 *
 * @author Óscar González Saucedo, Islas López Isis Jezabel Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 08-11-2026
 */
@Service
public class ProveedorServicio implements IProveedorServicio {

    @Autowired
    private ProveedorRepositorio proveedorRepositorio;

    /**
     * Descripción: Registra un nuevo proveedor en el sistema y persiste sus datos en MySQL.
     * Información de Modificación: Modifica la tabla Proveedor agregando un nuevo registro.
     * Información de Tablas, Entidades o Componentes: Tabla Proveedor y repositorio ProveedorRepositorio.
     * Reglas de Negocio: No se permite registrar objetos con referencia nula.
     *
     * @param proveedor objeto con la información del proveedor a persistir.
     * @return true si se guardó correctamente, false si el parámetro es nulo.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean insertarProveedor(Proveedor proveedor) {
        if (proveedor == null) return false;
        proveedorRepositorio.save(proveedor);
        return true;
    }

    /**
     * Descripción: Actualiza los datos comerciales y de contacto de un proveedor existente en el sistema.
     * Información de Modificación: Actualiza el registro coincidente en la tabla Proveedor.
     * Información de Tablas, Entidades o Componentes: Tabla Proveedor y repositorio ProveedorRepositorio.
     * Reglas de Negocio: El objeto y su identificador no deben ser nulos, y el registro debe existir previamente en la base de datos.
     *
     * @param proveedor objeto con los datos actualizados del proveedor.
     * @return true si se actualizó con éxito, false si el registro no existe o el parámetro es nulo.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean modificarProveedor(Proveedor proveedor) {
        if (proveedor == null || proveedor.getIdProveedor() == null) return false;
        if (proveedorRepositorio.existsById(proveedor.getIdProveedor())) {
            proveedorRepositorio.save(proveedor);
            return true;
        }
        return false;
    }

    /**
     * Descripción: Da de baja a un proveedor del sistema mediante su identificador primario.
     * Información de Modificación: Elimina el registro correspondiente en la tabla Proveedor.
     * Información de Tablas, Entidades o Componentes: Tabla Proveedor y repositorio ProveedorRepositorio.
     * Reglas de Negocio: Solo se procede con la eliminación si la llave primaria existe en la base de datos.
     *
     * @param idProveedor identificador numérico de la entidad a eliminar.
     * @return true si se eliminó satisfactoriamente, false si no se encontró el registro.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean borrarProveedor(int idProveedor) {
        if (proveedorRepositorio.existsById(idProveedor)) {
            proveedorRepositorio.deleteById(idProveedor);
            return true;
        }
        return false;
    }

    /**
     * Descripción: Obtiene la lista completa de todos los proveedores registrados en el catálogo.
     * Información de Modificación: Operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Tabla Proveedor y repositorio ProveedorRepositorio.
     * Reglas de Negocio: Devuelve todas las entidades Proveedor disponibles en el sistema.
     *
     * @return lista completa de entidades Proveedor.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public List<Proveedor> consultarListaProveedores() {
        return proveedorRepositorio.findAll();
    }

    /**
     * Descripción: Localiza el primer proveedor que coincida por su identificador primario o por su nombre.
     * Información de Modificación: Operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Tabla Proveedor y repositorio ProveedorRepositorio.
     * Reglas de Negocio: Recupera la primera coincidencia evaluando ambos criterios de búsqueda.
     *
     * @param idProveedor identificador numérico del proveedor a buscar.
     * @param nombre razón social o nombre comercial a buscar.
     * @return objeto Proveedor recuperado, o null si no hubo coincidencias.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public Proveedor consultarProveedorPorIdONombre(int idProveedor, String nombre) {
        return proveedorRepositorio.findTop1ByIdProveedorOrNombre(idProveedor, nombre);
    }

    /**
     * Descripción: Consulta los identificadores de artículos asociados a las órdenes de compra emitidas a este proveedor.
     * Información de Modificación: Operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Tablas OrdenCompra y OrdenCompraDetalle mediante ProveedorRepositorio.
     * Reglas de Negocio: Obtiene la lista sin duplicados de los artículos abastecidos por el proveedor.
     *
     * @param idProveedor identificador único del proveedor a consultar.
     * @return lista con los identificadores de los artículos asociados.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public List<Integer> consultarArticulosPorProveedor(int idProveedor) {
        return proveedorRepositorio.consultarArticulosPorProveedor(idProveedor);
    }
}