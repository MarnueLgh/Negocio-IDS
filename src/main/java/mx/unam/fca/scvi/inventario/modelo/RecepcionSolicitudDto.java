package mx.unam.fca.scvi.inventario.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Datos que envía el cliente para registrar una llegada de mercancía.
 * Responsabilidad principal: validar la entrada de la API antes de crear la
 * entidad Recepcion. Solo trae el identificador de la bodega destino, nunca
 * el de un almacén.
 * Reglas de negocio: artículo, bodega, cantidad positiva, fecha no futura,
 * ubicación y estado son obligatorios. Lote y fecha de caducidad son
 * opcionales aquí porque dependen del tipo del artículo, que solo conoce la
 * base de datos; los valida RecepcionServicio.
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
public class RecepcionSolicitudDto {

    @NotNull(message = "El artículo es obligatorio")
    private Long articuloId;

    @NotNull(message = "La bodega destino es obligatoria")
    private Long bodegaId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que cero")
    private Integer cantidad;

    @NotNull(message = "La fecha de recepción es obligatoria")
    @PastOrPresent(message = "La fecha de recepción no puede ser futura")
    private LocalDate fechaRecepcion;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 50, message = "La ubicación admite máximo 50 caracteres")
    private String ubicacion;

    @NotNull(message = "El estado es obligatorio")
    private EstadoMercancia estado;

    @Size(max = 30, message = "El lote admite máximo 30 caracteres")
    private String lote;

    private LocalDate fechaCaducidad;
}
