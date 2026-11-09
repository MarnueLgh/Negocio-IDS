-- INV-04: catálogo de artículos del subsistema de Inventario.
-- Ejecutar sobre la base scvi antes de arrancar la aplicación (ddl-auto=validate).
-- Autor: Olvera Ramírez Anuar Manuel, Equipo 3. Fecha: 09-11-2026.

CREATE TABLE Articulo (
    articuloId         BIGINT        NOT NULL AUTO_INCREMENT,
    nombre             VARCHAR(100)  NOT NULL,
    descripcion        VARCHAR(255)  NULL,
    codigoBarras       VARCHAR(50)   NOT NULL,
    marca              VARCHAR(60)   NOT NULL,
    proveedor          VARCHAR(100)  NOT NULL, -- Texto libre hasta que exista la tabla Proveedor (Compras).
    unidadMedida       VARCHAR(20)   NOT NULL,
    tamanio            VARCHAR(30)   NULL,
    existencias        INT           NOT NULL,
    existenciasMinimas INT           NOT NULL,
    existenciasMaximas INT           NOT NULL,
    precio             DECIMAL(12,2) NOT NULL,
    costo              DECIMAL(12,2) NOT NULL,
    tipo               VARCHAR(15)   NOT NULL,
    fechaCaducidad     DATE          NULL,
    CONSTRAINT pkArticulo PRIMARY KEY (articuloId),
    CONSTRAINT uqArticuloCodigoBarras UNIQUE (codigoBarras),
    CONSTRAINT ckArticuloExistencias CHECK (existencias >= 0 AND existenciasMinimas >= 0),
    CONSTRAINT ckArticuloRangoExistencias CHECK (existenciasMinimas <= existenciasMaximas),
    CONSTRAINT ckArticuloMontos CHECK (precio >= 0 AND costo >= 0),
    CONSTRAINT ckArticuloTipo CHECK (tipo IN ('PERECEDERO', 'NO_PERECEDERO')),
    CONSTRAINT ckArticuloCaducidad CHECK (tipo <> 'PERECEDERO' OR fechaCaducidad IS NOT NULL)
);
