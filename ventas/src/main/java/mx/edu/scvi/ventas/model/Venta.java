package mx.edu.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Venta directa: se paga y se entrega en el momento. */
@Entity //Jakarta
//Lombok
@Data //Getters and setters
@NoArgsConstructor //Constructor vacío
@AllArgsConstructor //Constructor con todos
@ToString //To String
public class Venta {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long ventaId;
    LocalDateTime fechaVenta;
    Long cajeroId;
    Long corteCajaId;
    Long clienteId;
    boolean solicitaFactura;
    BigDecimal subtotal;
    BigDecimal iva;
    BigDecimal total;
    @Enumerated(EnumType.STRING)//Jakarta
    EstadoVenta estado;
}
