package mx.unam.fca.scvi.ventas.integracion;

import java.util.Optional;

/**
 * Contrato síncrono con el subsistema de Inventario (la consistencia es inmediata: no se vende lo que no existe).
 * Lo implementa InventarioLocalGateway sobre la tabla Articulo de Inventario.
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
public interface InventarioGateway {

    Optional<ArticuloRef> buscarPorCodigo(String codigo);

    Optional<ArticuloRef> buscarPorId(long articuloId);

    /** Descuenta existencias. Lanza ReglaNegocioException si quedarían negativas. */
    void descontar(long articuloId, int cantidad);

    /** Regresa existencias (cancelaciones). */
    void reponer(long articuloId, int cantidad);
}
