package fca.imple.compras.servicio;

import fca.imple.compras.modelo.GerenteCompras;

/**
 * Interfaz que define las operaciones del servicio para la gestión de gerentes de compras.
 * Propósito: Establecer el contrato formal de los métodos para el registro, actualización, eliminación y
 *  consulta de los gerentes de compras en el sistema.
 * Responsabilidad principal:Delimitar el ciclo de vida y las operaciones disponibles para la administración
 *  de los gerentes autorizados dentro del subsistema.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Es implementada por la clase de servicio correspondiente en la capa de negocio.
 * - Es consumida por la consola ComprasApplication y otros módulos de gestión de compras.
 * Reglas de negocio:
 * - Todo gerente manipulado debe apegarse a las restricciones de unicidad y validez de sus datos de
 *   identificación.
 *
 * @author Islas López Isis Jezabel Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 08-11-2026
 */

public interface IGerenteComprasServicio {

    public boolean insertarGerente(GerenteCompras gerenteCompras);
    public boolean borrarGerente(int idGerenteCompras);
    public boolean modificarGerente(GerenteCompras gerenteCompras);
    public int buscarGerente(int idGerenteCompras);

}
