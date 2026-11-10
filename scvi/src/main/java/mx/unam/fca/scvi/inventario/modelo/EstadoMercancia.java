package mx.unam.fca.scvi.inventario.modelo;

/**
 * Condición física con la que llega la mercancía a la bodega.
 * Responsabilidad principal: definir los valores permitidos para la columna
 * estado de la tabla Recepcion.
 * Regla de negocio: solo la mercancía en estado BUENO suma a las existencias
 * del artículo; la DANADO queda registrada pero no suma.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
public enum EstadoMercancia {
    BUENO,
    DANADO
}
