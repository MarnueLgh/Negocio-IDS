package fca.imple.compras.repositorio;

import fca.imple.compras.modelo.GerenteCompras;
import fca.imple.compras.modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz de repositorio de acceso a datos para la entidad Gerente Compras.
 * Propósito: Proveer los mecanismos de persistencia y consultas hacia la tabla Proveedor en MySQL.
 * Responsabilidad principal: Gestionar las operaciones CRUD y búsquedas de los Gerentes de Compra del sistema.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Se comunica directamente con la entidad GerenteCompra mediante Spring Data JPA.
 * - Es inyectado en la capa de servicio para coordinar la lógica de negocio.
 * Reglas de negocio:
 * - Garantiza la unicidad de los registros mediante el identificador único del gerente de compras.
 *
 * @author Islas López Isis Jezabel Equipo 1
 * Fecha creación: 08-11-2026
 * Fecha modificación: 08-11-2026
 */

@Repository
public interface GerenteComprasRepositorio extends JpaRepository<GerenteCompras, Integer> {

}

