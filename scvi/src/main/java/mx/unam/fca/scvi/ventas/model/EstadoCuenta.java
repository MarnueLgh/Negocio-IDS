package mx.unam.fca.scvi.ventas.model;

/**
 * Estados de una cuenta por cobrar según su saldo.
 *
 * Subsistema: Ventas.
 *
 * @author Álvarez Aguilar Saúl Antonio Equipo 2
 * @author Pérez Flores Roberto Carlos Equipo 2
 * @author Pérez Muñoz Diego Equipo 2
 *
 * Fecha creación: 15-11-2026
 *
 * Fecha modificación: 23-11-2026
 */
public enum EstadoCuenta {
    PENDIENTE,
    PARCIAL,
    LIQUIDADA,
    CANCELADA
}
