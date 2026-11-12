package mx.unam.fca.scvi.inventario.servicio;

import mx.unam.fca.scvi.inventario.modelo.Articulo;
import mx.unam.fca.scvi.inventario.modelo.Bodega;
import mx.unam.fca.scvi.inventario.modelo.EstadoMercancia;
import mx.unam.fca.scvi.inventario.modelo.Recepcion;
import mx.unam.fca.scvi.inventario.modelo.RecepcionRespuestaDto;
import mx.unam.fca.scvi.inventario.modelo.RecepcionSolicitudDto;
import mx.unam.fca.scvi.inventario.modelo.TipoArticulo;
import mx.unam.fca.scvi.inventario.repositorio.IArticuloRepositorio;
import mx.unam.fca.scvi.inventario.repositorio.IBodegaRepositorio;
import mx.unam.fca.scvi.inventario.repositorio.IRecepcionRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de RecepcionServicio con repositorios y ArticuloServicio
 * simulados (Mockito), sin base de datos.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@ExtendWith(MockitoExtension.class)
class RecepcionServicioTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 9);

    @Mock
    private IRecepcionRepositorio recepcionRepositorio;

    @Mock
    private IBodegaRepositorio bodegaRepositorio;

    @Mock
    private IArticuloRepositorio articuloRepositorio;

    @Mock
    private ArticuloServicio articuloServicio;

    @InjectMocks
    private RecepcionServicio recepcionServicio;

    private Articulo crearArticulo(TipoArticulo tipo, boolean activo) {
        return Articulo.builder().articuloId(1L).nombre("Leche entera").tipo(tipo)
                .existencias(10).existenciasMaximas(50).activo(activo).build();
    }

    private RecepcionSolicitudDto crearSolicitud(int cantidad, EstadoMercancia estado,
                                                 String lote, LocalDate fechaCaducidad) {
        return RecepcionSolicitudDto.builder()
                .articuloId(1L).bodegaId(1L).cantidad(cantidad).fechaRecepcion(HOY)
                .ubicacion("Pasillo 3, Anaquel B").estado(estado)
                .lote(lote).fechaCaducidad(fechaCaducidad)
                .build();
    }

    private void prepararArticuloYBodega(Articulo articulo) {
        when(articuloServicio.obtenerArticulo(1L)).thenReturn(articulo);
        lenient().when(bodegaRepositorio.findById(1L))
                .thenReturn(Optional.of(Bodega.builder().bodegaId(1L).nombre("Bodega Central").build()));
        lenient().when(recepcionRepositorio.save(any(Recepcion.class))).thenAnswer(invocacion -> {
            Recepcion recepcion = invocacion.getArgument(0);
            recepcion.setRecepcionId(7L);
            return recepcion;
        });
    }

    @Test
    void registrarPerecederoBuenoGuardaYSumaExistencias() {
        prepararArticuloYBodega(crearArticulo(TipoArticulo.PERECEDERO, true));

        RecepcionRespuestaDto resultado = recepcionServicio.registrarRecepcion(
                crearSolicitud(5, EstadoMercancia.BUENO, "L-001", HOY.plusDays(30)));

        assertEquals(7L, resultado.getRecepcionId());
        assertEquals("Bodega Central", resultado.getNombreBodega());
        assertEquals("L-001", resultado.getLote());
        assertFalse(resultado.isExcedeMaximo());
        verify(articuloRepositorio).sumarExistencias(1L, 5);
    }

    @Test
    void registrarMercanciaDanadaNoSumaExistencias() {
        prepararArticuloYBodega(crearArticulo(TipoArticulo.NO_PERECEDERO, true));

        recepcionServicio.registrarRecepcion(crearSolicitud(5, EstadoMercancia.DANADO, null, null));

        verify(recepcionRepositorio).save(any(Recepcion.class));
        verify(articuloRepositorio, never()).sumarExistencias(anyLong(), anyInt());
    }

    @Test
    void registrarNoPerecederoDescartaLoteYCaducidad() {
        prepararArticuloYBodega(crearArticulo(TipoArticulo.NO_PERECEDERO, true));

        RecepcionRespuestaDto resultado = recepcionServicio.registrarRecepcion(
                crearSolicitud(5, EstadoMercancia.BUENO, "L-001", HOY.plusDays(30)));

        assertNull(resultado.getLote());
        assertNull(resultado.getFechaCaducidad());
    }

    @Test
    void registrarQueSuperaMaximoSeGuardaYLoIndica() {
        prepararArticuloYBodega(crearArticulo(TipoArticulo.NO_PERECEDERO, true));

        RecepcionRespuestaDto resultado = recepcionServicio.registrarRecepcion(
                crearSolicitud(41, EstadoMercancia.BUENO, null, null));

        assertTrue(resultado.isExcedeMaximo());
        verify(articuloRepositorio).sumarExistencias(1L, 41);
    }

    @Test
    void registrarArticuloInactivoLanzaExcepcionSinGuardar() {
        prepararArticuloYBodega(crearArticulo(TipoArticulo.NO_PERECEDERO, false));

        assertThrows(RecepcionInvalidaExcepcion.class, () -> recepcionServicio.registrarRecepcion(
                crearSolicitud(5, EstadoMercancia.BUENO, null, null)));
        verify(recepcionRepositorio, never()).save(any());
    }

    @Test
    void registrarEnBodegaInexistenteLanzaExcepcion() {
        when(articuloServicio.obtenerArticulo(1L)).thenReturn(crearArticulo(TipoArticulo.NO_PERECEDERO, true));
        when(bodegaRepositorio.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BodegaNoEncontradaExcepcion.class, () -> recepcionServicio.registrarRecepcion(
                crearSolicitud(5, EstadoMercancia.BUENO, null, null)));
        verify(recepcionRepositorio, never()).save(any());
    }

    @Test
    void registrarPerecederoSinLoteLanzaExcepcion() {
        prepararArticuloYBodega(crearArticulo(TipoArticulo.PERECEDERO, true));

        assertThrows(RecepcionInvalidaExcepcion.class, () -> recepcionServicio.registrarRecepcion(
                crearSolicitud(5, EstadoMercancia.BUENO, " ", HOY.plusDays(30))));
        verify(recepcionRepositorio, never()).save(any());
    }

    @Test
    void registrarPerecederoCaducadoLanzaExcepcion() {
        prepararArticuloYBodega(crearArticulo(TipoArticulo.PERECEDERO, true));

        assertThrows(RecepcionInvalidaExcepcion.class, () -> recepcionServicio.registrarRecepcion(
                crearSolicitud(5, EstadoMercancia.BUENO, "L-001", HOY)));
        verify(recepcionRepositorio, never()).save(any());
    }
}
