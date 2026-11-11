package mx.unam.fca.scvi.inventario.servicio;

import mx.unam.fca.scvi.inventario.modelo.Bodega;
import mx.unam.fca.scvi.inventario.repositorio.IBodegaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio principal para la gestión de reglas de negocio de Bodegas.
 *
 * @author Equipo 3
 */
@Service
public class BodegaService {

    private final IBodegaRepository bodegaRepository;

    @Autowired
    public BodegaService(IBodegaRepository bodegaRepository) {
        this.bodegaRepository = bodegaRepository;
    }

    /**
     * Valida y registra una nueva bodega en el sistema.
     *
     * @param bodega Objeto bodega con los datos de entrada.
     * @return La bodega persistida con su identificador único.
     * @throws IllegalArgumentException si los datos son inválidos o el nombre ya existe.
     */
    public Bodega registrarBodega(Bodega bodega) {
        if (bodega.getNombre() == null || bodega.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la bodega es un campo obligatorio.");
        }

        if (bodegaRepository.existsByNombre(bodega.getNombre())) {
            throw new IllegalArgumentException("Ya existe una bodega registrada con el nombre proporcionado.");
        }

        return bodegaRepository.save(bodega);
    }

    /**
     * Recupera el listado completo de bodegas registradas.
     */
    public List<Bodega> listarBodegas() {
        return bodegaRepository.findAll();
    }

    /**
     * Consulta una bodega específica mediante su identificador.
     */
    public Bodega consultarBodega(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna bodega con el identificador: " + id));
    }
}