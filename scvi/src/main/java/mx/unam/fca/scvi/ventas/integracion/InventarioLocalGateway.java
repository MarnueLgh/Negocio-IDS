package mx.unam.fca.scvi.ventas.integracion;

import java.util.Optional;
import mx.unam.fca.scvi.ventas.common.Reglas;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Implementación de InventarioGateway sobre la tabla Articulo del subsistema de Inventario
 * (script scvi/sql/01_crear_articulo.sql). Para Ventas el código del artículo es su codigoBarras.
 * Las existencias se descuentan con una sola sentencia condicionada para que nunca queden en negativo.
 */
@Repository
public class InventarioLocalGateway implements InventarioGateway {

    private static final String COLUMNAS = "SELECT articuloId, codigoBarras, nombre, precio, existencias FROM Articulo ";

    private static final RowMapper<ArticuloRef> MAPPER = (rs, n) -> new ArticuloRef(
            rs.getLong("articuloId"),
            rs.getString("codigoBarras"),
            rs.getString("nombre"),
            rs.getBigDecimal("precio"),
            rs.getInt("existencias"));

    private final JdbcTemplate jdbc;

    public InventarioLocalGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Solo se venden artículos activos del catálogo. */
    @Override
    public Optional<ArticuloRef> buscarPorCodigo(String codigo) {
        return jdbc.query(COLUMNAS + "WHERE codigoBarras = ? AND activo = TRUE", MAPPER, codigo).stream().findFirst();
    }

    @Override
    public Optional<ArticuloRef> buscarPorId(long articuloId) {
        return jdbc.query(COLUMNAS + "WHERE articuloId = ?", MAPPER, articuloId).stream().findFirst();
    }

    @Override
    public void descontar(long articuloId, int cantidad) {
        Reglas.exigir(cantidad > 0, "La cantidad a descontar debe ser mayor a cero");
        int filas = jdbc.update("UPDATE Articulo SET existencias = existencias - ? "
                + "WHERE articuloId = ? AND existencias >= ?", cantidad, articuloId, cantidad);
        Reglas.exigir(filas == 1, "Existencias insuficientes para el artículo " + articuloId
                + " (el inventario no puede quedar en negativo)");
    }

    @Override
    public void reponer(long articuloId, int cantidad) {
        Reglas.exigir(cantidad > 0, "La cantidad a reponer debe ser mayor a cero");
        jdbc.update("UPDATE Articulo SET existencias = existencias + ? WHERE articuloId = ?",
                cantidad, articuloId);
    }
}
