package mx.edu.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity //Jakarta
//Lombok
@Data //Getters and setters
@NoArgsConstructor //Constructor vacío
@AllArgsConstructor //Constructor con todos
@ToString //To String
public class Comision {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long comisionId;
    LocalDate fechaGeneracion;
    BigDecimal importeComision;
    BigDecimal importeVenta;
    BigDecimal porcentajeAplicado;
    Long ventaId;
    Long facturaId;
    Long empleadoId;
    String estado;
}
