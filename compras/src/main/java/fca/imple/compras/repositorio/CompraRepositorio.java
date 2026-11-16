package fca.imple.compras.repositorio;

import fca.imple.compras.modelo.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 - Interfaz de repositorio de acceso a datos para la entidad Compra.
 - Propósito: Gestionar las operaciones de persistencia, consulta y filtrado avanzado de las compras registradas en la base de datos.
 - Responsabilidad principal: Proveer métodos de acceso a datos basados en Spring Data JPA para administrar el historial de adquisiciones, rangos de fechas de compra, filtrado por proveedor y estados operativos.
 - Subsistema: Compras
 - Relación con otros componentes: Se relaciona directamente con la entidad Compra y es utilizada e inyectada en los servicios correspondientes del subsistema.
 - Reglas de negocio: Facilita la recuperación de transacciones de compra mediante intervalos de fechas, identificación de proveedores y control de estados de ejecución o facturación.
 - @author Becerra Cruz Lizeth Adriana Equipo 1
 - Fecha creación: 08-11-2026
 - Fecha modificación: 09-11-2026
 */

@Repository
public interface CompraRepositorio extends JpaRepository<Compra, Integer> {


    List<Compra> findByFechaCompraBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<Compra> findByIdProveedor(Integer idProveedor);

    List<Compra> findByEstado(String estado);

    List<Compra> findByIdProveedorAndFechaCompraBetween(Integer idProveedor, LocalDate fechaInicio, LocalDate fechaFin);

}