package mx.unam.fca.scvi.inventario.servicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.unam.fca.scvi.inventario.modelo.Articulo;
import mx.unam.fca.scvi.inventario.modelo.Bodega;
import mx.unam.fca.scvi.inventario.modelo.EstadoMercancia;
import mx.unam.fca.scvi.inventario.modelo.Recepcion;
import mx.unam.fca.scvi.inventario.modelo.RecepcionRespuestaDto;
import mx.unam.fca.scvi.inventario.modelo.RecepcionSolicitudDto;
import mx.unam.fca.scvi.inventario.modelo.TipoArticulo;
import mx.unam.fca.scvi.inventario.repositorio.IArticuloRepositorio;
import mx.unam.fca.scvi.inventario.repositorio.IBodegaRepositorio;
import mx.unam.fca.scvi.inventario.repositorio.IRecepcionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de la llegada de mercancía (INV-07) del subsistema de
 * Inventario.
 * Responsabilidad principal: registrar recepciones en la tabla Recepcion y
 * actualizar las existencias del artículo en la tabla Articulo dentro de una
 * misma transacción. Usa ArticuloServicio para validar el artículo y los
 * repositorios de Bodega, Recepcion y Articulo.
 * Reglas de negocio: el artículo debe existir y estar activo; la bodega
 * destino debe existir; un artículo perecedero exige lote y una fecha de
 * caducidad posterior a la fecha de recepción; solo la mercancía en estado
 * BUENO suma a las existencias. La mercancía entra siempre a una bodega
 * porque Recepcion solo se relaciona con Bodega, nunca con Almacen.
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
public class RecepcionServicio {

    private final IRecepcionRepositorio recepcionRepositorio;
    private final IBodegaRepositorio bodegaRepositorio;
    private final IArticuloRepositorio articuloRepositorio;
    private final ArticuloServicio articuloServicio;

    /**
     * Registra la llegada de mercancía a una bodega. Guarda la recepción en la
     * tabla Recepcion y, si el estado es BUENO, suma la cantidad a las
     * existencias del artículo en la tabla Articulo. Ambas operaciones forman
     * una sola transacción: si algo falla no se guarda ninguna.
     * Para artículos no perecederos se descartan el lote y la fecha de
     * caducidad recibidos. Si las existencias resultantes superan las
     * existencias máximas, la recepción se registra igual (la mercancía ya
     * está en bodega), se deja una advertencia en la bitácora y la respuesta
     * lo indica en excedeMaximo.
     *
     * @param solicitud datos de la recepción, ya validados por
     *                  RecepcionSolicitudDto.
     * @return recepción guardada, con el recepcionId generado.
     * @throws ArticuloNoEncontradoExcepcion si el artículo no existe.
     * @throws BodegaNoEncontradaExcepcion   si la bodega destino no existe.
     * @throws RecepcionInvalidaExcepcion    si el artículo está inactivo o si,
     *                                       siendo perecedero, falta el lote o
     *                                       la caducidad no es posterior a la
     *                                       fecha de recepción.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @Transactional
    public RecepcionRespuestaDto registrarRecepcion(RecepcionSolicitudDto solicitud) {
        Articulo articulo = articuloServicio.obtenerArticulo(solicitud.getArticuloId());
        if (!articulo.getActivo()) {
            throw new RecepcionInvalidaExcepcion(
                    "El artículo " + articulo.getArticuloId() + " está inactivo y no puede recibir mercancía");
        }

        Bodega bodega = bodegaRepositorio.findById(solicitud.getBodegaId())
                .orElseThrow(() -> new BodegaNoEncontradaExcepcion(
                        "No existe una bodega con id " + solicitud.getBodegaId()));

        boolean perecedero = articulo.getTipo() == TipoArticulo.PERECEDERO;
        if (perecedero) {
            validarLoteYCaducidad(solicitud);
        }

        Recepcion recepcion = Recepcion.builder()
                .articulo(articulo)
                .bodega(bodega)
                .cantidad(solicitud.getCantidad())
                .fechaRecepcion(solicitud.getFechaRecepcion())
                .ubicacion(solicitud.getUbicacion())
                .estado(solicitud.getEstado())
                .lote(perecedero ? solicitud.getLote() : null)
                .fechaCaducidad(perecedero ? solicitud.getFechaCaducidad() : null)
                .build();
        Recepcion recepcionGuardada = recepcionRepositorio.save(recepcion);

        boolean excedeMaximo = false;
        if (solicitud.getEstado() == EstadoMercancia.BUENO) {
            articuloRepositorio.sumarExistencias(articulo.getArticuloId(), solicitud.getCantidad());
            excedeMaximo = articulo.getExistencias() + solicitud.getCantidad() > articulo.getExistenciasMaximas();
            if (excedeMaximo) {
                log.warn("La recepción {} deja al artículo {} por encima de sus existencias máximas ({})",
                        recepcionGuardada.getRecepcionId(), articulo.getArticuloId(),
                        articulo.getExistenciasMaximas());
            }
        }

        log.info("Recepción {} registrada: artículo {}, bodega {}, cantidad {}, estado {}",
                recepcionGuardada.getRecepcionId(), articulo.getArticuloId(), bodega.getBodegaId(),
                solicitud.getCantidad(), solicitud.getEstado());
        return convertirARespuesta(recepcionGuardada, excedeMaximo);
    }

    /**
     * Lista todas las recepciones de la tabla Recepcion con los datos básicos
     * de su artículo y su bodega.
     *
     * @return lista de recepciones; vacía si no hay ninguna. En la lista
     *         excedeMaximo siempre es false porque solo aplica al registrar.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @Transactional(readOnly = true)
    public List<RecepcionRespuestaDto> listarRecepciones() {
        return recepcionRepositorio.findAll().stream()
                .map(recepcion -> convertirARespuesta(recepcion, false))
                .toList();
    }

    /**
     * Aplica la regla de artículos perecederos: el lote es obligatorio y la
     * fecha de caducidad debe existir y ser posterior a la fecha de recepción
     * (no se recibe mercancía caducada).
     *
     * @param solicitud datos de la recepción de un artículo perecedero.
     * @throws RecepcionInvalidaExcepcion si falta el lote o la caducidad, o si
     *                                    la caducidad no es posterior a la
     *                                    fecha de recepción.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    private void validarLoteYCaducidad(RecepcionSolicitudDto solicitud) {
        if (solicitud.getLote() == null || solicitud.getLote().isBlank()) {
            throw new RecepcionInvalidaExcepcion("El lote es obligatorio para artículos perecederos");
        }
        if (solicitud.getFechaCaducidad() == null) {
            throw new RecepcionInvalidaExcepcion("La fecha de caducidad es obligatoria para artículos perecederos");
        }
        if (!solicitud.getFechaCaducidad().isAfter(solicitud.getFechaRecepcion())) {
            throw new RecepcionInvalidaExcepcion(
                    "La fecha de caducidad debe ser posterior a la fecha de recepción");
        }
    }

    /**
     * Convierte una entidad Recepcion en el DTO que devuelve la API. Debe
     * llamarse dentro de una transacción porque lee las relaciones LAZY con
     * Articulo y Bodega.
     *
     * @param recepcion    recepción que se desea convertir.
     * @param excedeMaximo si la recepción dejó las existencias por encima del
     *                     máximo del artículo.
     * @return DTO con los datos de la recepción, su artículo y su bodega.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    private RecepcionRespuestaDto convertirARespuesta(Recepcion recepcion, boolean excedeMaximo) {
        return RecepcionRespuestaDto.builder()
                .recepcionId(recepcion.getRecepcionId())
                .articuloId(recepcion.getArticulo().getArticuloId())
                .nombreArticulo(recepcion.getArticulo().getNombre())
                .bodegaId(recepcion.getBodega().getBodegaId())
                .nombreBodega(recepcion.getBodega().getNombre())
                .cantidad(recepcion.getCantidad())
                .fechaRecepcion(recepcion.getFechaRecepcion())
                .ubicacion(recepcion.getUbicacion())
                .estado(recepcion.getEstado())
                .lote(recepcion.getLote())
                .fechaCaducidad(recepcion.getFechaCaducidad())
                .excedeMaximo(excedeMaximo)
                .build();
    }
}
