-- INV-07: bodegas donde se recibe la mercancía (estructura mínima).
-- La mercancía siempre entra a una Bodega; Almacen es una tabla distinta.
-- Ejecutar después de 01_crear_articulo.sql.
-- Autor: Olvera Ramírez Anuar Manuel, Equipo 3. Fecha: 09-11-2026.

CREATE TABLE Bodega (
    bodegaId BIGINT       NOT NULL AUTO_INCREMENT,
    nombre   VARCHAR(100) NOT NULL,
    CONSTRAINT pkBodega PRIMARY KEY (bodegaId),
    CONSTRAINT uqBodegaNombre UNIQUE (nombre)
);

-- Datos iniciales mientras no exista la historia de registro de bodegas.
INSERT INTO Bodega (nombre) VALUES ('Bodega Central'), ('Bodega Norte');
