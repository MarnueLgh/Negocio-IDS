package fca.imple.compras.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Date;

/**
 - Representa a una compra en el subsistema de compras.
 - Responsabilidad principal: Almacena y administra la información general de las compras realizadas a los proveedores, incluyendo fechas clave, importes totales, número de factura y su estado de ejecución.
 - Subsistema: Compras
 - Relación con otros componentes: Se relaciona con las entidades OrdenCompra, Proveedor, GerenteCompras y CompraDetalle.
 - Reglas de negocio: Toda compra debe estar asociada a una orden de compra previa, un proveedor autorizado y un gerente de compras responsable; el total debe reflejar la suma de los importes de sus partidas.
 - @author: Islas López Isis Jezabel, Becerra Cruz Lizeth Adriana Equipo 1
 - Fecha de Creación: 22-10-2026
 - Fecha de Modificación: 07-11-2026
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCompra;

    private String numeroFactura;

    private Date fechaOrden;

    private Date fechaRecepcion;

    private Date fechaCompra;

    private String estado;

    private double total;

    private int idOrdenCompra;

    private int idProveedor;

    private int idGerenteCompras;

}
