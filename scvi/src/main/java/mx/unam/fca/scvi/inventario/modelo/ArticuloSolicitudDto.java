package mx.unam.fca.scvi.inventario.modelo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos que envía el cliente para registrar un artículo.
 * Responsabilidad principal: validar la entrada de la API antes de crear la
 * entidad Articulo, sin exponerla directamente. No incluye articuloId
 * porque lo genera la base de datos.
 * Reglas de negocio: textos obligatorios, existencias y montos no negativos,
 * existencias mínimas menores o iguales a las máximas y fecha de caducidad
 * obligatoria para artículos perecederos.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloSolicitudDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción admite máximo 255 caracteres")
    private String descripcion;

    @NotBlank(message = "El código de barras es obligatorio")
    @Size(max = 50, message = "El código de barras admite máximo 50 caracteres")
    private String codigoBarras;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 60, message = "La marca admite máximo 60 caracteres")
    private String marca;

    @NotBlank(message = "El proveedor es obligatorio")
    @Size(max = 100, message = "El proveedor admite máximo 100 caracteres")
    private String proveedor;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 20, message = "La unidad de medida admite máximo 20 caracteres")
    private String unidadMedida;

    @Size(max = 30, message = "El tamaño admite máximo 30 caracteres")
    private String tamanio;

    @NotNull(message = "Las existencias son obligatorias")
    @PositiveOrZero(message = "Las existencias no pueden ser negativas")
    private Integer existencias;

    @NotNull(message = "Las existencias mínimas son obligatorias")
    @PositiveOrZero(message = "Las existencias mínimas no pueden ser negativas")
    private Integer existenciasMinimas;

    @NotNull(message = "Las existencias máximas son obligatorias")
    @PositiveOrZero(message = "Las existencias máximas no pueden ser negativas")
    private Integer existenciasMaximas;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
    private BigDecimal costo;

    @NotNull(message = "El tipo es obligatorio")
    private TipoArticulo tipo;

    private LocalDate fechaCaducidad;

    /**
     * Comprueba que un artículo perecedero traiga fecha de caducidad.
     * El nombre usa el prefijo "is" porque Jakarta Validation solo evalúa
     * {@code @AssertTrue} en métodos con forma de propiedad (is/get).
     *
     * @return false solo cuando el artículo es PERECEDERO y no tiene fecha
     *         de caducidad; true en cualquier otro caso.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @AssertTrue(message = "La fecha de caducidad es obligatoria para artículos perecederos")
    public boolean isFechaCaducidadValida() {
        return tipo != TipoArticulo.PERECEDERO || fechaCaducidad != null;
    }

    /**
     * Comprueba que las existencias mínimas no superen a las máximas.
     * Si falta alguno de los dos valores, devuelve true para que solo se
     * reporte el error de {@code @NotNull} correspondiente.
     *
     * @return false cuando existenciasMinimas es mayor que
     *         existenciasMaximas; true en cualquier otro caso.
     * @author Olvera Ramírez Anuar Manuel Equipo 3
     *
     * Fecha creación: 09-11-2026
     *
     * Fecha modificación: 09-11-2026
     */
    @AssertTrue(message = "Las existencias mínimas no pueden ser mayores que las máximas")
    public boolean isRangoExistenciasValido() {
        return existenciasMinimas == null || existenciasMaximas == null
                || existenciasMinimas <= existenciasMaximas;
    }
}
