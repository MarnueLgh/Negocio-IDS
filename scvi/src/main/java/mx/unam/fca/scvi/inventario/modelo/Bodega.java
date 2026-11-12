package mx.unam.fca.scvi.inventario.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una bodega donde se recibe la mercancía.
 * Responsabilidad principal: mapear la tabla Bodega
 * (script scvi/sql/02_crear_bodega.sql) con la estructura mínima que necesita
 * INV-07. Es una entidad distinta de Almacen: la mercancía siempre llega a una
 * bodega y nunca directo a un almacén.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@Entity
@Table(name = "Bodega", uniqueConstraints = {
        @UniqueConstraint(name = "uqBodegaNombre", columnNames = {"nombre"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long bodegaId;

    @Column(nullable = false, length = 100)
    private String nombre;
}
