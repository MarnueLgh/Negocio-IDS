package mx.unam.fca.scvi.inventario.controlador;

import mx.unam.fca.scvi.inventario.modelo.Articulo;
import mx.unam.fca.scvi.inventario.modelo.Bodega;
import mx.unam.fca.scvi.inventario.modelo.TipoArticulo;
import mx.unam.fca.scvi.inventario.repositorio.IArticuloRepositorio;
import mx.unam.fca.scvi.inventario.repositorio.IBodegaRepositorio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de la API de recepciones: controlador, servicio,
 * repositorios y base H2 en memoria (perfil test). Comprueban que la
 * recepción se persiste y que las existencias del artículo cambian.
 * Cada prueba crea su propio artículo porque comparten la base.
 *
 * @author Olvera Ramírez Anuar Manuel Equipo 3
 *
 * Fecha creación: 09-11-2026
 *
 * Fecha modificación: 09-11-2026
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecepcionControladorIntegracionTest {

    private static final String RUTA = "/api/inventario/recepciones";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IArticuloRepositorio articuloRepositorio;

    @Autowired
    private IBodegaRepositorio bodegaRepositorio;

    private Articulo crearArticulo(String codigoBarras, TipoArticulo tipo) {
        return articuloRepositorio.save(Articulo.builder()
                .nombre("Leche entera").codigoBarras(codigoBarras).marca("Lala")
                .proveedor("Distribuidora Norte").unidadMedida("litro")
                .existencias(10).existenciasMinimas(5).existenciasMaximas(50)
                .precio(new BigDecimal("28.50")).costo(new BigDecimal("21.00"))
                .tipo(tipo).fechaCaducidad(tipo == TipoArticulo.PERECEDERO ? LocalDate.of(2026, 12, 31) : null)
                .build());
    }

    private Long crearBodega(String nombre) {
        return bodegaRepositorio.save(Bodega.builder().nombre(nombre).build()).getBodegaId();
    }

    private String crearJson(Long articuloId, Long bodegaId, int cantidad, String estado, String lote) {
        String loteJson = lote == null ? "null" : "\"" + lote + "\"";
        return """
                {
                  "articuloId": %d,
                  "bodegaId": %d,
                  "cantidad": %d,
                  "fechaRecepcion": "%s",
                  "ubicacion": "Pasillo 3, Anaquel B",
                  "estado": "%s",
                  "lote": %s,
                  "fechaCaducidad": "%s"
                }
                """.formatted(articuloId, bodegaId, cantidad, LocalDate.now(), estado, loteJson,
                LocalDate.now().plusDays(30));
    }

    @Test
    void registrarRecepcionBuenaDevuelve201YSumaExistencias() throws Exception {
        Articulo articulo = crearArticulo("7501000000201", TipoArticulo.PERECEDERO);
        Long bodegaId = crearBodega("Bodega Prueba 1");

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content(crearJson(articulo.getArticuloId(), bodegaId, 5, "BUENO", "L-001")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recepcionId").isNumber())
                .andExpect(jsonPath("$.nombreBodega").value("Bodega Prueba 1"))
                .andExpect(jsonPath("$.lote").value("L-001"))
                .andExpect(jsonPath("$.excedeMaximo").value(false));

        assertEquals(15, articuloRepositorio.findById(articulo.getArticuloId()).orElseThrow().getExistencias());
        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].articuloId", hasItem(articulo.getArticuloId().intValue())));
    }

    @Test
    void registrarRecepcionDanadaNoSumaExistencias() throws Exception {
        Articulo articulo = crearArticulo("7501000000202", TipoArticulo.NO_PERECEDERO);
        Long bodegaId = crearBodega("Bodega Prueba 2");

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content(crearJson(articulo.getArticuloId(), bodegaId, 5, "DANADO", null)))
                .andExpect(status().isCreated());

        assertEquals(10, articuloRepositorio.findById(articulo.getArticuloId()).orElseThrow().getExistencias());
    }

    @Test
    void registrarPerecederoSinLoteDevuelve400() throws Exception {
        Articulo articulo = crearArticulo("7501000000203", TipoArticulo.PERECEDERO);
        Long bodegaId = crearBodega("Bodega Prueba 3");

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content(crearJson(articulo.getArticuloId(), bodegaId, 5, "BUENO", null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarCantidadCeroDevuelve400() throws Exception {
        Articulo articulo = crearArticulo("7501000000204", TipoArticulo.NO_PERECEDERO);
        Long bodegaId = crearBodega("Bodega Prueba 4");

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content(crearJson(articulo.getArticuloId(), bodegaId, 0, "BUENO", null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarEnBodegaInexistenteDevuelve404() throws Exception {
        Articulo articulo = crearArticulo("7501000000205", TipoArticulo.NO_PERECEDERO);

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content(crearJson(articulo.getArticuloId(), 999999L, 5, "BUENO", null)))
                .andExpect(status().isNotFound());
    }
}
