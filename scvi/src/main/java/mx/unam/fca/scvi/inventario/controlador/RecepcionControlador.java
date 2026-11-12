package mx.unam.fca.scvi.inventario.controlador;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.unam.fca.scvi.inventario.modelo.RecepcionRespuestaDto;
import mx.unam.fca.scvi.inventario.modelo.RecepcionSolicitudDto;
import mx.unam.fca.scvi.inventario.servicio.RecepcionServicio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST de llegada de mercancía (/api/inventario/recepciones), INV-07.
 * Responsabilidad principal: recibir las peticiones HTTP, validar la entrada
 * con {@code @Valid} y delegar en RecepcionServicio. Devuelve DTOs, nunca la
 * entidad Recepcion.
 * Respuestas de error: 400 si la entrada es inválida o incumple una regla de
 * negocio, y 404 si el artículo o la bodega no existen.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@RestController
@RequestMapping("/api/inventario/recepciones")
@RequiredArgsConstructor
public class RecepcionControlador {

    private final RecepcionServicio recepcionServicio;

    /**
     * Registra una llegada de mercancía a una bodega y actualiza las
     * existencias del artículo.
     *
     * @param solicitud datos de la recepción en el cuerpo de la petición.
     * @return recepción guardada, con HTTP 201.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecepcionRespuestaDto registrarRecepcion(@Valid @RequestBody RecepcionSolicitudDto solicitud) {
        return recepcionServicio.registrarRecepcion(solicitud);
    }

    /**
     * Lista las recepciones registradas.
     *
     * @return lista de recepciones, con HTTP 200.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @GetMapping
    public List<RecepcionRespuestaDto> listarRecepciones() {
        return recepcionServicio.listarRecepciones();
    }
}
