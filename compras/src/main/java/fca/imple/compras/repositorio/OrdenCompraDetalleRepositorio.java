package fca.imple.compras.repositorio;

import fca.imple.compras.modelo.OrdenCompraDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interfaz de repositorio de acceso a datos para la entidad OrdenCompraDetalle.
 * Propósito: Gestionar la persistencia y recuperación de las partidas o artículos de las órdenes de compra.
 * Responsabilidad principal: Proporcionar operaciones CRUD y consultas de partidas asociadas a una orden.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Se comunica con la entidad OrdenCompraDetalle y la tabla correspondiente en MySQL.
 * - Es utilizada por OrdenCompraServicio para consultar los renglones específicos de una orden.
 * Reglas de negocio:
 * - Todo detalle consultado debe estar asociado a una orden de compra existente.
 *
 * @author Óscar González Saucedo Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 07-11-2026
 */
@Repository
public interface OrdenCompraDetalleRepositorio extends JpaRepository<OrdenCompraDetalle, Integer> {

    List<OrdenCompraDetalle> findByIdOrdenCompra(Integer idOrdenCompra);
}