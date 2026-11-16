package fca.imple.compras.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

/**
 * Representa a un proveedor de mercancía en el subsistema de compras.
 * Responsabilidad principal: Funcionar como un catálogo para almacenar 
 * la información de contacto, datos fiscales (RFC) y datos bancarios de los proveedors
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Se le asocia una CondicionPago que dicta sus reglas de crédito.
 * - Puede tener registradas múltiples órdenes de compra, compras, cuentas por pagar y pagos.
 * Reglas de negocio:
 * - El RFC debe contener la longitud válida (13 caracteres).
 * - Al ser un catálogo, su función se centra en mantener la integridad de la información sin lógica de cálculos.
 * @author Islas López Isis Jezabel, Oscar Gonzalez Saucedo, Equipo 1
 * Fecha de Creación: 22-10-2026
 * Fecha modificación: 05-11-2026
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProveedor;

    private String nombre;

    private String telefono;

    private String domicilio;

    private String rfc;

    private String contacto;

    private String banco;

    private String numeroCuenta;

}
