package mx.unam.fca.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

/**
 * Entidad que mapea la tabla FacturaDetalle: un renglón de una factura.
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
public class FacturaDetalle {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long facturaDetalleId;
    Long facturaId;
    Long articuloId;
    String codigo;
    String descripcion;
    int cantidad;
    BigDecimal precio;
    BigDecimal importe;

    public FacturaDetalle conFactura(Long id) {
        return new FacturaDetalle(facturaDetalleId, id, articuloId, codigo, descripcion, cantidad, precio, importe);
    }
}
