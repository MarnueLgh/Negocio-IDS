package mx.unam.fca.scvi.inventario.servicio;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando no existe la bodega destino indicada en una recepción.
 * Responsabilidad principal: que la API responda HTTP 404 mediante
 * {@code @ResponseStatus}. Los constructores los genera Lombok
 * (@StandardException).
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BodegaNoEncontradaExcepcion extends RuntimeException {
}
