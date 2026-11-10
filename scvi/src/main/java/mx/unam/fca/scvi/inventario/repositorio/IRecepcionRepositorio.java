package mx.unam.fca.scvi.inventario.repositorio;

import mx.unam.fca.scvi.inventario.modelo.Recepcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Contrato de acceso a datos de la tabla Recepcion.
 * Responsabilidad principal: guardar y consultar las llegadas de mercancía
 * (INV-07); Spring Data JPA genera la implementación.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
public interface IRecepcionRepositorio extends JpaRepository<Recepcion, Long> {

    /**
     * Lista todas las recepciones junto con su artículo y su bodega en una
     * sola consulta. Sin el EntityGraph, cada relación LAZY dispararía una
     * consulta extra por recepción al armar la respuesta. El nombre está en
     * inglés porque sobrescribe el método de JpaRepository.
     *
     * @return lista de recepciones con artículo y bodega cargados.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @Override
    @EntityGraph(attributePaths = {"articulo", "bodega"})
    List<Recepcion> findAll();
}
