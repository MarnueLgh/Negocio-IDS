package mx.unam.fca.scvi.inventario.repositorio;

import mx.unam.fca.scvi.inventario.modelo.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contrato de acceso a datos de la tabla Bodega.
 * Responsabilidad principal: consultar las bodegas a las que llega la
 * mercancía (INV-07); Spring Data JPA genera la implementación.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
public interface IBodegaRepositorio extends JpaRepository<Bodega, Long> {
}
