package mx.unam.fca.scvi.inventario.servicio;

import mx.unam.fca.scvi.inventario.modelo.Almacen;
import mx.unam.fca.scvi.inventario.repositorio.IAlmacenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Servicio que contiene la lógica de negocio para la gestión de almacenes.
 * Responsabilidad principal: Validar reglas de negocio y coordinar
 * el registro y consulta de almacenes.
 * Pertenece al subsistema de Inventario.
 *
 * @author Equipo 3
 *
 * Fecha creación: 07-11-2026
 * Fecha modificación: 07-11-2026
 */
@Service
public class AlmacenService {

    private final IAlmacenRepository almacenRepository;

    @Autowired
    public AlmacenService(IAlmacenRepository almacenRepository) {
        this.almacenRepository = almacenRepository;
    }

    /**
     * Registra un nuevo almacén en el sistema validando sus datos.
     * Aplica la regla de negocio para evitar nombres duplicados.
     * Modifica el estado inicial del almacén a activo por defecto.
     *
     * @param almacen entidad con los datos del almacén a registrar.
     * @return el almacén guardado con su identificador generado.
     * @throws IllegalArgumentException si los datos son inválidos o el nombre ya existe.
     */
    @Transactional
    public Almacen registrarAlmacen(Almacen almacen) {
        validarAlmacen(almacen);

        if (almacenRepository.existsByNombre(almacen.getNombre())) {
            throw new IllegalArgumentException("El nombre del almacen ya se encuentra registrado.");
        }

        almacen.setActivo(true);
        return almacenRepository.save(almacen);
    }

    /**
     * Lista todos los almacenes registrados en la base de datos.
     *
     * @return lista de almacenes encontrados.
     */
    @Transactional(readOnly = true)
    public List<Almacen> listarAlmacenes() {
        return almacenRepository.findAll();
    }

    /**
     * Valida que los datos obligatorios del almacén estén presentes y correctos.
     *
     * @param almacen el almacén que se desea validar.
     * @throws IllegalArgumentException si falta información obligatoria.
     */
    private void validarAlmacen(Almacen almacen) {
        if (almacen == null) {
            throw new IllegalArgumentException("La informacion del almacen es obligatoria.");
        }
        if (almacen.getNombre() == null || almacen.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del almacen es obligatorio.");
        }
        if (almacen.getDireccion() == null || almacen.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La direccion del almacen es obligatoria.");
        }
    }
}