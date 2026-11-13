package mx.edu.scvi.ventas.integracion;

import java.util.Optional;
import mx.edu.scvi.ventas.common.Reglas;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class InventarioLocalGateway implements InventarioGateway {

    private static final RowMapper<ArticuloRef> MAPPER = (rs, n) -> new ArticuloRef(
            rs.getLong("articulo_id"),
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getBigDecimal("precio"),
            rs.getInt("existencias"));

    private final JdbcTemplate jdbc;

    public InventarioLocalGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<ArticuloRef> buscarPorCodigo(String codigo) {
        return jdbc.query("SELECT * FROM articulo_ref WHERE codigo = ?", MAPPER, codigo).stream().findFirst();
    }

    @Override
    public Optional<ArticuloRef> buscarPorId(long articuloId) {
        return jdbc.query("SELECT * FROM articulo_ref WHERE articulo_id = ?", MAPPER, articuloId).stream().findFirst();
    }

    @Override
    public void descontar(long articuloId, int cantidad) {
        Reglas.exigir(cantidad > 0, "La cantidad a descontar debe ser mayor a cero");
        int filas = jdbc.update("UPDATE articulo_ref SET existencias = existencias - ? "
                + "WHERE articulo_id = ? AND existencias >= ?", cantidad, articuloId, cantidad);
        Reglas.exigir(filas == 1, "Existencias insuficientes para el artículo " + articuloId
                + " (el inventario no puede quedar en negativo)");
    }

    @Override
    public void reponer(long articuloId, int cantidad) {
        Reglas.exigir(cantidad > 0, "La cantidad a reponer debe ser mayor a cero");
        jdbc.update("UPDATE articulo_ref SET existencias = existencias + ? WHERE articulo_id = ?",
                cantidad, articuloId);
    }
}
