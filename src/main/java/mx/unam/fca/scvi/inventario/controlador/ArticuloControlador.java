package mx.unam.fca.scvi.inventario.controlador;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.unam.fca.scvi.inventario.modelo.Articulo;
import mx.unam.fca.scvi.inventario.modelo.ArticuloSolicitudDto;
import mx.unam.fca.scvi.inventario.servicio.ArticuloServicio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST del catálogo de artículos (/api/inventario/articulos).
 * Responsabilidad principal: recibir las peticiones HTTP, validar la entrada
 * con {@code @Valid} y delegar en ArticuloServicio.
 * Respuestas de error: 400 si la entrada es inválida, 404 si el artículo no
 * existe y 409 si el código de barras ya está registrado.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@RestController
@RequestMapping("/api/inventario/articulos")
@RequiredArgsConstructor
public class ArticuloControlador {

    private final ArticuloServicio articuloServicio;

    /**
     * Registra un artículo nuevo.
     *
     * @param solicitud datos del artículo en el cuerpo de la petición.
     * @return artículo guardado, con HTTP 201.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Articulo registrarArticulo(@Valid @RequestBody ArticuloSolicitudDto solicitud) {
        return articuloServicio.registrarArticulo(solicitud);
    }

    /**
     * Lista los artículos registrados.
     *
     * @return lista de artículos, con HTTP 200.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @GetMapping
    public List<Articulo> listarArticulos() {
        return articuloServicio.listarArticulos();
    }

    /**
     * Obtiene un artículo por su identificador.
     *
     * @param articuloId identificador del artículo.
     * @return artículo encontrado, con HTTP 200 (404 si no existe).
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @GetMapping("/{articuloId}")
    public Articulo obtenerArticulo(@PathVariable Long articuloId) {
        return articuloServicio.obtenerArticulo(articuloId);
    }
}
