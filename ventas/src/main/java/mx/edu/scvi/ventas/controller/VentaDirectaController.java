package mx.edu.scvi.ventas.controller;

import java.time.LocalDate;
import java.util.List;
import mx.edu.scvi.ventas.model.Venta;
import mx.edu.scvi.ventas.service.VentaDirectaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas-directas")
public class VentaDirectaController {

    private final VentaDirectaService ventaService;

    public VentaDirectaController(VentaDirectaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaDirectaService.ResultadoVenta registrar(@RequestBody VentaDirectaService.Solicitud solicitud) {
        return ventaService.registrar(solicitud);
    }

    @GetMapping("/{id}")
    public VentaDirectaService.ResultadoVenta obtener(@PathVariable("id") long id) {
        return ventaService.obtener(id);
    }

    @PostMapping("/{id}/cancelar")
    public Venta cancelar(@PathVariable("id") long id) {
        return ventaService.cancelar(id);
    }

    @GetMapping
    public List<Venta> listar(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ventaService.listar(desde, hasta);
    }
}
