package mx.edu.scvi.ventas.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.model.Comision;
import mx.edu.scvi.ventas.service.ComisionService;
import mx.edu.scvi.ventas.service.ReporteVendedorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Reportes 1 a 6 del subsistema de ventas (fechas en formato AAAA-MM-DD). */
@RestController
@RequestMapping("/api/reportes")
public class ReporteVendedorController {

    private final ReporteVendedorService reporteService;
    private final ComisionService comisionService;

    public ReporteVendedorController(ReporteVendedorService reporteService, ComisionService comisionService) {
        this.reporteService = reporteService;
        this.comisionService = comisionService;
    }

    @GetMapping("/vendedores/comisiones")
    public List<Map<String, Object>> comisiones(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.comisionesPorEmpleado(desde, hasta);
    }

    @GetMapping("/vendedores/{id}/comisiones")
    public List<Comision> comisionesDeEmpleado(
            @PathVariable("id") long id,
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return comisionService.listarPorEmpleado(id, desde, hasta);
    }

    @GetMapping("/vendedores/ventas")
    public List<Map<String, Object>> ventasPorVendedor(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.ventasPorVendedor(desde, hasta);
    }

    @GetMapping("/vendedores/{id}/clientes")
    public List<Map<String, Object>> clientesDeVendedor(@PathVariable("id") long id) {
        return reporteService.clientesDeVendedor(id);
    }

    @GetMapping("/clientes/{id}/vendedores")
    public List<Map<String, Object>> vendedoresDeCliente(@PathVariable("id") long id) {
        return reporteService.vendedoresDeCliente(id);
    }

    @GetMapping("/zonas/vendedores")
    public List<Map<String, Object>> vendedoresPorZona(
            @RequestParam(name = "zonaId", required = false) Long zonaId) {
        return reporteService.vendedoresPorZona(zonaId);
    }

    @GetMapping("/zonas/clientes")
    public List<Map<String, Object>> clientesPorZona(
            @RequestParam(name = "zonaId", required = false) Long zonaId) {
        return reporteService.clientesPorZona(zonaId);
    }
}
