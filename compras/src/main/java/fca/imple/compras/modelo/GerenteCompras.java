package fca.imple.compras.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

/**
  * Representa a un gerente de compras en el subsistema de compras.
  * Responsabilidad principal: Administrar y mantener la información del personal encargado
  * de autorizar y gestionar los procesos de compra dentro del subsistema.
  * Relacióm con otros componentes:
  * - Está asociado a las órdenes de compra y compras que gestiona o autoriza.
  * - Se conecta con su respectivo objeto de acceso a datos (DAO) para las operaciones de consulta y registro
  * Reglas de negocio:
  * - Todo gerente de compras debe contar con datos de contacto válidos (nombre, teléfono y domicilio).
  * - El estado activo del gerente determina si tiene permisos vigentes para realizar operaciones en el sistema.
  * @author Islas López Isis Jezabel Equipo 1
  * Fecha de Creación: 22-20-2026
  * Fecha de Modificación: 02-11-2026
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class GerenteCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGerenteCompras;

    private String nombre;

    private String telefono;

    private String domicilio;

    private boolean activo;

}
