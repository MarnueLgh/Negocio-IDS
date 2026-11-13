package mx.edu.scvi.ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SCVI - Subsistema de Ventas.
 * Monolito modular: controller -> service -> dao -> BD (dependencias solo hacia abajo).
 */
@SpringBootApplication
public class VentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(VentasApplication.class, args);
    }
}
