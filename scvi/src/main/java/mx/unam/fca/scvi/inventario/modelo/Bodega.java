package mx.unam.fca.scvi.inventario.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una Bodega en el sistema de inventario.
 * Responsabilidad principal: Mapear la tabla 'bodega' en la base de datos MySQL.
 *
 * @author Equipo 3
 */
@Entity
@Table(name = "bodega")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bodega")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;
}