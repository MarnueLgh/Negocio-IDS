package mx.unam.fca.scvi.ventas.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Consultas SQL nativas para los reportes (agrupaciones con varias tablas) devueltas como filas columna -> valor. */
public final class Consultas {

    private Consultas() {
    }

    /** Alias de columna escrito en la consulta: AS `alias`. */
    private static final Pattern ALIAS = Pattern.compile("AS `(\\w+)`");

    /**
     * Ejecuta un SELECT nativo con parámetros '?' en orden y devuelve cada fila como mapa alias -> valor.
     * Los alias se toman del texto de la consulta (AS `alias`, en el orden del SELECT) y no del controlador JDBC,
     * porque cada base de datos los devuelve con distintas mayúsculas (H2 en pruebas, MySQL en producción).
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> filas(EntityManager em, String sql, Object... parametros) {
        Query consulta = em.createNativeQuery(numerar(sql), Tuple.class);
        for (int i = 0; i < parametros.length; i++) {
            consulta.setParameter(i + 1, parametros[i]);
        }
        List<String> alias = new ArrayList<>();
        Matcher coincidencia = ALIAS.matcher(sql);
        while (coincidencia.find()) {
            alias.add(coincidencia.group(1));
        }
        List<Map<String, Object>> filas = new ArrayList<>();
        for (Tuple tupla : (List<Tuple>) consulta.getResultList()) {
            List<TupleElement<?>> elementos = tupla.getElements();
            Map<String, Object> fila = new LinkedHashMap<>();
            for (int i = 0; i < elementos.size(); i++) {
                String nombre = alias.size() == elementos.size() ? alias.get(i) : elementos.get(i).getAlias();
                fila.put(nombre, tupla.get(i));
            }
            filas.add(fila);
        }
        return filas;
    }

    /** Convierte cualquier número devuelto por SQL a BigDecimal (0 si es nulo). */
    public static BigDecimal decimal(Object valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        if (valor instanceof BigDecimal decimal) {
            return decimal;
        }
        return new BigDecimal(valor.toString());
    }

    /** Cambia cada '?' por ?1, ?2, ... (parámetros posicionales de JPA). */
    private static String numerar(String sql) {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (char c : sql.toCharArray()) {
            if (c == '?') {
                sb.append('?').append(++n);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
