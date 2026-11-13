#!/usr/bin/env bash
# subir-commits.sh — reproduce, paso a paso, el historial del subsistema de Ventas (SCVI).
# Cada paso es un commit pequeño con su autor (Diego, Roberto o Saul). Las pruebas van primero (red)
# y la implementación después (green).
#
# Forma más simple: una sola persona corre `./subir-commits.sh --push` y salen los 49 commits con cada autor.
#
# Uso (desde la raíz del repo clonado, con TODOS los archivos del proyecto ya copiados dentro):
#   ./subir-commits.sh --listar                      muestra los pasos, su autor y sus archivos
#   ./subir-commits.sh --persona Roberto --dry-run   simula los pasos de Roberto sin tocar git
#   ./subir-commits.sh --persona Diego               hace los commits de Diego (en tu máquina, con tu nombre)
#   ./subir-commits.sh --persona Saul --desde 20 --hasta 31 --push
#   ./subir-commits.sh --dry-run                     simula TODO el recorrido
#
# Los pasos dependen del orden (el esquema de una persona usa tablas de otra), por eso se recomienda ir por turnos:
# quien termina hace `--push`, el siguiente hace `git pull --rebase` y corre su rango.
#
# Turnos sugeridos (cada quien con su propia identidad de git y con --push al final):
#   1) Diego   --desde 1  --hasta 1      proyecto base
#   2) Saul    --desde 2  --hasta 2      excepciones y filtro
#   3) Roberto --desde 3  --hasta 3      utilidades comunes
#   4) Diego   --desde 4  --hasta 17     zonas, empleados, clientes, comisiones, reportes 1-6
#   5) Roberto --desde 18 --hasta 30     inventario, venta directa, caja, ticket
#   6) Saul    --desde 31 --hasta 43     pedidos, facturacion, cuentas por cobrar
#   7) Roberto --desde 44 --hasta 46     reportes de facturacion
#   8) Saul    --desde 47 --hasta 47     reportes 1-6 (servicio y API)
#   9) Roberto --desde 48 --hasta 48     README
#  10) Diego   --desde 49 --hasta 49     este script
#
# Opciones: --persona NOMBRE | --desde N | --hasta N | --dry-run | --push | --listar | --ayuda

set -euo pipefail

REMOTO="https://github.com/MarnueLgh/Negocio-IDS.git"
RAMA="main"

# --- Identidades de git de cada integrante. El correo debe ser el que está agregado en su cuenta de GitHub
#     para que el commit se les atribuya (Settings > Emails). El nombre puedes cambiarlo. ---
DIEGO_NOMBRE="Diego";     DIEGO_EMAIL="dipzon03@gmail.com"
ROBERTO_NOMBRE="Roberto"; ROBERTO_EMAIL="robertocarlosperezflores9@gmail.com"   # GitHub: robby77cpf
SAUL_NOMBRE="Saul";       SAUL_EMAIL="saul_alvarez_aguilar@comunidad.unam.mx"   # GitHub: saul-aalvrz

PERSONA=""; DESDE=1; HASTA=9999; DRY=0; PUSH=0; LISTAR=0
while [ $# -gt 0 ]; do
  case "$1" in
    --persona) PERSONA="$(printf '%s' "$2" | tr '[:upper:]' '[:lower:]')"; shift 2 ;;
    --desde) DESDE="$2"; shift 2 ;;
    --hasta) HASTA="$2"; shift 2 ;;
    --dry-run) DRY=1; shift ;;
    --push) PUSH=1; shift ;;
    --listar) LISTAR=1; shift ;;
    --ayuda|-h|--help) sed -n '2,34p' "$0"; exit 0 ;;
    *) echo "Opción desconocida: $1 (usa --ayuda)" >&2; exit 2 ;;
  esac
done
case "$PERSONA" in ""|diego|roberto|saul) ;; *) echo "--persona debe ser Diego, Roberto o Saul" >&2; exit 2 ;; esac

if [ "$LISTAR" -eq 0 ] && [ "$DRY" -eq 0 ]; then
  git rev-parse --is-inside-work-tree >/dev/null 2>&1 || { echo "Corre este script dentro del repo clonado (git clone $REMOTO)." >&2; exit 1; }
  if ! git remote get-url origin >/dev/null 2>&1; then git remote add origin "$REMOTO"; fi
fi

N=0; HECHOS=0; OMITIDOS=0
paso() {
  local autor="$1" msg="$2"; shift 2
  N=$((N + 1))
  local clave; clave="$(printf '%s' "$autor" | tr '[:upper:]' '[:lower:]')"
  if [ "$LISTAR" -eq 1 ]; then printf '%2d  %-8s %s\n' "$N" "$autor" "$msg"; return 0; fi
  [ "$N" -lt "$DESDE" ] && return 0
  [ "$N" -gt "$HASTA" ] && return 0
  [ -n "$PERSONA" ] && [ "$PERSONA" != "$clave" ] && return 0
  local f
  for f in "$@"; do
    [ -e "$f" ] || { echo "Paso $N: falta el archivo '$f'. ¿Copiaste todo el proyecto al repo?" >&2; exit 1; }
  done
  local nombre email
  case "$clave" in
    diego) nombre="$DIEGO_NOMBRE"; email="$DIEGO_EMAIL" ;;
    roberto) nombre="$ROBERTO_NOMBRE"; email="$ROBERTO_EMAIL" ;;
    saul) nombre="$SAUL_NOMBRE"; email="$SAUL_EMAIL" ;;
  esac
  if [ "$DRY" -eq 1 ]; then
    printf '[dry-run] %2d  %s <%s>: %s  (%d archivos)\n' "$N" "$nombre" "$email" "$msg" "$#"
    HECHOS=$((HECHOS + 1)); return 0
  fi
  git add -- "$@"
  if git diff --cached --quiet -- "$@"; then
    echo "Paso $N omitido (sin cambios): $msg"; OMITIDOS=$((OMITIDOS + 1)); return 0
  fi
  GIT_AUTHOR_NAME="$nombre" GIT_AUTHOR_EMAIL="$email" \
  GIT_COMMITTER_NAME="$nombre" GIT_COMMITTER_EMAIL="$email" \
    git -c commit.gpgsign=false commit --quiet -m "$msg" -- "$@"
  echo "Paso $N  $nombre: $msg"
  HECHOS=$((HECHOS + 1))
}

paso "Diego" "chore: proyecto Spring Boot (Maven, Java 21) con H2 en archivo y consola" \
     "pom.xml" \
     ".gitignore" \
     "src/main/java/mx/edu/scvi/ventas/VentasApplication.java" \
     "src/main/resources/application.properties"

paso "Saul" "feat(common): excepciones de negocio, manejador global de errores y filtro de trazabilidad" \
     "src/main/java/mx/edu/scvi/ventas/common/NoEncontradoException.java" \
     "src/main/java/mx/edu/scvi/ventas/common/ReglaNegocioException.java" \
     "src/main/java/mx/edu/scvi/ventas/common/GlobalExceptionHandler.java" \
     "src/main/java/mx/edu/scvi/ventas/common/TrazabilidadFilter.java"

paso "Roberto" "feat(common): utilidades de validación, dinero (IVA 16%) e inserción con llave generada" \
     "src/main/java/mx/edu/scvi/ventas/common/Reglas.java" \
     "src/main/java/mx/edu/scvi/ventas/common/Dinero.java" \
     "src/main/java/mx/edu/scvi/ventas/common/Consultas.java" \
     "src/test/java/mx/edu/scvi/ventas/PruebaIntegracion.java"

paso "Diego" "feat(bd): tablas zona, empleado, cliente y comision con datos semilla" \
     "src/main/resources/db/schema-01-zonas-vendedores-clientes.sql" \
     "src/main/resources/db/data-01-zonas-vendedores-clientes.sql"

paso "Diego" "feat(model): zona, empleado (vendedor/cajero), cliente y comision" \
     "src/main/java/mx/edu/scvi/ventas/model/Zona.java" \
     "src/main/java/mx/edu/scvi/ventas/model/TipoEmpleado.java" \
     "src/main/java/mx/edu/scvi/ventas/model/Empleado.java" \
     "src/main/java/mx/edu/scvi/ventas/model/Cliente.java" \
     "src/main/java/mx/edu/scvi/ventas/model/Comision.java"

paso "Diego" "test(zonas): datos de prueba y reglas de alta, modificación, unión y división de zonas (red)" \
     "src/test/java/mx/edu/scvi/ventas/PruebaDatos.java" \
     "src/test/java/mx/edu/scvi/ventas/service/ZonaServiceTest.java"

paso "Diego" "feat(zonas): ZonaDao y ZonaService (crear, modificar, eliminar, unir, dividir) (green)" \
     "src/main/java/mx/edu/scvi/ventas/dao/ZonaDao.java" \
     "src/main/java/mx/edu/scvi/ventas/service/ZonaService.java"

paso "Diego" "feat(zonas): API REST de zonas" \
     "src/main/java/mx/edu/scvi/ventas/controller/ZonaController.java"

paso "Diego" "test(empleados): alta y reglas de vendedores (una zona) y cajeros (sin zona) (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/EmpleadoServiceTest.java"

paso "Diego" "feat(empleados): EmpleadoDao, VendedorDao, VendedorCajaDao y EmpleadoService (green)" \
     "src/main/java/mx/edu/scvi/ventas/dao/EmpleadoDao.java" \
     "src/main/java/mx/edu/scvi/ventas/dao/VendedorDao.java" \
     "src/main/java/mx/edu/scvi/ventas/dao/VendedorCajaDao.java" \
     "src/main/java/mx/edu/scvi/ventas/service/EmpleadoService.java"

paso "Diego" "feat(empleados): API REST de vendedores y cajeros" \
     "src/main/java/mx/edu/scvi/ventas/controller/EmpleadoRequest.java" \
     "src/main/java/mx/edu/scvi/ventas/controller/VendedorController.java" \
     "src/main/java/mx/edu/scvi/ventas/controller/CajeroController.java"

paso "Diego" "test(clientes): validación de RFC, crédito y zona del cliente (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/ClienteServiceTest.java"

paso "Diego" "feat(clientes): ClienteDao y ClienteService (green)" \
     "src/main/java/mx/edu/scvi/ventas/dao/ClienteDao.java" \
     "src/main/java/mx/edu/scvi/ventas/service/ClienteService.java"

paso "Diego" "feat(clientes): API REST de clientes" \
     "src/main/java/mx/edu/scvi/ventas/controller/ClienteController.java"

paso "Diego" "test(comisiones): generación y cancelación de comisiones por venta y por factura (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/ComisionServiceTest.java"

paso "Diego" "feat(comisiones): ComisionDao y ComisionService (green)" \
     "src/main/java/mx/edu/scvi/ventas/dao/ComisionDao.java" \
     "src/main/java/mx/edu/scvi/ventas/service/ComisionService.java"

paso "Diego" "feat(reportes): consultas ReporteVendedorDao para los reportes 1 a 6" \
     "src/main/java/mx/edu/scvi/ventas/dao/ReporteVendedorDao.java"

paso "Roberto" "feat(bd): tablas de artículo de referencia, corte de caja, venta, pago y ticket con artículos demo" \
     "src/main/resources/db/schema-02-ventas-directas-caja.sql" \
     "src/main/resources/db/data-02-articulos-demo.sql"

paso "Roberto" "feat(integracion): InventarioGateway síncrono con implementación local sobre articulo_ref" \
     "src/main/java/mx/edu/scvi/ventas/integracion/ArticuloRef.java" \
     "src/main/java/mx/edu/scvi/ventas/integracion/InventarioGateway.java" \
     "src/main/java/mx/edu/scvi/ventas/integracion/InventarioLocalGateway.java"

paso "Roberto" "feat(model): venta, detalle, pago, ticket y corte de caja" \
     "src/main/java/mx/edu/scvi/ventas/model/FormaPago.java" \
     "src/main/java/mx/edu/scvi/ventas/model/EstadoVenta.java" \
     "src/main/java/mx/edu/scvi/ventas/model/EstadoCorte.java" \
     "src/main/java/mx/edu/scvi/ventas/model/Venta.java" \
     "src/main/java/mx/edu/scvi/ventas/model/VentaDetalle.java" \
     "src/main/java/mx/edu/scvi/ventas/model/Pago.java" \
     "src/main/java/mx/edu/scvi/ventas/model/Ticket.java" \
     "src/main/java/mx/edu/scvi/ventas/model/CorteCaja.java"

paso "Roberto" "feat(evento): eventos en proceso VentaDirectaRegistrada y VentaDirectaCancelada" \
     "src/main/java/mx/edu/scvi/ventas/evento/VentaDirectaRegistrada.java" \
     "src/main/java/mx/edu/scvi/ventas/evento/VentaDirectaCancelada.java"

paso "Roberto" "test(ticket): formato del ticket de venta y cálculo de cambio (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/TicketServiceTest.java"

paso "Roberto" "feat(ticket): TicketService y TicketDao (green)" \
     "src/main/java/mx/edu/scvi/ventas/service/TicketService.java" \
     "src/main/java/mx/edu/scvi/ventas/dao/TicketDao.java"

paso "Roberto" "feat(ventas): VentaDao y PagoDao" \
     "src/main/java/mx/edu/scvi/ventas/dao/VentaDao.java" \
     "src/main/java/mx/edu/scvi/ventas/dao/PagoDao.java"

paso "Roberto" "test(caja): apertura y corte de caja con diferencias (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/CorteCajaServiceTest.java"

paso "Roberto" "feat(caja): CorteCajaDao y CorteCajaService (green)" \
     "src/main/java/mx/edu/scvi/ventas/dao/CorteCajaDao.java" \
     "src/main/java/mx/edu/scvi/ventas/service/CorteCajaService.java"

paso "Roberto" "feat(caja): API REST de cortes de caja y reportes 7 y 8" \
     "src/main/java/mx/edu/scvi/ventas/controller/CorteCajaController.java"

paso "Roberto" "test(ventas): venta directa, stock, cancelación y comisión del cajero (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/VentaDirectaServiceTest.java"

paso "Roberto" "feat(ventas): VentaDirectaService con descuento de inventario y eventos (green)" \
     "src/main/java/mx/edu/scvi/ventas/service/VentaDirectaService.java"

paso "Roberto" "feat(ventas): API REST de ventas directas" \
     "src/main/java/mx/edu/scvi/ventas/controller/VentaDirectaController.java"

paso "Saul" "feat(bd): tablas de pedido, factura, cuenta por cobrar y pagos de cuenta" \
     "src/main/resources/db/schema-03-pedidos-facturacion-cxc.sql"

paso "Saul" "feat(model): pedido, factura y cuenta por cobrar con sus estados" \
     "src/main/java/mx/edu/scvi/ventas/model/EstadoPedido.java" \
     "src/main/java/mx/edu/scvi/ventas/model/Pedido.java" \
     "src/main/java/mx/edu/scvi/ventas/model/DetallePedido.java" \
     "src/main/java/mx/edu/scvi/ventas/model/EstadoFactura.java" \
     "src/main/java/mx/edu/scvi/ventas/model/Factura.java" \
     "src/main/java/mx/edu/scvi/ventas/model/FacturaDetalle.java" \
     "src/main/java/mx/edu/scvi/ventas/model/EstadoCuenta.java" \
     "src/main/java/mx/edu/scvi/ventas/model/CuentaPorCobrar.java" \
     "src/main/java/mx/edu/scvi/ventas/model/PagoCuentaPorCobrar.java"

paso "Saul" "test(pedidos): zona, crédito, promesa de entrega y entrega parcial (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/PedidoServiceTest.java"

paso "Saul" "feat(pedidos): PedidoDao y CuentaPorCobrarDao" \
     "src/main/java/mx/edu/scvi/ventas/dao/PedidoDao.java" \
     "src/main/java/mx/edu/scvi/ventas/dao/CuentaPorCobrarDao.java"

paso "Saul" "feat(pedidos): PedidoService (green)" \
     "src/main/java/mx/edu/scvi/ventas/service/PedidoService.java"

paso "Saul" "feat(pedidos): API REST de pedidos" \
     "src/main/java/mx/edu/scvi/ventas/controller/PedidoController.java"

paso "Saul" "test(facturacion): factura de pedido y de venta directa, cancelar y sustituir (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/FacturacionServiceTest.java"

paso "Saul" "feat(facturacion): FacturaDao y FacturacionService (green)" \
     "src/main/java/mx/edu/scvi/ventas/dao/FacturaDao.java" \
     "src/main/java/mx/edu/scvi/ventas/service/FacturacionService.java"

paso "Saul" "feat(facturacion): listener de eventos de venta directa" \
     "src/main/java/mx/edu/scvi/ventas/service/FacturaVentaListener.java"

paso "Saul" "feat(facturacion): API REST de facturas y reporte 12" \
     "src/main/java/mx/edu/scvi/ventas/controller/FacturaController.java"

paso "Saul" "test(cxc): abonos, liquidación, cuentas vencidas (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/CuentaPorCobrarServiceTest.java"

paso "Saul" "feat(cxc): CuentaPorCobrarService con ticket de pago (green)" \
     "src/main/java/mx/edu/scvi/ventas/service/CuentaPorCobrarService.java"

paso "Saul" "feat(cxc): API REST de cuentas por cobrar" \
     "src/main/java/mx/edu/scvi/ventas/controller/CuentaPorCobrarController.java"

paso "Roberto" "test(reportes): pedidos pendientes y facturación por cliente, vendedor, producto y zona (red)" \
     "src/test/java/mx/edu/scvi/ventas/service/ReporteFacturacionServiceTest.java"

paso "Roberto" "feat(reportes): ReporteFacturacionDao y ReporteFacturacionService (green)" \
     "src/main/java/mx/edu/scvi/ventas/dao/ReporteFacturacionDao.java" \
     "src/main/java/mx/edu/scvi/ventas/service/ReporteFacturacionService.java"

paso "Roberto" "feat(reportes): API REST de reportes 9 a 20" \
     "src/main/java/mx/edu/scvi/ventas/controller/ReporteFacturacionController.java"

paso "Saul" "feat(reportes): ReporteVendedorService y API de reportes 1 a 6 de vendedores, clientes y zonas" \
     "src/main/java/mx/edu/scvi/ventas/service/ReporteVendedorService.java" \
     "src/main/java/mx/edu/scvi/ventas/controller/ReporteVendedorController.java"

paso "Roberto" "docs: README con arranque, consola H2, endpoints, supuestos y reparto del equipo" \
     "README.md" \
     "docs/historias-por-desarrollador.md"

paso "Diego" "chore: script subir-commits.sh para reproducir el historial por pasos" \
     "subir-commits.sh"

[ "$LISTAR" -eq 1 ] && exit 0
echo "Listo: $HECHOS commit(s), $OMITIDOS omitido(s)."
if [ "$PUSH" -eq 1 ] && [ "$DRY" -eq 0 ]; then
  git push origin "HEAD:$RAMA"
elif [ "$DRY" -eq 0 ]; then
  echo "No se hizo push. Cuando quieras: git push origin HEAD:$RAMA   (o vuelve a correr con --push)"
fi
