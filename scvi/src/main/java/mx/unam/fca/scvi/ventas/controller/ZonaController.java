package mx.edu.scvi.ventas.controller;

import java.util.List;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.model.Zona;
import mx.edu.scvi.ventas.service.ZonaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    public record ZonaRequest(String nombre, Boolean activo) {
    }

    public record UnirRequest(Long zonaOrigenId, Long zonaDestinoId) {
    }

    public record DividirRequest(String nuevoNombre, List<Long> clienteIds) {
    }

    private final ZonaService zonaService;

    public ZonaController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @GetMapping
    public List<Zona> listar() {
        return zonaService.listar();
    }

    @GetMapping("/{id}")
    public Zona obtener(@PathVariable("id") long id) {
        return zonaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Zona crear(@RequestBody ZonaRequest request) {
        return zonaService.crear(request.nombre());
    }

    @PutMapping("/{id}")
    public Zona modificar(@PathVariable("id") long id, @RequestBody ZonaRequest request) {
        boolean activo = request.activo() == null || request.activo();
        return zonaService.modificar(id, request.nombre(), activo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {
        zonaService.eliminar(id);
    }

    @PostMapping("/unir")
    public ZonaService.MovimientoZona unir(@RequestBody UnirRequest request) {
        Reglas.exigir(request.zonaOrigenId() != null && request.zonaDestinoId() != null,
                "zonaOrigenId y zonaDestinoId son obligatorios");
        return zonaService.unir(request.zonaOrigenId(), request.zonaDestinoId());
    }

    @PostMapping("/{id}/dividir")
    @ResponseStatus(HttpStatus.CREATED)
    public Zona dividir(@PathVariable("id") long id, @RequestBody DividirRequest request) {
        return zonaService.dividir(id, request.nuevoNombre(), request.clienteIds());
    }
}
