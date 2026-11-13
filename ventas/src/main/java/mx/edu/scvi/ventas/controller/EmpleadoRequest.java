package mx.edu.scvi.ventas.controller;

import java.math.BigDecimal;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.TipoEmpleado;

/** Cuerpo JSON para registrar/modificar vendedores y cajeros. */
public record EmpleadoRequest(String nombre, String telefono, String domicilio, BigDecimal porcentajeComision,
                              Long zonaId, Boolean activo) {

    public Empleado aEmpleado(TipoEmpleado tipo) {
        return new Empleado(null, nombre, telefono, domicilio, tipo, porcentajeComision, true, zonaId);
    }
}
