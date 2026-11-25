package mx.unam.fca.scvi.inventario.controlador;

import mx.unam.fca.scvi.inventario.modelo.Almacen;
import mx.unam.fca.scvi.inventario.servicio.AlmacenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de almacenes.
 * Responsabilidad principal: Exponer los endpoints para registrar
 * y listar los almacenes del sistema de inventario.
 *
 * @author Equipo 3
 *
 * Fecha creación: 07-11-2026
 * Fecha modificación: 07-11-2026
 */

@RestController
@RequestMapping("/api/almacenes")
public class AlmacenController {

    private final AlmacenService almacenService;

    @Autowired
    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }

    /**
     * Endpoint para registrar un nuevo almacén.
     *
     * @param almacen datos del almacén recibidos en el cuerpo de la petición.
     * @return respuesta HTTP 201 con el almacén creado o HTTP 400 en caso de error.
     */
    @PostMapping
    public ResponseEntity<?> registrarAlmacen(@RequestBody Almacen almacen) {
        try {
            Almacen nuevoAlmacen = almacenService.registrarAlmacen(almacen);
            return new ResponseEntity<>(nuevoAlmacen, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint para listar todos los almacenes registrados.
     *
     * @return respuesta HTTP 200 con la lista de almacenes.
     */
    @GetMapping
    public ResponseEntity<List<Almacen>> listarAlmacenes() {
        List<Almacen> almacenes = almacenService.listarAlmacenes();
        return new ResponseEntity<>(almacenes, HttpStatus.OK);
    }
}