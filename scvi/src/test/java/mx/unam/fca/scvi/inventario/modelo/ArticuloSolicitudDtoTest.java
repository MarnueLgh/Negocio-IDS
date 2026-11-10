package mx.unam.fca.scvi.inventario.modelo;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de las reglas de Jakarta Validation de ArticuloSolicitudDto.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
class ArticuloSolicitudDtoTest {

    private static ValidatorFactory fabricaValidadores;
    private static Validator validador;

    @BeforeAll
    static void crearValidador() {
        fabricaValidadores = Validation.buildDefaultValidatorFactory();
        validador = fabricaValidadores.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        fabricaValidadores.close();
    }

    private ArticuloSolicitudDto crearSolicitudValida() {
        return ArticuloSolicitudDto.builder()
                .nombre("Leche entera").codigoBarras("7501000000001").marca("Lala")
                .proveedor("Distribuidora Norte").unidadMedida("litro")
                .existencias(10).existenciasMinimas(5).existenciasMaximas(50)
                .precio(new BigDecimal("28.50")).costo(new BigDecimal("21.00"))
                .tipo(TipoArticulo.PERECEDERO).fechaCaducidad(LocalDate.of(2026, 12, 31))
                .build();
    }

    private String obtenerUnicoMensaje(ArticuloSolicitudDto solicitud) {
        var violaciones = validador.validate(solicitud);
        assertEquals(1, violaciones.size(), () -> "Violaciones: " + violaciones);
        return violaciones.iterator().next().getMessage();
    }

    @Test
    void solicitudCompletaNoTieneViolaciones() {
        assertTrue(validador.validate(crearSolicitudValida()).isEmpty());
    }

    @Test
    void perecederoSinFechaCaducidadEsInvalido() {
        ArticuloSolicitudDto solicitud = crearSolicitudValida();
        solicitud.setFechaCaducidad(null);

        assertEquals("La fecha de caducidad es obligatoria para artículos perecederos",
                obtenerUnicoMensaje(solicitud));
    }

    @Test
    void noPerecederoSinFechaCaducidadEsValido() {
        ArticuloSolicitudDto solicitud = crearSolicitudValida();
        solicitud.setTipo(TipoArticulo.NO_PERECEDERO);
        solicitud.setFechaCaducidad(null);

        assertTrue(validador.validate(solicitud).isEmpty());
    }

    @Test
    void existenciasMinimasMayoresQueMaximasEsInvalido() {
        ArticuloSolicitudDto solicitud = crearSolicitudValida();
        solicitud.setExistenciasMinimas(60);

        assertEquals("Las existencias mínimas no pueden ser mayores que las máximas",
                obtenerUnicoMensaje(solicitud));
    }

    @Test
    void precioNegativoEsInvalido() {
        ArticuloSolicitudDto solicitud = crearSolicitudValida();
        solicitud.setPrecio(new BigDecimal("-1.00"));

        assertEquals("El precio no puede ser negativo", obtenerUnicoMensaje(solicitud));
    }

    @Test
    void nombreEnBlancoEsInvalido() {
        ArticuloSolicitudDto solicitud = crearSolicitudValida();
        solicitud.setNombre("   ");

        assertEquals("El nombre es obligatorio", obtenerUnicoMensaje(solicitud));
    }
}
