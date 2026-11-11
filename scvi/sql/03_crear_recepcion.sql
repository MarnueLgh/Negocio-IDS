-- INV-07: llegada de mercancía a una bodega.
-- La llave foránea solo apunta a Bodega, por lo que la mercancía nunca entra directo a un Almacen.
-- Ejecutar después de 02_crear_bodega.sql.
-- Autor: Olvera Ramírez Anuar Manuel, Equipo 3. Fecha: 09-11-2026.

CREATE TABLE Recepcion (
    recepcionId    BIGINT      NOT NULL AUTO_INCREMENT,
    articuloId     BIGINT      NOT NULL,
    bodegaId       BIGINT      NOT NULL,
    cantidad       INT         NOT NULL,
    fechaRecepcion DATE        NOT NULL,
    ubicacion      VARCHAR(50) NOT NULL, -- Texto libre (p. ej. "Pasillo 3, Anaquel B") hasta que exista la tabla Anaquel.
    estado         VARCHAR(10) NOT NULL,
    lote           VARCHAR(30) NULL,     -- Obligatorio si el artículo es PERECEDERO (se valida en el servicio).
    fechaCaducidad DATE        NULL,     -- Obligatoria si el artículo es PERECEDERO (se valida en el servicio).
    CONSTRAINT pkRecepcion PRIMARY KEY (recepcionId),
    CONSTRAINT fkRecepcionArticulo FOREIGN KEY (articuloId) REFERENCES Articulo (articuloId),
    CONSTRAINT fkRecepcionBodega FOREIGN KEY (bodegaId) REFERENCES Bodega (bodegaId),
    CONSTRAINT ckRecepcionCantidad CHECK (cantidad > 0),
    CONSTRAINT ckRecepcionEstado CHECK (estado IN ('BUENO', 'DANADO')),
    CONSTRAINT ckRecepcionCaducidad CHECK (fechaCaducidad IS NULL OR fechaCaducidad > fechaRecepcion)
);
