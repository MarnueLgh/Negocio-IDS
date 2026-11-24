package mx.unam.fca.scvi.ventas;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

/**
 * Prueba de integración de Ventas sobre la aplicación completa (ScviApplication) con el perfil test:
 * H2 en memoria y tablas creadas por Hibernate. Cada prueba carga los artículos de prueba en la tabla
 * Articulo de Inventario y hace rollback al terminar.
 *
 * Subsistema: Ventas.
 *
 * @author Álvarez Aguilar Saúl Antonio Equipo 2
 * @author Pérez Flores Roberto Carlos Equipo 2
 * @author Pérez Muñoz Diego Equipo 2
 *
 * Fecha creación: 15-11-2026
 *
 * Fecha modificación: 23-11-2026
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql("/ventas/articulos-prueba.sql")
public @interface PruebaIntegracion {
}
