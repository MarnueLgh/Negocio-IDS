package mx.unam.fca.scvi.ventas.common;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Validaciones pequeñas y reutilizables para los servicios.
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
public final class Reglas {

    private Reglas() {
    }

    public static void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new ReglaNegocioException(mensaje);
        }
    }

    public static String texto(String valor, String campo) {
        exigir(valor != null && !valor.isBlank(), "El campo '" + campo + "' es obligatorio");
        return valor.trim();
    }

    public static final String SIN_DATOS = "No hay datos que concuerden con la busqueda";
    public static final String ID_INEXISTENTE = "El Id buscado no existe";

    /** Consultas: si no hay resultados se avisa con un mensaje claro (HTTP 404) en lugar de una lista vacía. */
    public static <T> List<T> conDatos(List<T> filas) {
        if (filas == null || filas.isEmpty()) {
            throw new NoEncontradoException(SIN_DATOS);
        }
        return filas;
    }

    /** Consultas: el cliente, vendedor, zona o artículo por el que se filtra debe existir (HTTP 404 si no). */
    public static void existe(Optional<?> valor) {
        if (valor.isEmpty()) {
            throw new NoEncontradoException(ID_INEXISTENTE);
        }
    }

    public static <T> T encontrado(Optional<T> valor, String mensaje) {
        return valor.orElseThrow(() -> new NoEncontradoException(mensaje));
    }

    public static void periodo(LocalDate desde, LocalDate hasta) {
        exigir(desde != null && hasta != null && !hasta.isBefore(desde),
                "Periodo inválido: 'desde' y 'hasta' son obligatorios y 'hasta' no puede ser anterior a 'desde'");
    }
}
