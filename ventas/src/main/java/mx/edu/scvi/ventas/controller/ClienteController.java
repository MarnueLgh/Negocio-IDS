package mx.edu.scvi.ventas.controller;

import java.util.List;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listar(@RequestParam(name = "zonaId", required = false) Long zonaId) {
        return zonaId == null ? clienteService.listar() : clienteService.listarPorZona(zonaId);
    }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable("id") long id) {
        return clienteService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente registrar(@RequestBody Cliente cliente) {
        return clienteService.registrar(cliente);
    }

    @PutMapping("/{id}")
    public Cliente modificar(@PathVariable("id") long id, @RequestBody Cliente cliente) {
        return clienteService.modificar(id, cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void baja(@PathVariable("id") long id) {
        clienteService.baja(id);
    }
}
