package mx.unam.fca.scvi.inventario.servicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.unam.fca.scvi.inventario.modelo.Articulo;
import mx.unam.fca.scvi.inventario.modelo.ArticuloSolicitudDto;
import mx.unam.fca.scvi.inventario.modelo.TipoArticulo;
import mx.unam.fca.scvi.inventario.repositorio.IArticuloRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio del catálogo de artículos del subsistema de Inventario.
 * Responsabilidad principal: registrar y consultar artículos en la tabla
 * Articulo mediante IArticuloRepositorio, que se inyecta por constructor.
 * Reglas de negocio: el código de barras no se repite y solo los artículos
 * perecederos conservan fecha de caducidad.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ArticuloServicio {

    private final IArticuloRepositorio articuloRepositorio;

    /**
     * Registra un artículo nuevo en la tabla Articulo a partir de datos ya
     * validados por ArticuloSolicitudDto. Si el artículo no es perecedero se
     * descarta la fecha de caducidad recibida.
     *
     * @param solicitud datos del artículo que se desea registrar.
     * @return artículo guardado, con el articuloId generado por la base de
     *         datos.
     * @throws CodigoBarrasDuplicadoExcepcion si ya existe un artículo con el
     *                                        mismo código de barras.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @Transactional
    public Articulo registrarArticulo(ArticuloSolicitudDto solicitud) {
        if (articuloRepositorio.existsByCodigoBarras(solicitud.getCodigoBarras())) {
            throw new CodigoBarrasDuplicadoExcepcion(
                    "Ya existe un artículo con el código de barras " + solicitud.getCodigoBarras());
        }

        Articulo articulo = Articulo.builder()
                .nombre(solicitud.getNombre())
                .descripcion(solicitud.getDescripcion())
                .codigoBarras(solicitud.getCodigoBarras())
                .marca(solicitud.getMarca())
                .proveedor(solicitud.getProveedor())
                .unidadMedida(solicitud.getUnidadMedida())
                .tamanio(solicitud.getTamanio())
                .existencias(solicitud.getExistencias())
                .existenciasMinimas(solicitud.getExistenciasMinimas())
                .existenciasMaximas(solicitud.getExistenciasMaximas())
                .precio(solicitud.getPrecio())
                .costo(solicitud.getCosto())
                .tipo(solicitud.getTipo())
                .fechaCaducidad(solicitud.getTipo() == TipoArticulo.PERECEDERO
                        ? solicitud.getFechaCaducidad() : null)
                .build();

        Articulo articuloGuardado = articuloRepositorio.save(articulo);
        log.info("Artículo registrado con id {} y código de barras {}",
                articuloGuardado.getArticuloId(), articuloGuardado.getCodigoBarras());
        return articuloGuardado;
    }

    /**
     * Lista todos los artículos registrados en la tabla Articulo.
     *
     * @return lista de artículos; vacía si no hay ninguno.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @Transactional(readOnly = true)
    public List<Articulo> listarArticulos() {
        return articuloRepositorio.findAll();
    }

    /**
     * Obtiene un artículo por su identificador. Las historias que reciben un
     * "ID artículo" lo usan para validar que exista.
     *
     * @param articuloId identificador del artículo.
     * @return artículo encontrado.
     * @throws ArticuloNoEncontradoExcepcion si no existe un artículo con ese
     *                                       identificador.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @Transactional(readOnly = true)
    public Articulo obtenerArticulo(Long articuloId) {
        return articuloRepositorio.findById(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoExcepcion(
                        "No existe un artículo con id " + articuloId));
    }
}
