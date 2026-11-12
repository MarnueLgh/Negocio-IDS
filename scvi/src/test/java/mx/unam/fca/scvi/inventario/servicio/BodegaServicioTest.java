package mx.unam.fca.scvi.inventario.servicio;

import mx.unam.fca.scvi.inventario.modelo.Bodega;
import mx.unam.fca.scvi.inventario.repositorio.IBodegaRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BodegaServicioTest {

    @Mock
    private IBodegaRepositorio bodegaRepositorio;

    @InjectMocks
    private BodegaServicio bodegaServicio;

    @Test
    void registrarBodega_Exitoso() {
        Bodega entrada = new Bodega(null, "Bodega Central", "Principal");
        Bodega salida = new Bodega(1L, "Bodega Central", "Principal");

        when(bodegaRepositorio.existsByNombre("Bodega Central")).thenReturn(false);
        when(bodegaRepositorio.save(any(Bodega.class))).thenReturn(salida);

        Bodega resultado = bodegaServicio.registrarBodega(entrada);

        assertNotNull(resultado.getId());
        assertEquals("Bodega Central", resultado.getNombre());
        verify(bodegaRepositorio, times(1)).save(entrada);
    }

    @Test
    void registrarBodega_LanzaExcepcion_SiNombreDuplicado() {
        Bodega entrada = new Bodega(null, "Bodega Norte", null);
        when(bodegaRepositorio.existsByNombre("Bodega Norte")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
                () -> bodegaServicio.registrarBodega(entrada));

        assertEquals("Ya existe una bodega registrada con el nombre proporcionado.", excepcion.getMessage());
        verify(bodegaRepositorio, never()).save(any());
    }

    @Test
    void consultarBodega_LanzaExcepcion_SiNoExiste() {
        when(bodegaRepositorio.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
                () -> bodegaServicio.consultarBodega(99L));

        assertTrue(excepcion.getMessage().contains("No se encontró ninguna bodega"));
    }
}