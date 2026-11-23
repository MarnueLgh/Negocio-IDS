package mx.edu.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Factura de una venta directa (ventaId) o de un pedido (pedidoId). No se modifica: se cancela y se crea otra
 * (sustituyeFacturaId apunta a la factura cancelada que reemplaza).
 */
@Entity //Jakarta
//Lombok
@Data //Getters and setters
@NoArgsConstructor //Constructor vacío
@AllArgsConstructor //Constructor con todos
@ToString //To String
public class Factura {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long facturaId;
    LocalDate fechaFactura;
    int diasCredito;
    LocalDate fechaLimitePago;
    BigDecimal subtotal;
    BigDecimal iva;
    BigDecimal total;
    @Enumerated(EnumType.STRING)//Jakarta
    EstadoFactura estado;
    Long ventaId;
    Long pedidoId;
    Long clienteId;
    Long vendedorId;
    Long sustituyeFacturaId;
}
