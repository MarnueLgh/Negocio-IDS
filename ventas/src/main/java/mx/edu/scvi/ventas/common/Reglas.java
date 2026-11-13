package mx.edu.scvi.ventas.common;

import java.time.LocalDate;
import java.util.Optional;

/** Validaciones pequeñas y reutilizables para los servicios. */
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

    public static <T> T encontrado(Optional<T> valor, String mensaje) {
        return valor.orElseThrow(() -> new NoEncontradoException(mensaje));
    }

    public static void periodo(LocalDate desde, LocalDate hasta) {
        exigir(desde != null && hasta != null && !hasta.isBefore(desde),
                "Periodo inválido: 'desde' y 'hasta' son obligatorios y 'hasta' no puede ser anterior a 'desde'");
    }
}
