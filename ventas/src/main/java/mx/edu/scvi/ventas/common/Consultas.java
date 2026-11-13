package mx.edu.scvi.ventas.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Consultas SQL nativas para los reportes (agrupaciones con varias tablas) devueltas como filas columna -> valor. */
public final class Consultas {

    private Consultas() {
    }

    /** Ejecuta un SELECT nativo con parámetros '?' en orden y devuelve cada fila como mapa alias -> valor. */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> filas(EntityManager em, String sql, Object... parametros) {
        Query consulta = em.createNativeQuery(numerar(sql), Tuple.class);
        for (int i = 0; i < parametros.length; i++) {
            consulta.setParameter(i + 1, parametros[i]);
        }
        List<Map<String, Object>> filas = new ArrayList<>();
        for (Tuple tupla : (List<Tuple>) consulta.getResultList()) {
            Map<String, Object> fila = new LinkedHashMap<>();
            for (TupleElement<?> elemento : tupla.getElements()) {
                fila.put(elemento.getAlias(), tupla.get(elemento.getAlias()));
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
