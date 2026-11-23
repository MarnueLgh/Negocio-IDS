package mx.unam.fca.scvi.ventas.controller;

import mx.unam.fca.scvi.ventas.model.Pedido;
import mx.unam.fca.scvi.ventas.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST para crear, consultar, entregar y cancelar pedidos (/api/pedidos).
 * Delega en PedidoService.
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
