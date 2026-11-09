package mx.unam.fca.scvi.inventario.servicio;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indica que se intentó registrar un artículo con un código de barras que ya
 * existe en la tabla Articulo.
 * Responsabilidad principal: aplicar la regla de unicidad del código de
 * barras; Spring la convierte en una respuesta HTTP 409 (Conflict).
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@StandardException
@ResponseStatus(HttpStatus.CONFLICT)
public class CodigoBarrasDuplicadoExcepcion extends RuntimeException {
}
