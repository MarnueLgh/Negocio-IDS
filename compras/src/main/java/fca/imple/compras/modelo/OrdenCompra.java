package fca.imple.compras.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Representa una orden de compra generada a un proveedor en el subsistema de compras.
 * Responsabilidad principal: Registrar la solicitud de mercancía a un
 * proveedor, manteniendo el control de los costos estimados antes de la recepción.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Es generada por un GerenteCompras.
 * - Se emite dirigida a un Proveedor.
 * - Contiene una lista de OrdenCompraDetalle (composición) con los artículos pedidos.
 * - Da origen posteriormente a una Compra real.
 * Reglas de negocio:
 * - Toda orden debe tener una fecha de creación y un estado actual.
 * - El total estimado se compone de la suma de sus detalles menos los descuentos aplicables.
 * - Una orden de compra no genera deuda hasta convertirse en Compra.
 *
 * @author Islas Lopez Isis Jezabel, Óscar González Saucedo Equipo 1
 * Fecha creación: 24-10-2026
 * Fecha modificación: 07-11-2026
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrdenCompra;

    private int idProveedor;

    private int idGerenteCompras;

    private LocalDate fechaCreacion;

    private double costo;

    private double descuento;

    private double total;

    private String estado;

}