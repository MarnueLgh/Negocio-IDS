package mx.unam.fca.scvi.inventario.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.unam.fca.scvi.inventario.modelo.Bodega;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test") // Levanta base de datos H2 en memoria según los acuerdos del equipo
class BodegaControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void integrar_RegistrarBodegaExitosa() throws Exception {
        Bodega nuevaBodega = new Bodega(null, "Bodega Sur", "Bodega de refacciones");

        mockMvc.perform(post("/api/bodegas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaBodega)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Bodega Sur"));
    }

    @Test
    void integrar_RechazarBodegaSinNombre() throws Exception {
        Bodega bodegaInvalida = new Bodega(null, "", "Sin nombre");

        mockMvc.perform(post("/api/bodegas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodegaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El nombre de la bodega es un campo obligatorio."));
    }
}