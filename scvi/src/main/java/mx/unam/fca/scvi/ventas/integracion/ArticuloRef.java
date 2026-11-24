package mx.unam.fca.scvi.ventas.integracion;

import java.math.BigDecimal;

/**
 * Vista del artículo tal como lo ve Ventas: precio y existencias vienen de Inventario.
 *
 * Subsistema: Ventas.
 *
 * @author Álvarez Aguilar Saúl Antonio Equipo 2
 * @author Pérez Flores Roberto Carlos Equipo 2
 * @author Pérez Muñoz Diego Equipo 2
 *
 * Fecha creación: 15-11-2026
 *
 * Fecha modificación: 23-11-2026
 */
public record ArticuloRef(long articuloId, String codigo, String nombre, BigDecimal precio, int existencias) {
}
