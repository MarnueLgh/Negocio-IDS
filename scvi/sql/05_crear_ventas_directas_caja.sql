-- Ventas: corte de caja, ventas directas, su detalle, pago y ticket.
-- Los artículos son los de la tabla Articulo de Inventario (01_crear_articulo.sql).
-- Ejecutar después de 04_crear_ventas_zonas_empleados_clientes.sql.
-- Autor: Álvarez Aguilar Saúl Antonio, Equipo 2. Fecha: 23-11-2026.

CREATE TABLE CorteCaja (
    corteCajaId     BIGINT        NOT NULL AUTO_INCREMENT,
    cajaId          INT           NOT NULL,
    cajeroId        BIGINT        NOT NULL,
    fechaApertura   DATETIME      NOT NULL,
    horaCorte       DATETIME      NULL,
    importeInicial  DECIMAL(12,2) NOT NULL,
    totalVentas     INT           NOT NULL DEFAULT 0, -- Número de ventas del corte, no su importe.
    importeEsperado DECIMAL(12,2) NULL,
    importeFisico   DECIMAL(12,2) NULL,
    diferencia      DECIMAL(12,2) NULL,
    hayDiferencia   BOOLEAN       NOT NULL DEFAULT FALSE,
    estado          VARCHAR(10)   NOT NULL DEFAULT 'ABIERTA',
    CONSTRAINT pkCorteCaja PRIMARY KEY (corteCajaId),
    CONSTRAINT fkCorteCajaEmpleado FOREIGN KEY (cajeroId) REFERENCES Empleado (empleadoId),
    CONSTRAINT ckCorteCajaEstado CHECK (estado IN ('ABIERTA', 'CERRADA'))
);

CREATE TABLE Venta (
    ventaId         BIGINT        NOT NULL AUTO_INCREMENT,
    fechaVenta      DATETIME      NOT NULL,
    cajeroId        BIGINT        NOT NULL,
    corteCajaId     BIGINT        NOT NULL,
    clienteId       BIGINT        NULL,
    solicitaFactura BOOLEAN       NOT NULL DEFAULT FALSE,
    subtotal        DECIMAL(12,2) NOT NULL,
    iva             DECIMAL(12,2) NOT NULL,
    total           DECIMAL(12,2) NOT NULL,
    estado          VARCHAR(12)   NOT NULL DEFAULT 'COMPLETADA',
    CONSTRAINT pkVenta PRIMARY KEY (ventaId),
    CONSTRAINT fkVentaEmpleado FOREIGN KEY (cajeroId) REFERENCES Empleado (empleadoId),
    CONSTRAINT fkVentaCorteCaja FOREIGN KEY (corteCajaId) REFERENCES CorteCaja (corteCajaId),
    CONSTRAINT fkVentaCliente FOREIGN KEY (clienteId) REFERENCES Cliente (clienteId),
    CONSTRAINT ckVentaEstado CHECK (estado IN ('COMPLETADA', 'CANCELADA'))
);

-- codigo, descripcion y precio se copian del artículo al vender para conservar el historial.
CREATE TABLE VentaDetalle (
    ventaDetalleId BIGINT        NOT NULL AUTO_INCREMENT,
    ventaId        BIGINT        NOT NULL,
    articuloId     BIGINT        NOT NULL,
    codigo         VARCHAR(50)   NOT NULL,
    descripcion    VARCHAR(120)  NOT NULL,
    cantidad       INT           NOT NULL,
    precio         DECIMAL(12,2) NOT NULL,
    importe        DECIMAL(12,2) NOT NULL,
    CONSTRAINT pkVentaDetalle PRIMARY KEY (ventaDetalleId),
    CONSTRAINT fkVentaDetalleVenta FOREIGN KEY (ventaId) REFERENCES Venta (ventaId),
    CONSTRAINT fkVentaDetalleArticulo FOREIGN KEY (articuloId) REFERENCES Articulo (articuloId),
    CONSTRAINT ckVentaDetalleCantidad CHECK (cantidad > 0)
);

CREATE TABLE Pago (
    pagoId          BIGINT        NOT NULL AUTO_INCREMENT,
    ventaId         BIGINT        NOT NULL,
    formaPago       VARCHAR(15)   NOT NULL,
    importe         DECIMAL(12,2) NOT NULL,
    importeRecibido DECIMAL(12,2) NOT NULL,
    cambio          DECIMAL(12,2) NOT NULL,
    fechaPago       DATETIME      NOT NULL,
    CONSTRAINT pkPago PRIMARY KEY (pagoId),
    CONSTRAINT uqPagoVentaId UNIQUE (ventaId),
    CONSTRAINT fkPagoVenta FOREIGN KEY (ventaId) REFERENCES Venta (ventaId),
    CONSTRAINT ckPagoFormaPago CHECK (formaPago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA'))
);

CREATE TABLE Ticket (
    ticketId  BIGINT        NOT NULL AUTO_INCREMENT,
    ventaId   BIGINT        NOT NULL,
    folio     VARCHAR(20)   NOT NULL,
    contenido VARCHAR(2000) NOT NULL,
    fecha     DATETIME      NOT NULL,
    CONSTRAINT pkTicket PRIMARY KEY (ticketId),
    CONSTRAINT uqTicketVentaId UNIQUE (ventaId),
    CONSTRAINT fkTicketVenta FOREIGN KEY (ventaId) REFERENCES Venta (ventaId)
);
