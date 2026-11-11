package mx.unam.fca.scvi.inventario.repositorio;

import mx.unam.fca.scvi.inventario.modelo.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Contrato para el acceso a datos de la entidad Bodega.
 *
 * @author Equipo 3
 */
@Repository
public interface IBodegaRepositorio extends JpaRepository<Bodega, Long> {

    /**
     * Comprueba si existe una bodega registrada con el nombre indicado.
     * NOTA: Convención de Spring Data JPA en inglés obligatoria.
     *
     * @param nombre Nombre exacto de la bodega a buscar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByNombre(String nombre);
}