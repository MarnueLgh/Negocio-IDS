package mx.edu.scvi.ventas.controller;

import mx.edu.scvi.ventas.model.Pedido;
import mx.edu.scvi.ventas.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoService.Resultado crear(@RequestBody PedidoService.Solicitud solicitud) {
        return pedidoService.crear(solicitud);
    }

    @GetMapping("/{id}")
    public PedidoService.Resultado obtener(@PathVariable("id") long id) {
        return pedidoService.obtener(id);
    }

    @PostMapping("/{id}/entregar")
    public PedidoService.Resultado entregar(@PathVariable("id") long id) {
        return pedidoService.entregar(id);
    }

    @PostMapping("/{id}/cancelar")
    public Pedido cancelar(@PathVariable("id") long id) {
        return pedidoService.cancelar(id);
    }
}
