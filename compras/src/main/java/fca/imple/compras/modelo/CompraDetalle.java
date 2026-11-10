package fca.imple.compras.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * - Representa el detalle de una compra en el subsistema de compras.
 * - Responsabilidad principal: Almacena los datos completos de una compra, incluyendo la cantidad de artículos que se solicitaron y su costo unitario.
 * - Subsistema: Compras
 * - Relación con otros componentes: Se relaciona con la entidad Compra mediante el identificador de compra.
 * - Reglas de negocio: El importe de cada línea de detalle debe calcularse considerando la cantidad recibida, el costo unitario y los descuentos aplicados.
 * - @author: Islas López Isis Jezabel, Becerra Cruz Lizeth Adriana Equipo 1
 * - Fecha de Creación: 22-10-2026
 * - Fecha de Modificación: 07-11-2026
 *  */


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class CompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCompraDetalle;

    private int idCompra;

    private int idArticulo;

    private int cantidadRecibida;

    private double costoUnitario;

    private double descuento;

    private double importe;

    /**
     - Descripción: Calcula el importe total de la partida multiplicando la cantidad recibida por el costo unitario y restando el descuento correspondiente.
     - @throws ArithmeticException Excepción lanzada en caso de error durante la operación aritmética con montos.
     - Información de Modificación: Creación de la lógica inicial para el cálculo automatizado del importe de la partida.
     - Información de Tablas, Entidades o Componentes: Tabla compradetalle, entidad CompraDetalle.
     - Reglas de Negocio: Importe = (cantidadRecibida * costoUnitario) - descuento.
     - @author: Islas López Isis Jezabel, Becerra Cruz Lizeth Adriana Equipo 1
     - Fecha de creación: 22-10-2026
     - Fecha de modificación: 07-11-2026
     */

    public double calcularImporte(){

        return 0;

    }
}
