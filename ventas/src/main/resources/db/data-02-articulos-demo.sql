-- Artículos demo para poder vender sin el subsistema de Inventario (idempotente)
INSERT INTO articulo_ref (codigo, nombre, precio, existencias)
SELECT 'ART-001', 'Taladro percutor 1/2', 100.00, 50 WHERE NOT EXISTS (SELECT 1 FROM articulo_ref WHERE codigo = 'ART-001');
INSERT INTO articulo_ref (codigo, nombre, precio, existencias)
SELECT 'ART-002', 'Caja de pilas AA', 25.00, 3 WHERE NOT EXISTS (SELECT 1 FROM articulo_ref WHERE codigo = 'ART-002');
INSERT INTO articulo_ref (codigo, nombre, precio, existencias)
SELECT 'ART-003', 'Tornillo galvanizado', 10.00, 200 WHERE NOT EXISTS (SELECT 1 FROM articulo_ref WHERE codigo = 'ART-003');
