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
 * Representa el detalle (partida) de un artículo dentro de una orden de compra.
 * Responsabilidad principal: Desglosar qué artículo específico se está pidiendo, 
 * en qué cantidad, y ser responsable de calcular su propio importe estimado.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Pertenece estrictamente a una única OrdenCompra (relación de composición).
 * - Se vincula mediante idArticulo con el catálogo de Artículos del subsistema de Inventario.
 * Reglas de negocio:
 * - La cantidad solicitada y el costo unitario deben ser mayores a cero para calcular el importe.
 * - El método calcularImporte() es responsabilidad exclusiva de esta clase (cantidadSolicitada * costoUnitario).
 *
 * @author Islas López Isis Jezabel, Óscar González Saucedo Equipo 1
 * Fecha creación: 24-10-2026
 * Fecha modificación: 07-11-2026
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrdenCompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrdenDetalle;

    private int idOrdenCompra;

    private int idArticulo;

    private int cantidadSolicitada;

    private double costoUnitario;

    private double importe;

    /**
     * Descripción: Realiza el cálculo del importe económico total de la partida multiplicando la cantidad solicitada del artículo por su costo unitario pactado, actualizando el atributo importe.
     * Información de Modificación: Modifica el atributo interno 'importe' con el resultado de la operación aritmética.
     * Información de Tablas, Entidades o Componentes: Corresponde a la tabla OrdenCompraDetalle y aporta al cálculo total de OrdenCompra.
     * Reglas de Negocio: La cantidad solicitada debe ser estrictamente mayor a cero y el costo unitario no puede ser negativo.
     *
     * @return valor numérico decimal que representa el importe total de la partida calculada.
     * @throws IllegalArgumentException si la cantidad solicitada es menor o igual a cero o el costo unitario es negativo.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 07-11-2026
     */
    public double calcularImporte() {
        if (this.cantidadSolicitada <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a cero.");
        }
        if (this.costoUnitario < 0) {
            throw new IllegalArgumentException("El costo unitario del artículo no puede ser un valor negativo.");
        }
        this.importe = this.cantidadSolicitada * this.costoUnitario;
        return this.importe;
    }
}