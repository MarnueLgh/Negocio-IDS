-- Ventas: zonas, empleados (vendedores y cajeros), clientes y comisiones.
-- Ejecutar sobre la base scvi después de 03_crear_recepcion.sql (ddl-auto=validate).
-- Autor: Álvarez Aguilar Saúl Antonio, Equipo 2. Fecha: 23-11-2026.

CREATE TABLE Zona (
    zonaId BIGINT      NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    activo BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT pkZona PRIMARY KEY (zonaId),
    CONSTRAINT uqZonaNombre UNIQUE (nombre)
);

-- Un vendedor pertenece a una zona; un cajero no tiene zona.
CREATE TABLE Empleado (
    empleadoId         BIGINT       NOT NULL AUTO_INCREMENT,
    nombre             VARCHAR(120) NOT NULL,
    telefono           VARCHAR(20)  NOT NULL,
    domicilio          VARCHAR(200) NOT NULL,
    tipo               VARCHAR(10)  NOT NULL,
    porcentajeComision DECIMAL(5,2) NOT NULL,
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    zonaId             BIGINT       NULL,
    CONSTRAINT pkEmpleado PRIMARY KEY (empleadoId),
    CONSTRAINT fkEmpleadoZona FOREIGN KEY (zonaId) REFERENCES Zona (zonaId),
    CONSTRAINT ckEmpleadoTipo CHECK (tipo IN ('VENDEDOR', 'CAJERO')),
    CONSTRAINT ckEmpleadoPorcentaje CHECK (porcentajeComision >= 0 AND porcentajeComision <= 100),
    CONSTRAINT ckEmpleadoZona CHECK ((tipo = 'VENDEDOR' AND zonaId IS NOT NULL) OR (tipo = 'CAJERO' AND zonaId IS NULL))
);

CREATE TABLE Cliente (
    clienteId       BIGINT        NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(120)  NOT NULL,
    rfc             VARCHAR(13)   NOT NULL,
    domicilio       VARCHAR(200)  NOT NULL,
    telefono        VARCHAR(20)   NOT NULL,
    telefonoAlterno VARCHAR(20)   NULL,
    contacto        VARCHAR(120)  NULL,
    limiteCredito   DECIMAL(12,2) NOT NULL DEFAULT 0,
    diasCredito     INT           NOT NULL DEFAULT 0,
    activo          BOOLEAN       NOT NULL DEFAULT TRUE,
    zonaId          BIGINT        NOT NULL,
    CONSTRAINT pkCliente PRIMARY KEY (clienteId),
    CONSTRAINT uqClienteRfc UNIQUE (rfc),
    CONSTRAINT fkClienteZona FOREIGN KEY (zonaId) REFERENCES Zona (zonaId),
    CONSTRAINT ckClienteCredito CHECK (limiteCredito >= 0 AND diasCredito >= 0)
);

-- ventaId y facturaId se guardan sin llave foránea: sus tablas se crean en los scripts 05 y 06.
CREATE TABLE Comision (
    comisionId         BIGINT        NOT NULL AUTO_INCREMENT,
    fechaGeneracion    DATE          NOT NULL,
    importeComision    DECIMAL(12,2) NOT NULL,
    importeVenta       DECIMAL(12,2) NOT NULL,
    porcentajeAplicado DECIMAL(5,2)  NOT NULL,
    ventaId            BIGINT        NULL,
    facturaId          BIGINT        NULL,
    empleadoId         BIGINT        NOT NULL,
    estado             VARCHAR(10)   NOT NULL DEFAULT 'GENERADA',
    CONSTRAINT pkComision PRIMARY KEY (comisionId),
    CONSTRAINT fkComisionEmpleado FOREIGN KEY (empleadoId) REFERENCES Empleado (empleadoId),
    CONSTRAINT ckComisionEstado CHECK (estado IN ('GENERADA', 'CANCELADA'))
);
