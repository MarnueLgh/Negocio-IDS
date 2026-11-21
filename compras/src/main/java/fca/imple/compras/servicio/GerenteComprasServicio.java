package fca.imple.compras.servicio;

import fca.imple.compras.modelo.GerenteCompras;
import fca.imple.compras.repositorio.GerenteComprasRepositorio;
import fca.imple.compras.repositorio.ProveedorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Clase de servicio que implementa la lógica de negocio y operaciones de persistencia para Gerente Compras.
 * Propósito: Gestionar el ciclo de vida y la  busqueda del gerente de compras implementando Spring Data JPA.
 * Responsabilidad principal: Servir como intermediario entre la interfaz de usuario y el repositorio de acceso a datos.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Implementa la interfaz IGerenteComprasServicio.
 * - Inyecta y delega operaciones en GerenteRepositorio.
 * Reglas de negocio:
 * - Valida la existencia del registro antes de ejecutar actualizaciones o bajas.
 *
 * @author Islas López Isis Jezabel Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 08-11-2026
 */

@Service
public class GerenteComprasServicio implements IGerenteComprasServicio{

    @Autowired
    private GerenteComprasRepositorio gerenteComprasRepositorio;

    /**
     * Inserta a un gerente de compras.
     * Información de Modificación: Ninguna, sólo operación de inserción.
     * Información de Tablas, Entidades o Componentes: Utiliza el repositorio para persistir la entidad GerenteCompras en la base de datos.
     * Reglas de Negocio: Permite insertar un nuevo Gerente de Compras validando que el objeto no sea nulo.
     *
     * @param gerenteCompras objeto de tipo GerenteCompras a registrar.
     * @return true sí la inserción se realizó con éxito; false en caso contrario.
     * @author Islas López Isis Jezabel Equipo1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean insertarGerente(GerenteCompras gerenteCompras){

        if (gerenteCompras == null) {

            return false;

        }

        gerenteComprasRepositorio.save(gerenteCompras);

        return true;

    }

    /**
     * Borra los datos de un Gerente de Compras
     * Información de Modificación: Ninguna, sólo operación de eliminación.
     * Información de Tablas, Entidades o Componentes: Verifica y elimina el registro en la tabla de gerentes a través del repositorio.
     * Reglas de Negocio: Valida de manera previa la existencia del registro en la base de datos antes de proceder con la baja.
     *
     * @param idGerenteCompras identificador único de un Gerente de Compras.
     * @return true si la eliminación se realizó con éxito; false si el registro no existe.
     * @author Islas López Isis Jezabel Equipo1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean borrarGerente(int idGerenteCompras){

        if (gerenteComprasRepositorio.existsById(idGerenteCompras)) {

            gerenteComprasRepositorio.deleteById(idGerenteCompras);

            return true;

        }

        return false;

    }

    /**
     * Modifica los datos almacenados de un Gerente de Compras existente.
     * Información de Modificación: Ninguna, sólo operación de actualización.
     * Información de Tablas, Entidades o Componentes: Actualiza los campos de la entidad en la base de datos mediante el repositorio.
     * Reglas de Negocio: Valida que el objeto y su identificador no sean nulos, asegurando que el registro exista previamente.
     *
     * @param gerenteCompras Objeto GerenteCompras con los datos actualizados.
     * @return true si la modificación fue exitosa; false en caso contrario.
     * @author Islas López Isis Jezabel Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public boolean modificarGerente(GerenteCompras gerenteCompras){

        if (gerenteCompras == null || gerenteCompras.getIdGerenteCompras() == null){

            return false;

        }

        if (gerenteComprasRepositorio.existsById(gerenteCompras.getIdGerenteCompras())) {

            gerenteComprasRepositorio.save(gerenteCompras);

            return true;

        }

        return false;

    }

    /**
     * Busca a un Gerente de Compras a partir de su id.
     * Información de Modificación: Ninguna, sólo operación de búsqueda.
     * Información de Tablas, Entidades o Componentes: Consulta la entidad y tabla de gerentes en la base de datos mediante el repositorio.
     * Reglas de Negocio: Retorna el objeto GerenteCompras si el registro existe; de lo contrario, retorna null.
     *
     * @param idGerenteCompras Identificador único del gerente a consultar.
     * @return El objeto GerenteCompras encontrado, o null si no existe el registro.
     * @author Islas López Isis Jezabel Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Override
    public int buscarGerente(int idGerenteCompras) {

        GerenteCompras gerente = gerenteComprasRepositorio.findById(idGerenteCompras).orElse(null);

        return gerente != null ? gerente.getIdGerenteCompras() : 0;

    }

}