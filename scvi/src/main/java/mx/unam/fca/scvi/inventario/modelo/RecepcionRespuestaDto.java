package mx.unam.fca.scvi.inventario.modelo;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Datos de una recepción que devuelve la API.
 * Responsabilidad principal: exponer la recepción sin serializar la entidad
 * Recepcion ni sus relaciones LAZY con Articulo y Bodega, como recomienda el
 * manual de JPA de la materia. Es inmutable (@Value).
 * Reglas de negocio: excedeMaximo indica que, tras sumar esta recepción, las
 * existencias del artículo superan sus existencias máximas; la recepción se
 * registra de todos modos porque la mercancía ya está físicamente en bodega.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@Value
@Builder
public class RecepcionRespuestaDto {

    Long recepcionId;
    Long articuloId;
    String nombreArticulo;
    Long bodegaId;
    String nombreBodega;
    Integer cantidad;
    LocalDate fechaRecepcion;
    String ubicacion;
    EstadoMercancia estado;
    String lote;
    LocalDate fechaCaducidad;
    boolean excedeMaximo;
}
