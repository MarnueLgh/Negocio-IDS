CREATE TABLE bodega (
                        id_bodega BIGINT AUTO_INCREMENT,
                        nombre VARCHAR(100) NOT NULL,
                        descripcion VARCHAR(255) NULL,
                        CONSTRAINT pkBodega PRIMARY KEY (id_bodega),
                        CONSTRAINT ukBodegaNombre UNIQUE (nombre)
);