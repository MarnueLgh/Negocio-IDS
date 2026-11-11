package mx.unam.fca.scvi.inventario.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Registro de una llegada de mercancía a una bodega.
 * Responsabilidad principal: mapear la tabla Recepcion
 * (script scvi/sql/03_crear_recepcion.sql). Se relaciona muchos a uno con
 * Articulo y con Bodega; no tiene relación con Almacen, así que la mercancía
 * nunca entra directo a un almacén.
 * Reglas de negocio: lote y fecha de caducidad solo se guardan para artículos
 * perecederos (los valida RecepcionServicio). No usa @Data ni @ToString porque
 * tiene relaciones, como recomienda el tutorial de Lombok.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@Entity
@Table(name = "Recepcion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recepcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recepcionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "articuloId", nullable = false,
            foreignKey = @ForeignKey(name = "fkRecepcionArticulo"))
    private Articulo articulo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bodegaId", nullable = false,
            foreignKey = @ForeignKey(name = "fkRecepcionBodega"))
    private Bodega bodega;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDate fechaRecepcion;

    /** Texto libre hasta que exista la tabla Anaquel. */
    @Column(nullable = false, length = 50)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoMercancia estado;

    @Column(length = 30)
    private String lote;

    private LocalDate fechaCaducidad;
}
