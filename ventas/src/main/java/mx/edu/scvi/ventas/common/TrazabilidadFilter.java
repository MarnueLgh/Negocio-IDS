package mx.edu.scvi.ventas.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Filtro de trazabilidad: asigna X-Trace-Id a cada petición y registra método, ruta, estado y duración. */
@Component
public class TrazabilidadFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TrazabilidadFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String traza = request.getHeader("X-Trace-Id");
        if (traza == null || traza.isBlank()) {
            traza = UUID.randomUUID().toString();
        }
        traza = traza.replaceAll("[^A-Za-z0-9-]", "");
        if (traza.length() > 64) {
            traza = traza.substring(0, 64);
        }
        MDC.put("traza", traza);
        response.setHeader("X-Trace-Id", traza);
        long inicio = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            LOG.info("{} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(), response.getStatus(), ms);
            MDC.remove("traza");
        }
    }
}
