package mx.edu.scvi.ventas.controller;

import java.time.LocalDate;
import java.util.List;
import mx.edu.scvi.ventas.model.Factura;
import mx.edu.scvi.ventas.service.FacturacionService;
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
@RequestMapping("/api/facturas")
public class FacturaController {

    /** Cuerpo opcional: permite cambiar los días de crédito (nunca más de los otorgados al cliente). */
    public record FacturarRequest(Integer diasCredito) {
    }

    private final FacturacionService facturacionService;

    public FacturaController(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @PostMapping("/pedidos/{pedidoId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Factura facturarPedido(@PathVariable("pedidoId") long pedidoId,
                                  @RequestBody(required = false) FacturarRequest request) {
        return facturacionService.facturarPedido(pedidoId, request == null ? null : request.diasCredito());
    }

    @GetMapping("/{id}")
    public FacturacionService.ResultadoFactura obtener(@PathVariable("id") long id) {
        return facturacionService.obtener(id);
    }

    @PostMapping("/{id}/cancelar")
    public Factura cancelar(@PathVariable("id") long id) {
        return facturacionService.cancelar(id);
    }

    @PostMapping("/{id}/sustituir")
    @ResponseStatus(HttpStatus.CREATED)
    public Factura sustituir(@PathVariable("id") long id,
                             @RequestBody(required = false) FacturarRequest request) {
        return facturacionService.sustituir(id, request == null ? null : request.diasCredito());
    }

    @GetMapping
    public List<Factura> listar(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return facturacionService.listar(desde, hasta);
    }
}
