package mx.edu.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

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
