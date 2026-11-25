package mx.unam.fca.scvi.inventario.servicio;

import lombok.RequiredArgsConstructor;
import mx.unam.fca.scvi.inventario.modelo.Articulo;
import mx.unam.fca.scvi.inventario.repositorio.IArticuloRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de negocio para la generación de reportes del subsistema de Inventario (historia INV-13).
 * Responsabilidad principal: consultar y consolidar la información de reportes a partir de
 * IArticuloRepositorio, inyectado por constructor.
 * Reglas de negocio:
 * -E l costo total devuelve cero si no hay registros y el reporte de baja salida
 *  permanece pendiente hasta contar con la definición de ajustes/salidas de INV-11.
 *
 * @author Islas López Isis Jezabel Equipo 1.
 * Fecha creación: 23-11-2026
 * Fecha modificación: 23-11-2026
 */
@Service
@RequiredArgsConstructor
public class ReporteServicio {

    private final IArticuloRepositorio articuloRepositorio;

    /**
     * Obtiene el listado general de artículos registrados dentro del inventario.
     *
     * @return lista completa de artículos disponibles.
     * @author Islas López Isis Jezabel Equipo 1.
     * Fecha creación: 23-11-2026
     * Fecha modificación: 23-11-2026
     */
    public List<Articulo> generarListadoGeneral() {

        return articuloRepositorio.findAll();
    }

    /**
     * Genera el reporte de artículos ordenados por existencias de mayor a
     * menor.
     *
     * @return lista de artículos ordenados de manera descendente por existencias.
     * @author Islas López Isis Jezabel Equipo 1.
     * Fecha creación: 23-11-2026
     * Fecha modificación: 23-11-2026
     */
    public List<Articulo> generarReportePorExistencias() {

        return articuloRepositorio.findAllByOrderByExistenciasDesc();
    }

    /**
     * Genera el reporte de artículos con existencias iguales o por debajo de su
     * existencia mínima, ordenados de menor a mayor.
     *
     * @return lista de artículos con bajo stock de manera ascendente por existencias.
     * @author Islas López Isis Jezabel Equipo 1.
     * Fecha creación: 23-11-2026
     * Fecha modificación: 23-11-2026
     */
    public List<Articulo> generarReporteBajoStock() {

        return articuloRepositorio.findBajoStockOrdenadoAsc();
    }

    /**
     * Calcula el costo total acumulado del inventario.
     *
     * @return valor monetario total acumulado, o cero si no hay registros
     * @author Islas López Isis Jezabel Equipo 1.
     * Fecha creación: 23-11-2026
     * Fecha modificación: 23-11-2026
     */
    public Double calcularCostoTotalInventario() {

        Double total = articuloRepositorio.calcularCostoTotalInventario();

        if (total == null) {

            return 0.0;
        }

        return total;
    }

    /**
     * Valida el número de días sin salida para el reporte de baja salida; su
     * ejecución completa queda pendiente de la definición de INV-11.
     *
     * @param diasSinSalida número mínimo de días sin movimientos de salida; debe ser mayor que cero.
     * @return lista de artículos con baja salida una vez implementada INV-11.
     * @throws IllegalArgumentException si diasSinSalida es menor o igual a cero.
     * @throws UnsupportedOperationException mientras la funcionalidad de ajustes/salidas de INV-11 no esté disponible.
     * @author Islas López Isis Jezabel Equipo 1.
     * Fecha creación: 23-11-2026
     * Fecha modificación: 23-11-2026
     */
    public List<Articulo> generarReporteBajaSalida(int diasSinSalida) {

        if (diasSinSalida <= 0) {

            throw new IllegalArgumentException("El umbral de días sin salida debe ser mayor que cero.");
        }
        // Pendiente de definir la entidad/tabla de ajustes de INV-11
        throw new UnsupportedOperationException(
                "El reporte de baja salida requiere la definición de ajustes/salidas de INV-11"
        );

    }

}