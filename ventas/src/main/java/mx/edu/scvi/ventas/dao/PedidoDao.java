package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mx.edu.scvi.ventas.model.DetallePedido;
import mx.edu.scvi.ventas.model.EstadoPedido;
import mx.edu.scvi.ventas.model.Pedido;
import org.springframework.stereotype.Repository;

@Repository
public class PedidoDao {

    @PersistenceContext
    private EntityManager em;

    public Pedido insertarPedido(Pedido p) {
        em.persist(p);
        em.flush();
        return p;
    }

    public void insertarDetalle(DetallePedido d) {
        em.persist(d);
        em.flush();
    }

    public Optional<Pedido> getPedido(long pedidoId) {
        return Optional.ofNullable(em.find(Pedido.class, pedidoId));
    }

    public List<DetallePedido> detalles(long pedidoId) {
        return em.createQuery("SELECT d FROM DetallePedido d WHERE d.pedidoId = :pedido ORDER BY d.detallePedidoId",
                        DetallePedido.class)
                .setParameter("pedido", pedidoId)
                .getResultList();
    }

    public int actualizarEstado(long pedidoId, EstadoPedido estado) {
        Pedido pedido = em.find(Pedido.class, pedidoId);
        if (pedido == null) {
            return 0;
        }
        pedido.setEstado(estado);
        em.flush();
        return 1;
    }

    public int actualizarPendiente(long detalleId, int cantidadPendiente) {
        DetallePedido detalle = em.find(DetallePedido.class, detalleId);
        if (detalle == null) {
            return 0;
        }
        detalle.setCantidadPendiente(cantidadPendiente);
        em.flush();
        return 1;
    }

    /** Reporte 9: pedidos pendientes en un periodo (por fecha del pedido). */
    public List<Pedido> pendientesPorPeriodo(LocalDate desde, LocalDate hasta) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.estado = :estado AND p.fechaPedido BETWEEN :desde "
                        + "AND :hasta ORDER BY p.fechaEntrega, p.pedidoId", Pedido.class)
                .setParameter("estado", EstadoPedido.PENDIENTE)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }

    /** Reporte 10: pedidos pendientes de un cliente. */
    public List<Pedido> pendientesDeCliente(long clienteId) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.estado = :estado AND p.clienteId = :cliente "
                        + "ORDER BY p.fechaEntrega, p.pedidoId", Pedido.class)
                .setParameter("estado", EstadoPedido.PENDIENTE)
                .setParameter("cliente", clienteId)
                .getResultList();
    }

    /** Reporte 11: pedidos pendientes de un vendedor. */
    public List<Pedido> pendientesDeVendedor(long vendedorId) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.estado = :estado AND p.vendedorId = :vendedor "
                        + "ORDER BY p.fechaEntrega, p.pedidoId", Pedido.class)
                .setParameter("estado", EstadoPedido.PENDIENTE)
                .setParameter("vendedor", vendedorId)
                .getResultList();
    }
}
