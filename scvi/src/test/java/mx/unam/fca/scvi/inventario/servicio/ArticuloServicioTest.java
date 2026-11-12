package mx.unam.fca.scvi.inventario.servicio;

import mx.unam.fca.scvi.inventario.modelo.Articulo;
import mx.unam.fca.scvi.inventario.modelo.ArticuloSolicitudDto;
import mx.unam.fca.scvi.inventario.modelo.TipoArticulo;
import mx.unam.fca.scvi.inventario.repositorio.IArticuloRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de ArticuloServicio con el repositorio simulado
 * (Mockito), sin base de datos.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@ExtendWith(MockitoExtension.class)
class ArticuloServicioTest {

    @Mock
    private IArticuloRepositorio articuloRepositorio;

    @InjectMocks
    private ArticuloServicio articuloServicio;

    private ArticuloSolicitudDto crearSolicitud(TipoArticulo tipo, LocalDate fechaCaducidad) {
        return ArticuloSolicitudDto.builder()
                .nombre("Leche entera").codigoBarras("7501000000001").marca("Lala")
                .proveedor("Distribuidora Norte").unidadMedida("litro")
                .existencias(10).existenciasMinimas(5).existenciasMaximas(50)
                .precio(new BigDecimal("28.50")).costo(new BigDecimal("21.00"))
                .tipo(tipo).fechaCaducidad(fechaCaducidad)
                .build();
    }

    @Test
    void registrarArticuloGuardaYDevuelveElArticulo() {
        LocalDate caducidad = LocalDate.of(2026, 12, 31);
        when(articuloRepositorio.existsByCodigoBarras("7501000000001")).thenReturn(false);
        when(articuloRepositorio.save(any(Articulo.class))).thenAnswer(invocacion -> {
            Articulo articulo = invocacion.getArgument(0);
            articulo.setArticuloId(1L);
            return articulo;
        });

        Articulo resultado = articuloServicio.registrarArticulo(
                crearSolicitud(TipoArticulo.PERECEDERO, caducidad));

        assertEquals(1L, resultado.getArticuloId());
        assertEquals("7501000000001", resultado.getCodigoBarras());
        assertEquals(caducidad, resultado.getFechaCaducidad());
    }

    @Test
    void registrarArticuloRechazaCodigoBarrasDuplicadoSinGuardar() {
        when(articuloRepositorio.existsByCodigoBarras("7501000000001")).thenReturn(true);

        assertThrows(CodigoBarrasDuplicadoExcepcion.class, () -> articuloServicio.registrarArticulo(
                crearSolicitud(TipoArticulo.NO_PERECEDERO, null)));
        verify(articuloRepositorio, never()).save(any());
    }

    @Test
    void registrarArticuloNoPerecederoDescartaFechaCaducidad() {
        when(articuloRepositorio.existsByCodigoBarras(any())).thenReturn(false);
        when(articuloRepositorio.save(any(Articulo.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Articulo resultado = articuloServicio.registrarArticulo(
                crearSolicitud(TipoArticulo.NO_PERECEDERO, LocalDate.of(2026, 12, 31)));

        assertNull(resultado.getFechaCaducidad());
    }

    @Test
    void obtenerArticuloInexistenteLanzaExcepcion() {
        when(articuloRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ArticuloNoEncontradoExcepcion.class, () -> articuloServicio.obtenerArticulo(99L));
    }
}
