package mx.unam.fca.scvi.inventario.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;

/**
 * Representa la entidad Almacen dentro del sistema de inventario.
 * Responsabilidad principal: Mapear la tabla Almacen en la base de datos
 * y almacenar los datos de los almacenes de distribución.
 * No comparte tabla con Bodega, manteniendo entidades separadas.
 *
 * @author Equipo 3
 *
 * Fecha creación: 07-11-2026
 * Fecha modificación: 07-11-2026
 */
@Entity
@Table(name = "Almacen", uniqueConstraints = {
        @UniqueConstraint(name = "ukAlmacenNombre", columnNames = {"nombre"})
})
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAlmacen")
    private Long almacenId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    /**
     * Obtiene el identificador único del almacén.
     *
     * @return identificador del almacén.
     */
    public Long getAlmacenId() {
        return almacenId;
    }

    /**
     * Establece el identificador único del almacén.
     *
     * @param almacenId identificador a asignar.
     */
    public void setAlmacenId(Long almacenId) {
        this.almacenId = almacenId;
    }

    /**
     * Obtiene el nombre del almacén.
     *
     * @return nombre del almacén.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del almacén.
     *
     * @param nombre nombre descriptivo a asignar.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la dirección física del almacén.
     *
     * @return dirección del almacén.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección física del almacén.
     *
     * @param direccion dirección a asignar.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene el estado de actividad del almacén.
     *
     * @return true si está activo, false en caso contrario.
     */
    public Boolean getActivo() {
        return activo;
    }

    /**
     * Establece el estado de actividad del almacén.
     *
     * @param activo estado de actividad a asignar.
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}