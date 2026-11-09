package mx.unam.fca.scvi.inventario.repositorio;

import mx.unam.fca.scvi.inventario.modelo.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contrato de acceso a datos de la tabla Articulo.
 * Responsabilidad principal: proveer las operaciones de persistencia del
 * catálogo de artículos; Spring Data JPA genera la implementación.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
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
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    boolean existsByCodigoBarras(String codigoBarras);
}
