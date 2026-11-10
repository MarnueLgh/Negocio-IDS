package mx.unam.fca.scvi.inventario.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa un artículo del catálogo de inventario.
 * Responsabilidad principal: mapear la tabla Articulo
 * (script scvi/sql/01_crear_articulo.sql). Las demás historias que usan
 * "ID artículo" se relacionan con esta entidad mediante articuloId.
 * Reglas de negocio: el código de barras es único; las validaciones de
 * entrada se aplican en ArticuloSolicitudDto; solo un artículo activo puede
 * recibir mercancía (INV-07).
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@Entity
@Table(name = "Articulo", uniqueConstraints = {
        @UniqueConstraint(name = "uqArticuloCodigoBarras", columnNames = {"codigoBarras"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articuloId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String codigoBarras;

    @Column(nullable = false, length = 60)
    private String marca;

    /** Texto libre hasta que Compras defina la tabla Proveedor. */
    @Column(nullable = false, length = 100)
    private String proveedor;

    @Column(nullable = false, length = 20)
    private String unidadMedida;

    /** Tamaño o presentación; se escribe sin ñ por la regla 3.4. */
    @Column(length = 30)
    private String tamanio;

    @Column(nullable = false)
    private Integer existencias;

    @Column(nullable = false)
    private Integer existenciasMinimas;

    @Column(nullable = false)
    private Integer existenciasMaximas;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoArticulo tipo;

    private LocalDate fechaCaducidad;

    /** Un artículo nuevo nace activo; solo los activos reciben mercancía (INV-07). */
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
