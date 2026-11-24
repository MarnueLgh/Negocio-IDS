-- Ventas: pedidos (venta no directa), facturación y cuentas por cobrar.
-- Ejecutar después de 05_crear_ventas_directas_caja.sql.
-- Autor: Álvarez Aguilar Saúl Antonio, Equipo 2. Fecha: 23-11-2026.

CREATE TABLE Pedido (
    pedidoId     BIGINT        NOT NULL AUTO_INCREMENT,
    fechaPedido  DATE          NOT NULL,
    fechaEntrega DATE          NOT NULL,
    clienteId    BIGINT        NOT NULL,
    vendedorId   BIGINT        NOT NULL,
    diasCredito  INT           NOT NULL DEFAULT 0,
    subtotal     DECIMAL(12,2) NOT NULL,
    iva          DECIMAL(12,2) NOT NULL,
    total        DECIMAL(12,2) NOT NULL,
    estado       VARCHAR(12)   NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT pkPedido PRIMARY KEY (pedidoId),
    CONSTRAINT fkPedidoCliente FOREIGN KEY (clienteId) REFERENCES Cliente (clienteId),
    CONSTRAINT fkPedidoEmpleado FOREIGN KEY (vendedorId) REFERENCES Empleado (empleadoId),
    CONSTRAINT ckPedidoEstado CHECK (estado IN ('PENDIENTE', 'ENTREGADO', 'FACTURADO', 'CANCELADO'))
);

CREATE TABLE DetallePedido (
    detallePedidoId   BIGINT        NOT NULL AUTO_INCREMENT,
    pedidoId          BIGINT        NOT NULL,
    articuloId        BIGINT        NOT NULL,
    codigo            VARCHAR(50)   NOT NULL,
    descripcion       VARCHAR(120)  NOT NULL,
    cantidadPedida    INT           NOT NULL,
    cantidadPendiente INT           NOT NULL,
    precio            DECIMAL(12,2) NOT NULL,
    importe           DECIMAL(12,2) NOT NULL,
    promesa           BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pkDetallePedido PRIMARY KEY (detallePedidoId),
    CONSTRAINT fkDetallePedidoPedido FOREIGN KEY (pedidoId) REFERENCES Pedido (pedidoId),
    CONSTRAINT fkDetallePedidoArticulo FOREIGN KEY (articuloId) REFERENCES Articulo (articuloId),
    CONSTRAINT ckDetallePedidoCantidades CHECK (cantidadPedida > 0 AND cantidadPendiente >= 0
        AND cantidadPendiente <= cantidadPedida)
);

-- Una factura nace de una venta directa o de un pedido, nunca de ambos.
CREATE TABLE Factura (
    facturaId          BIGINT        NOT NULL AUTO_INCREMENT,
    fechaFactura       DATE          NOT NULL,
    diasCredito        INT           NOT NULL DEFAULT 0,
    fechaLimitePago    DATE          NOT NULL,
    subtotal           DECIMAL(12,2) NOT NULL,
    iva                DECIMAL(12,2) NOT NULL,
    total              DECIMAL(12,2) NOT NULL,
    estado             VARCHAR(10)   NOT NULL DEFAULT 'EMITIDA',
    ventaId            BIGINT        NULL,
    pedidoId           BIGINT        NULL,
    clienteId          BIGINT        NOT NULL,
    vendedorId         BIGINT        NOT NULL,
    sustituyeFacturaId BIGINT        NULL,
    CONSTRAINT pkFactura PRIMARY KEY (facturaId),
    CONSTRAINT fkFacturaVenta FOREIGN KEY (ventaId) REFERENCES Venta (ventaId),
    CONSTRAINT fkFacturaPedido FOREIGN KEY (pedidoId) REFERENCES Pedido (pedidoId),
    CONSTRAINT fkFacturaCliente FOREIGN KEY (clienteId) REFERENCES Cliente (clienteId),
    CONSTRAINT fkFacturaEmpleado FOREIGN KEY (vendedorId) REFERENCES Empleado (empleadoId),
    CONSTRAINT fkFacturaFactura FOREIGN KEY (sustituyeFacturaId) REFERENCES Factura (facturaId),
    CONSTRAINT ckFacturaEstado CHECK (estado IN ('EMITIDA', 'CANCELADA')),
    CONSTRAINT ckFacturaOrigen CHECK ((ventaId IS NOT NULL AND pedidoId IS NULL) OR (ventaId IS NULL AND pedidoId IS NOT NULL))
);

CREATE TABLE FacturaDetalle (
    facturaDetalleId BIGINT        NOT NULL AUTO_INCREMENT,
    facturaId        BIGINT        NOT NULL,
    articuloId       BIGINT        NOT NULL,
    codigo           VARCHAR(50)   NOT NULL,
    descripcion      VARCHAR(120)  NOT NULL,
    cantidad         INT           NOT NULL,
    precio           DECIMAL(12,2) NOT NULL,
    importe          DECIMAL(12,2) NOT NULL,
    CONSTRAINT pkFacturaDetalle PRIMARY KEY (facturaDetalleId),
    CONSTRAINT fkFacturaDetalleFactura FOREIGN KEY (facturaId) REFERENCES Factura (facturaId),
    CONSTRAINT fkFacturaDetalleArticulo FOREIGN KEY (articuloId) REFERENCES Articulo (articuloId)
);

CREATE TABLE CuentaPorCobrar (
    cuentaId         BIGINT        NOT NULL AUTO_INCREMENT,
    fechaCuenta      DATE          NOT NULL,
    fechaFacturacion DATE          NOT NULL,
    fechaLimite      DATE          NOT NULL,
    estado           VARCHAR(10)   NOT NULL DEFAULT 'PENDIENTE',
    importeOriginal  DECIMAL(12,2) NOT NULL,
    importePendiente DECIMAL(12,2) NOT NULL,
    facturaId        BIGINT        NOT NULL,
    clienteId        BIGINT        NOT NULL,
    CONSTRAINT pkCuentaPorCobrar PRIMARY KEY (cuentaId),
    CONSTRAINT uqCuentaPorCobrarFacturaId UNIQUE (facturaId),
    CONSTRAINT fkCuentaPorCobrarFactura FOREIGN KEY (facturaId) REFERENCES Factura (facturaId),
    CONSTRAINT fkCuentaPorCobrarCliente FOREIGN KEY (clienteId) REFERENCES Cliente (clienteId),
    CONSTRAINT ckCuentaPorCobrarEstado CHECK (estado IN ('PENDIENTE', 'PARCIAL', 'LIQUIDADA', 'CANCELADA')),
    CONSTRAINT ckCuentaPorCobrarImportes CHECK (importePendiente >= 0 AND importePendiente <= importeOriginal)
);

CREATE TABLE PagoCuentaPorCobrar (
    pagoCuentaId BIGINT        NOT NULL AUTO_INCREMENT,
    cuentaId     BIGINT        NOT NULL,
    fechaPago    DATE          NOT NULL,
    importe      DECIMAL(12,2) NOT NULL,
    formaPago    VARCHAR(15)   NOT NULL,
    CONSTRAINT pkPagoCuentaPorCobrar PRIMARY KEY (pagoCuentaId),
    CONSTRAINT fkPagoCuentaPorCobrarCuentaPorCobrar FOREIGN KEY (cuentaId) REFERENCES CuentaPorCobrar (cuentaId),
    CONSTRAINT ckPagoCuentaPorCobrarImporte CHECK (importe > 0)
);
