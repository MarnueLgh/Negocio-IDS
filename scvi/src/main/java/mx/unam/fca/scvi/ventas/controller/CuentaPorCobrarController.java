package mx.edu.scvi.ventas.controller;

import java.math.BigDecimal;
import mx.edu.scvi.ventas.model.CuentaPorCobrar;
import mx.edu.scvi.ventas.model.FormaPago;
import mx.edu.scvi.ventas.service.CuentaPorCobrarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas-por-cobrar")
public class CuentaPorCobrarController {

    public record PagoRequest(BigDecimal importe, FormaPago formaPago) {
    }

    private final CuentaPorCobrarService cuentaService;

    public CuentaPorCobrarController(CuentaPorCobrarService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping("/{id}")
    public CuentaPorCobrar obtener(@PathVariable("id") long id) {
        return cuentaService.obtener(id);
    }

    @PostMapping("/{id}/pagos")
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaPorCobrarService.ResultadoPago pagar(@PathVariable("id") long id, @RequestBody PagoRequest request) {
        return cuentaService.registrarPago(id, request.importe(), request.formaPago());
    }
}
