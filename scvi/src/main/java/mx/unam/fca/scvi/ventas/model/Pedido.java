package mx.unam.fca.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Venta no directa: empieza con un pedido; la entrega se define al hacerlo.
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
public class Pedido {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long pedidoId;
    LocalDate fechaPedido;
    LocalDate fechaEntrega;
    Long clienteId;
    Long vendedorId;
    int diasCredito;
    BigDecimal subtotal;
    BigDecimal iva;
    BigDecimal total;
    @Enumerated(EnumType.STRING)//Jakarta
    EstadoPedido estado;
}
