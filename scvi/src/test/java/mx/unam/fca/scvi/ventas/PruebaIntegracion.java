package mx.edu.scvi.ventas;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/** Prueba de integración con BD H2 en memoria; cada prueba hace rollback al terminar. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:ventas_test;DB_CLOSE_DELAY=-1")
@Transactional
public @interface PruebaIntegracion {
}
