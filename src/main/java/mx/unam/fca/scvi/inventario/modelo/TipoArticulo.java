package mx.unam.fca.scvi.inventario.modelo;

/**
 * Clasifica un artículo según si caduca o no.
 * Responsabilidad principal: definir los valores permitidos para la columna
 * tipo de la tabla Articulo.
 * Regla de negocio: un artículo PERECEDERO exige fecha de caducidad.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
public enum TipoArticulo {
    PERECEDERO,
    NO_PERECEDERO
}
