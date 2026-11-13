package mx.edu.scvi.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Inicio y corte de caja: importeEsperado = importeInicial + ventas cobradas en efectivo. */
@Entity //Jakarta
//Lombok
@Data //Getters and setters
@NoArgsConstructor //Constructor vacío
@AllArgsConstructor //Constructor con todos
@ToString //To String
public class CorteCaja {

    @Id//Jakarta
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Jakarta
    Long corteCajaId;
    int cajaId;
    Long cajeroId;
    LocalDateTime fechaApertura;
    LocalDateTime horaCorte;
    BigDecimal importeInicial;
    int totalVentas;
    BigDecimal importeEsperado;
    BigDecimal importeFisico;
    BigDecimal diferencia;
    boolean hayDiferencia;
    @Enumerated(EnumType.STRING)//Jakarta
    EstadoCorte estado;
}
