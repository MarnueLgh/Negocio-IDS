package mx.unam.fca.scvi.inventario.servicio;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indica que no existe un artículo con el identificador solicitado.
 * Responsabilidad principal: señalar un "ID artículo" inexistente; Spring la
 * convierte en una respuesta HTTP 404 (Not Found).
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArticuloNoEncontradoExcepcion extends RuntimeException {
}
