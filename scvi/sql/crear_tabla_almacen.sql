CREATE TABLE Almacen (
                         almacenId BIGINT AUTO_INCREMENT NOT NULL,
                         nombre VARCHAR(100) NOT NULL,
                         direccion VARCHAR(255) NOT NULL,
                         activo BOOLEAN NOT NULL DEFAULT TRUE,
                         CONSTRAINT pkAlmacen PRIMARY KEY (almacenId),
                         CONSTRAINT ukAlmacenNombre UNIQUE (nombre)
);