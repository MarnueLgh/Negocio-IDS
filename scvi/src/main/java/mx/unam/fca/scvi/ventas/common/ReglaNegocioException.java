package mx.unam.fca.scvi.ventas.common;

/** Se violó una regla de negocio (HTTP 422). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
