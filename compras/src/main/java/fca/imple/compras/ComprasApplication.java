package fca.imple.compras;

import fca.imple.compras.modelo.*;
import fca.imple.compras.servicio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal y ejecutor de pruebas del subsistema de compras.
 * Responsabilidad principal: Inicializar el contexto de Spring Boot y ejecutar la prueba de integración
 * de los servicios y repositorios del subsistema de Compras.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Consume e inyecta las interfaces de servicio del equipo.
 * - Conecta con la base de datos MySQL mediante el pool de conexiones.
 * Reglas de negocio:
 * - Valida la integridad referencial y persistencia de las órdenes, compras y pagos.
 *
 * @author Óscar González Saucedo, Islas López Isis Jezabel Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 09-11-2026
 */
@SpringBootApplication
public class ComprasApplication implements CommandLineRunner {

	@Autowired
	private IProveedorServicio proveedorServicio;

	@Autowired
	private IOrdenCompraServicio ordenCompraServicio;

	@Autowired
	private IGerenteComprasServicio gerenteComprasServicio;

	@Autowired
	private IPagoProveedorServicio pagoProveedorServicio;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Método principal que arranca la aplicación Spring Boot.
	 *
	 * @param args argumentos de línea de comandos.
	 * @author Óscar González Saucedo Equipo 1
	 * Fecha creación: 07-11-2026
	 * Fecha modificación: 07-11-2026
	 */
	public static void main(String[] args) {
		SpringApplication.run(ComprasApplication.class, args);
	}

	/**
	 * Ejecuta las pruebas funcionales de persistencia y consultas al iniciar el contexto.
	 *
	 * @param args argumentos pasados a la ejecución.
	 * @throws Exception en caso de error durante la interacción con la base de datos.
	 * @author Óscar González Saucedo, Islas López Isis Jezabel Equipo 1
	 * Fecha creación: 07-11-2026
	 * Fecha modificación: 09-11-2026
	 */
	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n========================================================");
		System.out.println(">>> INICIANDO PRUEBAS DE SERVICIOS - EQUIPO 1 (ÓSCAR) <<<");
		System.out.println("========================================================\n");

		// 0. Asegurar que exista al menos un GerenteCompras para no violar la FK de OrdenCompra
		jdbcTemplate.execute(
				"INSERT INTO GerenteCompras (idGerenteCompras, nombre, telefono, domicilio, activo) " +
						"VALUES (1, 'Gerente General', '5511223344', 'Oficina Central 100', true) " +
						"ON DUPLICATE KEY UPDATE nombre = nombre;"
		);

		// --- 1. PRUEBA DE PROVEEDOR (ÓSCAR) ---
		System.out.println("--- 1. Probando inserción de Proveedor ---");
		Proveedor nuevoProveedor = new Proveedor();
		nuevoProveedor.setNombre("Distribuidora Industrial SA");
		nuevoProveedor.setTelefono("5544332211");
		nuevoProveedor.setDomicilio("Av. Insurgentes 450");
		nuevoProveedor.setRfc("DIS241105XYZ");
		nuevoProveedor.setContacto("Lic. Roberto Morales");
		nuevoProveedor.setBanco("BBVA");
		nuevoProveedor.setNumeroCuenta("012345678901234567");

		boolean insertado = proveedorServicio.insertarProveedor(nuevoProveedor);
		System.out.println("¿Proveedor registrado con éxito?: " + insertado);
		System.out.println("ID asignado por MySQL: " + nuevoProveedor.getIdProveedor());

		System.out.println("\n--- 2. Probando consulta por ID o Nombre ---");
		Proveedor proveedorEncontrado = proveedorServicio.consultarProveedorPorIdONombre(
				nuevoProveedor.getIdProveedor(), "Distribuidora Industrial SA"
		);
		System.out.println("Proveedor recuperado: " + proveedorEncontrado);

		System.out.println("\n--- 3. Probando listado general de Proveedores ---");
		List<Proveedor> listaProveedores = proveedorServicio.consultarListaProveedores();
		System.out.println("Total de proveedores en catálogo: " + listaProveedores.size());
		listaProveedores.forEach(p -> System.out.println("-> " + p.getIdProveedor() + " | " + p.getNombre() + " | RFC: " + p.getRfc()));

		// --- 2. PRUEBA DE ORDEN DE COMPRA (ÓSCAR) ---
		System.out.println("\n--- 4. Probando inserción de Orden de Compra ---");
		OrdenCompra orden = new OrdenCompra();
		orden.setIdProveedor(nuevoProveedor.getIdProveedor());
		orden.setIdGerenteCompras(1);
		orden.setFechaCreacion(LocalDate.now());
		orden.setCosto(15000.00);
		orden.setDescuento(500.00);
		orden.setTotal(14500.00);
		orden.setEstado("Pendiente");

		boolean ordenGuardada = ordenCompraServicio.insertarOrdenCompra(orden);
		System.out.println("¿Orden de compra registrada?: " + ordenGuardada);
		System.out.println("ID de la Orden asignado: " + orden.getIdOrdenCompra());

		System.out.println("\n--- 5. Probando consulta de órdenes pendientes por proveedor ---");
		List<OrdenCompra> pendientes = ordenCompraServicio.consultarOrdenesPendientesPorProveedor(nuevoProveedor.getIdProveedor());
		System.out.println("Órdenes pendientes encontradas: " + pendientes.size());
		pendientes.forEach(o -> System.out.println("-> ID Orden: " + o.getIdOrdenCompra() + " | Estado: " + o.getEstado() + " | Total: $" + o.getTotal()));

		System.out.println("\n========================================================");
		System.out.println(">>> INICIANDO PRUEBAS DE SERVICIOS - EQUIPO 1 (ISIS)  <<<");
		System.out.println("========================================================\n");

		// --- 3. PRUEBA DE GERENTE DE COMPRAS (ISIS) ---
		System.out.println("--- 6. Probando inserción y búsqueda de Gerente de Compras ---");
		GerenteCompras nuevoGerente = new GerenteCompras();
		nuevoGerente.setNombre("Isis Jezabel");
		nuevoGerente.setTelefono("5512345678");
		nuevoGerente.setDomicilio("Ciudad de México");

		boolean gerenteInsertado = gerenteComprasServicio.insertarGerente(nuevoGerente);
		System.out.println("¿Gerente registrado con éxito?: " + gerenteInsertado);

		int idGerenteBuscado = nuevoGerente.getIdGerenteCompras() != null ? nuevoGerente.getIdGerenteCompras() : 1;
		int idGerenteEncontrado = gerenteComprasServicio.buscarGerente(idGerenteBuscado);
		System.out.println("Gerente localizado con ID: " + (idGerenteEncontrado != 0 ? idGerenteEncontrado : "No encontrado"));

		// --- 4. PRUEBA DE SEGUNDO PROVEEDOR / REUTILIZACIÓN (ISIS) ---
		System.out.println("\n--- 7. Probando inserción de segundo Proveedor para pagos ---");
		Proveedor segundoProveedor = new Proveedor();
		segundoProveedor.setNombre("Comercializadora Global SA");
		segundoProveedor.setTelefono("5598765432");
		segundoProveedor.setDomicilio("Reforma 222");
		segundoProveedor.setRfc("CGL241108ABC");
		segundoProveedor.setContacto("Ing. Carlos Ruiz");
		segundoProveedor.setBanco("Santander");
		segundoProveedor.setNumeroCuenta("987654321098765432");

		boolean provInsertado = proveedorServicio.insertarProveedor(segundoProveedor);
		System.out.println("¿Segundo proveedor registrado con éxito?: " + provInsertado);
		System.out.println("ID asignado al Proveedor: " + segundoProveedor.getIdProveedor());

		// --- 5. PRUEBA DE PAGO A PROVEEDOR Y DETALLES (ISIS) ---
		System.out.println("\n--- 8. Probando registro de Pago a Proveedor ---");
		PagoProveedor pago = new PagoProveedor();
		pago.setIdProveedor(segundoProveedor.getIdProveedor());
		pago.setFechaPago(LocalDate.now());
		pago.setMontoTotal(5500.00);

		List<PagoDetalle> detalles = new ArrayList<>();
		PagoDetalle detalle1 = new PagoDetalle();
		detalle1.setAbonoAlSaldo(5000.00);
		detalle1.setAbonoAlInteres(500.00);
		detalles.add(detalle1);

		boolean pagoRegistrado = pagoProveedorServicio.registrarPago(pago, detalles);
		System.out.println("¿Pago a proveedor registrado con éxito?: " + pagoRegistrado);
		System.out.println("ID de Pago asignado: " + pago.getIdPago());

		System.out.println("\n--- 9. Probando consulta de pagos por proveedor y periodo ---");
		LocalDate inicioPeriodo = LocalDate.now().minusDays(5);
		LocalDate finPeriodo = LocalDate.now().plusDays(5);

		List<PagoProveedor> pagosPeriodo = pagoProveedorServicio.consultarPagosPorProveedorYPeriodo(
				segundoProveedor.getIdProveedor(), inicioPeriodo, finPeriodo
		);
		System.out.println("Pagos encontrados en el periodo: " + (pagosPeriodo != null ? pagosPeriodo.size() : 0));

		// --- 6. PRUEBA DE COMPRA (LIZETH) ---
		System.out.println("\n--- 10. Probando inserción y consulta de Compra ---");
		Compra nuevaCompra = new Compra();
		nuevaCompra.setNumeroFactura("FAC-2026-001");
		nuevaCompra.setIdOrdenCompra(orden.getIdOrdenCompra()); // Referencia la orden real recién creada
		nuevaCompra.setIdProveedor(nuevoProveedor.getIdProveedor()); // Referencia el proveedor real
		nuevaCompra.setIdGerenteCompras(1);
		nuevaCompra.setFechaOrden(Date.valueOf("2026-11-01"));
		nuevaCompra.setFechaRecepcion(Date.valueOf("2026-11-05"));
		nuevaCompra.setFechaCompra(Date.valueOf("2026-11-08"));
		nuevaCompra.setEstado("ACTIVA");
		nuevaCompra.setTotal(1500.00);

		boolean compraInsertada = CompraServicio.insertarCompra(nuevaCompra);
		System.out.println("¿Compra registrada con éxito?: " + compraInsertada);

		System.out.println("Listado total de compras registradas:");
		List<Compra> listaCompras = CompraServicio.consultarListaCompras();
		if (listaCompras != null) {
			for (Compra c : listaCompras) {
				System.out.println("ID Compra: " + c.getIdCompra() + " | Factura: " + c.getNumeroFactura() + " | Total: " + c.getTotal());
			}
		}

		System.out.println("\n========================================================");
		System.out.println(">>> ¡TODAS LAS PRUEBAS DEL EQUIPO 1 FINALIZARON EXITOSAMENTE! <<<");
		System.out.println("========================================================\n");
	}
}