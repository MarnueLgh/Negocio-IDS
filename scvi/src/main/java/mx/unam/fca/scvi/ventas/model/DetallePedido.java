package mx.unam.fca.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

/**
 * Renglón de un pedido. cantidadPendiente es lo que aún no se entrega; promesa = se prometió con anticipación.
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
public class DetallePedido {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long detallePedidoId;
    Long pedidoId;
    Long articuloId;
    String codigo;
    String descripcion;
    int cantidadPedida;
    int cantidadPendiente;
    BigDecimal precio;
    BigDecimal importe;
    boolean promesa;

    public DetallePedido conPedido(Long id) {
        return new DetallePedido(detallePedidoId, id, articuloId, codigo, descripcion, cantidadPedida,
                cantidadPendiente, precio, importe, promesa);
    }
}
