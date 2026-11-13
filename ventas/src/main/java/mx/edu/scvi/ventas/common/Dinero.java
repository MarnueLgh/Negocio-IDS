package mx.edu.scvi.ventas.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Cálculos monetarios (BigDecimal, 2 decimales, IVA 16%). */
public final class Dinero {

    public static final BigDecimal IVA = new BigDecimal("0.16");

    private Dinero() {
    }

    public static BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal importe(BigDecimal precio, int cantidad) {
        return redondear(precio.multiply(BigDecimal.valueOf(cantidad)));
    }

    public static BigDecimal iva(BigDecimal subtotal) {
        return redondear(subtotal.multiply(IVA));
    }

    /** base * porcentaje / 100, p. ej. porcentaje(1000, 3) = 30.00 */
    public static BigDecimal porcentaje(BigDecimal base, BigDecimal porcentaje) {
        return redondear(base.multiply(porcentaje).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
    }
}
