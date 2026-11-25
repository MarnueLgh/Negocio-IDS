package mx.unam.fca.scvi.inventario.servicio;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando una recepción incumple una regla de negocio que depende
 * de la base de datos (artículo inactivo, lote o caducidad faltante o
 * inválida).
 * Responsabilidad principal: que la API responda HTTP 400 mediante
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
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecepcionInvalidaExcepcion extends RuntimeException {
}
