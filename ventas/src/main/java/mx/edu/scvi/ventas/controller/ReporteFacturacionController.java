package mx.edu.scvi.ventas.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.model.CuentaPorCobrar;
import mx.edu.scvi.ventas.model.Factura;
import mx.edu.scvi.ventas.model.Pedido;
import mx.edu.scvi.ventas.service.CuentaPorCobrarService;
import mx.edu.scvi.ventas.service.FacturacionService;
import mx.edu.scvi.ventas.service.ReporteFacturacionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Reportes 9 a 20 del subsistema de ventas (fechas en formato AAAA-MM-DD). */
@RestController
@RequestMapping("/api/reportes")
public class ReporteFacturacionController {

    private final ReporteFacturacionService reporteService;
    private final FacturacionService facturacionService;
    private final CuentaPorCobrarService cuentaService;

    public ReporteFacturacionController(ReporteFacturacionService reporteService,
                                        FacturacionService facturacionService,
                                        CuentaPorCobrarService cuentaService) {
        this.reporteService = reporteService;
        this.facturacionService = facturacionService;
        this.cuentaService = cuentaService;
    }

    @GetMapping("/pedidos/pendientes")
    public List<Pedido> pedidosPendientes(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.pedidosPendientes(desde, hasta);
    }

    @GetMapping("/pedidos/pendientes/cliente/{id}")
    public List<Pedido> pedidosPendientesDeCliente(@PathVariable("id") long id) {
        return reporteService.pedidosPendientesDeCliente(id);
    }

    @GetMapping("/pedidos/pendientes/vendedor/{id}")
    public List<Pedido> pedidosPendientesDeVendedor(@PathVariable("id") long id) {
        return reporteService.pedidosPendientesDeVendedor(id);
    }

    @GetMapping("/facturacion/periodo")
    public List<Factura> facturas(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return facturacionService.listar(desde, hasta);
    }

    @GetMapping("/facturacion/clientes")
    public List<Map<String, Object>> porCliente(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.facturacionPorCliente(desde, hasta);
    }

    @GetMapping("/facturacion/vendedores")
    public List<Map<String, Object>> porVendedor(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.facturacionPorVendedor(desde, hasta);
    }

    @GetMapping("/facturacion/productos")
    public List<Map<String, Object>> porProducto(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(name = "articuloId", required = false) Long articuloId) {
        return reporteService.facturacionPorProducto(desde, hasta, articuloId);
    }

    @GetMapping("/facturacion/ranking-articulos")
    public List<Map<String, Object>> ranking(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.rankingArticulos(desde, hasta);
    }

    @GetMapping("/facturacion/zonas")
    public List<Map<String, Object>> porZona(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.facturacionPorZona(desde, hasta);
    }

    @GetMapping("/cxc/cliente/{id}")
    public List<CuentaPorCobrar> cxcDeCliente(@PathVariable("id") long id) {
        return cuentaService.porCliente(id);
    }

    @GetMapping("/cxc/periodo")
    public List<CuentaPorCobrar> cxcPorPeriodo(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return cuentaService.porPeriodo(desde, hasta);
    }

    @GetMapping("/cxc/vencidas")
    public List<CuentaPorCobrarService.CuentaVencida> cxcVencidas() {
        return cuentaService.vencidas();
    }
}
