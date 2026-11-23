package mx.unam.fca.scvi.inventario.controlador;


import mx.unam.fca.scvi.inventario.modelo.Almacen;
import mx.unam.fca.scvi.inventario.repositorio.IAlmacenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AlmacenControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IAlmacenRepository almacenRepository;

    @BeforeEach
    void setUp() {
        almacenRepository.deleteAll();
    }

    @Test
    void integrar_RegistrarAlmacen() throws Exception {
        String jsonRequest = """
            {
                "nombre": "Almacen Norte",
                "direccion": "Gustavo A. Madero, CDMX"
            }
            """;

        mockMvc.perform(post("/api/almacenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.almacenId").exists())
                .andExpect(jsonPath("$.nombre").value("Almacen Norte"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void integrar_ListarAlmacenes() throws Exception {
        Almacen a = new Almacen();
        a.setNombre("Almacen Sur");
        a.setDireccion("Tlalpan, CDMX");
        a.setActivo(true);
        almacenRepository.save(a);

        mockMvc.perform(get("/api/almacenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Almacen Sur"));
    }
}