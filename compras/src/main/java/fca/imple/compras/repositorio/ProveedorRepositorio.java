package fca.imple.compras.repositorio;

import fca.imple.compras.modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interfaz de repositorio de acceso a datos para la entidad Proveedor.
 * Propósito: Proveer los mecanismos de persistencia y consultas hacia la tabla Proveedor en MySQL.
 * Responsabilidad principal: Gestionar las operaciones CRUD y búsquedas especializadas de los proveedores del sistema.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Se comunica directamente con la entidad Proveedor mediante Spring Data JPA.
 * - Es inyectado en la capa de servicio para coordinar la lógica de negocio.
 * Reglas de negocio:
 * - No permite la duplicidad de registros con el mismo RFC ni identificador único.
 *
 * @author Óscar González Saucedo, Islas López Isis Jezabel Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 08-11-2026
 */
@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Integer> {
    /**
     * Localiza el primer proveedor en el catálogo coincidiendo por su identificador único o por su razón social/nombre.
     * Información de Modificación: Se ajustó a findTop1 para retornar un único resultado y evitar excepciones de unicidad.
     * Información de Tablas, Entidades o Componentes: Consulta la tabla Proveedor.
     * Reglas de Negocio: Permite la recuperación del primer registro coincidente ante cualquiera de los dos criterios de búsqueda.
     *
     * @param idProveedor identificador numérico de la llave primaria del proveedor.
     * @param nombre      cadena con el nombre comercial o razón social a buscar.
     * @return entidad Proveedor localizada (el primer registro coincidente), o null si no existe.
     * @author Óscar González Saucedo, Islas López Isis Jezabel Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    Proveedor findTop1ByIdProveedorOrNombre(Integer idProveedor, String nombre);

    /**
     * Consulta los identificadores únicos de los artículos que han sido suministrados por un proveedor específico.
     * Información de Modificación: Ninguna, operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Realiza un cruce entre las tablas OrdenCompra y OrdenCompraDetalle filtrando por idProveedor.
     * Reglas de Negocio: Obtiene la lista sin duplicados de artículos asociados a las órdenes emitidas al proveedor.
     *
     * @param idProveedor identificador del proveedor a consultar.
     * @return lista de identificadores numéricos de artículos relacionados.
     * @author Óscar González Saucedo, Islas López Isis Jezabel Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 07-11-2026
     */
    @Query(value = "SELECT DISTINCT ocd.idArticulo FROM OrdenCompra oc " +
            "JOIN OrdenCompraDetalle ocd ON oc.idOrdenCompra = ocd.idOrdenCompra " +
            "WHERE oc.idProveedor = :idProveedor", nativeQuery = true)
    List<Integer> consultarArticulosPorProveedor(@Param("idProveedor") Integer idProveedor);

}
