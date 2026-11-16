package mx.edu.scvi.ventas.integracion;

import java.math.BigDecimal;

/** Vista del artículo tal como lo ve Ventas: precio y existencias vienen de Inventario. */
public record ArticuloRef(long articuloId, String codigo, String nombre, BigDecimal precio, int existencias) {
}
