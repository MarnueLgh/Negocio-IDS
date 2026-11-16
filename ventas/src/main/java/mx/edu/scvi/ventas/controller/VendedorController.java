package mx.edu.scvi.ventas.controller;

import java.util.List;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.TipoEmpleado;
import mx.edu.scvi.ventas.service.EmpleadoService;
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
@RequestMapping("/api/vendedores")
public class VendedorController {

    private final EmpleadoService empleadoService;

    public VendedorController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public List<Empleado> listar() {
        return empleadoService.listar(TipoEmpleado.VENDEDOR);
    }

    @GetMapping("/{id}")
    public Empleado obtener(@PathVariable("id") long id) {
        return empleadoService.obtener(TipoEmpleado.VENDEDOR, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empleado registrar(@RequestBody EmpleadoRequest request) {
        return empleadoService.registrar(TipoEmpleado.VENDEDOR, request.aEmpleado(TipoEmpleado.VENDEDOR));
    }

    @PutMapping("/{id}")
    public Empleado modificar(@PathVariable("id") long id, @RequestBody EmpleadoRequest request) {
        return empleadoService.modificar(TipoEmpleado.VENDEDOR, id, request.aEmpleado(TipoEmpleado.VENDEDOR),
                request.activo());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void baja(@PathVariable("id") long id) {
        empleadoService.baja(TipoEmpleado.VENDEDOR, id);
    }
}
