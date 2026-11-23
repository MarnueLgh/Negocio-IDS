package mx.unam.fca.scvi.ventas.integracion;

import java.util.Optional;

/**
 * Contrato síncrono con el subsistema de Inventario (la consistencia es inmediata: no se vende lo que no existe).
 * Lo implementa InventarioLocalGateway sobre la tabla Articulo de Inventario.
 */
public interface InventarioGateway {

    Optional<ArticuloRef> buscarPorCodigo(String codigo);

    Optional<ArticuloRef> buscarPorId(long articuloId);

    /** Descuenta existencias. Lanza ReglaNegocioException si quedarían negativas. */
    void descontar(long articuloId, int cantidad);

    /** Regresa existencias (cancelaciones). */
    void reponer(long articuloId, int cantidad);
}
