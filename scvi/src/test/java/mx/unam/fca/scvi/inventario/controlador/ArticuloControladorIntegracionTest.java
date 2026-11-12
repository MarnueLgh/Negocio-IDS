package mx.unam.fca.scvi.inventario.controlador;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de la API de artículos: controlador, servicio,
 * repositorio y base H2 en memoria (perfil test).
 * Cada prueba usa un código de barras distinto porque comparten la base.
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
class ArticuloControladorIntegracionTest {

    private static final String RUTA = "/api/inventario/articulos";

    @Autowired
    private MockMvc mockMvc;

    private String crearJson(String codigoBarras, String tipo, String fechaCaducidad) {
        String fecha = fechaCaducidad == null ? "null" : "\"" + fechaCaducidad + "\"";
        return """
                {
                  "nombre": "Leche entera",
                  "codigoBarras": "%s",
                  "marca": "Lala",
                  "proveedor": "Distribuidora Norte",
                  "unidadMedida": "litro",
                  "existencias": 10,
                  "existenciasMinimas": 5,
                  "existenciasMaximas": 50,
                  "precio": 28.50,
                  "costo": 21.00,
                  "tipo": "%s",
                  "fechaCaducidad": %s
                }
                """.formatted(codigoBarras, tipo, fecha);
    }

    @Test
    void registrarArticuloValidoDevuelve201YApareceEnLaLista() throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content(crearJson("7501000000101", "PERECEDERO", "2026-12-31")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.articuloId").isNumber())
                .andExpect(jsonPath("$.fechaCaducidad").value("2026-12-31"));

        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].codigoBarras", hasItem("7501000000101")));
    }

    @Test
    void registrarCodigoBarrasDuplicadoDevuelve409() throws Exception {
        String json = crearJson("7501000000102", "NO_PERECEDERO", null);
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void registrarPerecederoSinFechaCaducidadDevuelve400() throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content(crearJson("7501000000103", "PERECEDERO", null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerArticuloInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get(RUTA + "/999999"))
                .andExpect(status().isNotFound());
    }
}
