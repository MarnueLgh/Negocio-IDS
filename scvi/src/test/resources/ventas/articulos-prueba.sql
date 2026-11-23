-- Artículos de prueba del subsistema de Ventas, en la tabla Articulo de Inventario.
-- Los carga @PruebaIntegracion antes de cada prueba, dentro de su transacción (se deshacen con el rollback).
INSERT INTO Articulo (nombre, codigoBarras, marca, proveedor, unidadMedida, existencias, existenciasMinimas,
                      existenciasMaximas, precio, costo, tipo, activo)
VALUES ('Taladro percutor 1/2', 'ART-001', 'Marca prueba', 'Proveedor prueba', 'PIEZA', 50, 0, 1000, 100.00, 60.00,
        'NO_PERECEDERO', TRUE),
       ('Caja de pilas AA', 'ART-002', 'Marca prueba', 'Proveedor prueba', 'PIEZA', 3, 0, 1000, 25.00, 15.00,
        'NO_PERECEDERO', TRUE),
       ('Tornillo galvanizado', 'ART-003', 'Marca prueba', 'Proveedor prueba', 'PIEZA', 200, 0, 1000, 10.00, 6.00,
        'NO_PERECEDERO', TRUE);
