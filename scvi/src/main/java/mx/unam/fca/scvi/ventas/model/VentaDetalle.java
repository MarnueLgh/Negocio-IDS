package mx.unam.fca.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

/**
 * Entidad que mapea la tabla VentaDetalle: un renglón de una venta directa.
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
@Entity //Jakarta
//Lombok
@Data //Getters and setters
@NoArgsConstructor //Constructor vacío
@AllArgsConstructor //Constructor con todos
@ToString //To String
public class VentaDetalle {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long ventaDetalleId;
    Long ventaId;
    Long articuloId;
    String codigo;
    String descripcion;
    int cantidad;
    BigDecimal precio;
    BigDecimal importe;

    public VentaDetalle conVenta(Long id) {
        return new VentaDetalle(ventaDetalleId, id, articuloId, codigo, descripcion, cantidad, precio, importe);
    }
}
