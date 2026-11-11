package mx.unam.fca.scvi.inventario.servicio;

import mx.unam.fca.scvi.inventario.modelo.Almacen;
import mx.unam.fca.scvi.inventario.repositorio.IAlmacenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlmacenServicioTest {

    @Mock
    private IAlmacenRepository almacenRepository;

    @InjectMocks
    private AlmacenService almacenService;

    private Almacen almacenValido;

    @BeforeEach
    void setUp() {
        almacenValido = new Almacen();
        almacenValido.setNombre("Almacen Central");
        almacenValido.setDireccion("Av. Universidad 3000, CDMX");
    }

    @Test
    void registrarAlmacen_Exitoso() {
        when(almacenRepository.existsByNombre(almacenValido.getNombre())).thenReturn(false);
        when(almacenRepository.save(any(Almacen.class))).thenAnswer(invocation -> {
            Almacen a = invocation.getArgument(0);
            a.setAlmacenId(1L);
            return a;
        });

        Almacen resultado = almacenService.registrarAlmacen(almacenValido);

        assertNotNull(resultado.getAlmacenId());
        assertTrue(resultado.getActivo());
        verify(almacenRepository, times(1)).save(almacenValido);
    }

    @Test
    void registrarAlmacen_FallaPorNombreDuplicado() {
        when(almacenRepository.existsByNombre(almacenValido.getNombre())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            almacenService.registrarAlmacen(almacenValido);
        });

        assertEquals("El nombre del almacen ya se encuentra registrado.", exception.getMessage());
        verify(almacenRepository, never()).save(any());
    }

    @Test
    void listarAlmacenes_RetornaLista() {
        when(almacenRepository.findAll()).thenReturn(Arrays.asList(almacenValido));

        List<Almacen> almacenes = almacenService.listarAlmacenes();

        assertFalse(almacenes.isEmpty());
        assertEquals(1, almacenes.size());
    }
}
