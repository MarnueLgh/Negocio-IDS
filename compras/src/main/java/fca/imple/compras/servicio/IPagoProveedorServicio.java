package fca.imple.compras.servicio;

import fca.imple.compras.modelo.PagoDetalle;
import fca.imple.compras.modelo.PagoProveedor;

import java.time.LocalDate;
import java.util.List;
/**
 * Interfaz que define las operaciones del servicio del pago a proveedores.
 * Propósito: Establecer el contrato formal de los métodos para el registro, control y consulta de
 *  las transacciones de pago efectuadas a los proveedores.
 * Responsabilidad principal: Gestionar la persistencia y validación de las órdenes de pago y sus
 *  respectivos desgloses dentro del subsistema de compras.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Es implementada por la clase de servicio correspondiente en la capa de negocio.
 * - Es consumida por la capa de interfaz de usuario en ComprasApplication.
 * - Interactúa con los modelos PagoProveedor y PagoDetalle.
 * Reglas de negocio:
 * - Todo pago registrado debe estar asociado a un proveedor válido y activo en el sistema.
 * - Los montos y los detalles de los pagos deben coincidir con las obligaciones fiscales y
 *   saldos pendientes correspondientes.
 *
 * @author Islas López Isis Jezabel Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 08-11-2026
 */

public interface IPagoProveedorServicio {

    public boolean registrarPago(PagoProveedor pago, List<PagoDetalle> detalles);
    public List<PagoDetalle> consultarDetallePago(int idPago);
    public List<PagoProveedor> consultarPagosPorProveedorYPeriodo(int idProveedor, LocalDate inicio, LocalDate fin);

}