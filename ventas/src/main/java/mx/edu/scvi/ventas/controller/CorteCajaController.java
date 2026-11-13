package mx.edu.scvi.ventas.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.model.CorteCaja;
import mx.edu.scvi.ventas.service.CorteCajaService;
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
@RequestMapping("/api")
public class CorteCajaController {

    public record AbrirRequest(Long cajeroId, Integer cajaId, BigDecimal importeInicial) {
    }

    public record CerrarRequest(BigDecimal importeFisico) {
    }

    private final CorteCajaService corteService;

    public CorteCajaController(CorteCajaService corteService) {
        this.corteService = corteService;
    }

    @PostMapping("/cortes-caja")
    @ResponseStatus(HttpStatus.CREATED)
    public CorteCaja abrir(@RequestBody AbrirRequest request) {
        int cajaId = request.cajaId() == null ? 1 : request.cajaId();
        return corteService.abrir(request.cajeroId(), cajaId, request.importeInicial());
    }

    @PostMapping("/cortes-caja/{id}/cerrar")
    public CorteCaja cerrar(@PathVariable("id") long id, @RequestBody CerrarRequest request) {
        return corteService.cerrar(id, request.importeFisico());
    }

    @GetMapping("/cortes-caja/{id}")
    public CorteCaja obtener(@PathVariable("id") long id) {
        return corteService.obtener(id);
    }

    @GetMapping("/reportes/caja/ventas-directas")
    public List<Map<String, Object>> ventasPorCaja(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return corteService.ventasPorCaja(desde, hasta);
    }

    @GetMapping("/reportes/caja/diferencias")
    public List<CorteCaja> diferencias(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(name = "soloConDiferencia", defaultValue = "false") boolean soloConDiferencia) {
        return corteService.cortes(desde, hasta, soloConDiferencia);
    }
}
