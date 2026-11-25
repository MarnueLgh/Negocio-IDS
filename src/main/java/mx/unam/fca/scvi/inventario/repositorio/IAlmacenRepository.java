package mx.unam.fca.scvi.inventario.repositorio;

import mx.unam.fca.scvi.inventario.modelo.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Contrato para el acceso a datos de la entidad Almacen.
 * Responsabilidad principal: Proveer las operaciones de persistencia
 * para los almacenes en la base de datos MySQL.
 *
 * @author Equipo 3
 *
 * Fecha creación: 07-11-2026
 * Fecha modificación: 07-11-2026
 */
@Repository
public interface IAlmacenRepository extends JpaRepository<Almacen, Long> {

    /**
     * Comprueba si existe un almacén registrado con el nombre indicado.
     *
     * @param nombre nombre del almacén a buscar.
     * @return true si existe un almacén con ese nombre, false en caso contrario.
     */
    boolean existsByNombre(String nombre);
}