package mx.edu.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity //Jakarta
//Lombok
@Data //Getters and setters
@NoArgsConstructor //Constructor vacío
@AllArgsConstructor //Constructor con todos
@ToString //To String
public class Pago {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long pagoId;
    Long ventaId;
    @Enumerated(EnumType.STRING)//Jakarta
    FormaPago formaPago;
    BigDecimal importe;
    BigDecimal importeRecibido;
    BigDecimal cambio;
    LocalDateTime fechaPago;

    /** Cambio a entregar: solo hay cambio si se recibe más que el importe. */
    public static BigDecimal calcularCambio(BigDecimal importe, BigDecimal importeRecibido) {
        BigDecimal cambio = importeRecibido.subtract(importe);
        return cambio.signum() < 0 ? BigDecimal.ZERO.setScale(2) : cambio;
    }
}
