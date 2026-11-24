package mx.unam.fca.scvi.ventas.controller;

import java.math.BigDecimal;
import mx.unam.fca.scvi.ventas.model.Empleado;
import mx.unam.fca.scvi.ventas.model.TipoEmpleado;

/**
 * Cuerpo JSON para registrar/modificar vendedores y cajeros.
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
public record EmpleadoRequest(String nombre, String telefono, String domicilio, BigDecimal porcentajeComision,
                              Long zonaId, Boolean activo) {

    public Empleado aEmpleado(TipoEmpleado tipo) {
        return new Empleado(null, nombre, telefono, domicilio, tipo, porcentajeComision, true, zonaId);
    }
}
