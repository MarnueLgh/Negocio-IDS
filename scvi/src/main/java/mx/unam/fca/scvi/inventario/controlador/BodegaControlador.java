package mx.unam.fca.scvi.inventario.controlador;

import mx.unam.fca.scvi.inventario.modelo.Bodega;
import mx.unam.fca.scvi.inventario.servicio.BodegaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para exponer las operaciones de Bodegas (Historia INV-01).
 *
 * @author Equipo 3
 */
@RestController
@RequestMapping("/api/bodegas")
public class BodegaControlador {

    private final BodegaService bodegaService;

    @Autowired
    public BodegaControlador(BodegaService bodegaService) {
        this.bodegaService = bodegaService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Bodega bodega) {
        try {
            Bodega nuevaBodega = bodegaService.registrarBodega(bodega);
            return new ResponseEntity<>(nuevaBodega, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Bodega>> listar() {
        return new ResponseEntity<>(bodegaService.listarBodegas(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultar(@PathVariable Long id) {
        try {
            Bodega bodega = bodegaService.consultarBodega(id);
            return new ResponseEntity<>(bodega, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}