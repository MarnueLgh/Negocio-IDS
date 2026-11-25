package mx.unam.fca.scvi.inventario.repositorio;

import org.springframework.data.jpa.repository.Query;
import mx.unam.fca.scvi.inventario.modelo.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Contrato de acceso a datos de la tabla Articulo.
 * Responsabilidad principal: proveer las operaciones de persistencia del
 * catálogo de artículos; Spring Data JPA genera la implementación.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3, Islas López Isis Jeazbel Equipo 1.
 * Fecha creación: 09-11-2026
 * Fecha modificación: 23-11-2026
 */
public interface IArticuloRepositorio extends JpaRepository<Articulo, Long> {

    /**
     * Comprueba si ya existe un artículo con el código de barras indicado.
     * El nombre está en inglés porque Spring Data arma la consulta a partir
     * de las palabras clave del método (existsBy).
     *
     * @param codigoBarras código de barras que se desea verificar.
     * @return true si la tabla Articulo ya tiene ese código de barras.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     * Fecha creación: 09-11-2026
     * Fecha modificación: 09-11-2026
     */
    boolean existsByCodigoBarras(String codigoBarras);

    /**
     * Suma la cantidad indicada a las existencias de un artículo en la tabla
     * Articulo con una sola sentencia UPDATE. La suma se hace en la base de
     * datos para que dos recepciones simultáneas del mismo artículo no se
     * pisen (no se lee el valor, se suma y se escribe desde Java).
     * Debe llamarse dentro de una transacción.
     *
     * @param articuloId identificador del artículo cuyas existencias cambian.
     * @param cantidad   unidades que se suman; positiva en una recepción.
     * @return número de filas actualizadas: 1 si el artículo existe, 0 si no.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     * Fecha creación: 09-11-2026
     * Fecha modificación: 09-11-2026
     */
    @Modifying
    @Query("UPDATE Articulo a SET a.existencias = a.existencias + :cantidad WHERE a.articuloId = :articuloId")
    int sumarExistencias(@Param("articuloId") Long articuloId, @Param("cantidad") Integer cantidad);

    /**
     * Obtiene todos los artículos ordenados por existencias de mayor a menor
     * para el reporte de existencias del inventario.
     *
     * @return lista de artículos ordenados descendentemente según su campo existencias.
     * @author Islas López Isis Jezabel Equipo 1
     * Fecha creación: 23-11-2026
     * Fecha modificación: 23-11-2026
     */
    List<Articulo> findAllByOrderByExistenciasDesc();

    /**
     * Obtiene los artículos cuyo nivel de existencias es menor o igual a su
     * existencia mínima, ordenados de menor a mayor para el reporte de bajo stock.
     *
     * @return lista de artículos con bajo stock ordenados ascendentemente según su
     * campo existencias.
     * @author Islas López Isis Jezabel Equipo 1
     * Fecha creación: 23-11-2026
     * Fecha modificación: 23-11-2026
     */
    @Query("SELECT a FROM Articulo a WHERE a.existencias <= a.existenciasMinimas ORDER BY a.existencias ASC")
    List<Articulo> findBajoStockOrdenadoAsc();

    /**
     * Calcula el costo total del inventario sumando el valor de cada artículo
     * (costo por existencias) y devolviendo cero si no existen registros.
     *
     * @return valor monetario total acumulado del inventario.
     * @author Islas López Isis Jezabel Equipo 1
     * Fecha creación: 23-11-2026
     * Fecha modificación: 23-11-2026
     */
    @Query("SELECT COALESCE(SUM(a.costo * a.existencias), 0) FROM Articulo a")
    Double calcularCostoTotalInventario();

}