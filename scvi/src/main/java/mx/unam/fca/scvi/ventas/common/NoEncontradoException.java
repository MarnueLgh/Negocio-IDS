package mx.unam.fca.scvi.ventas.common;

/** El recurso solicitado no existe (HTTP 404). */
public class NoEncontradoException extends RuntimeException {
    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
