-- Datos semilla (idempotentes: no duplican en cada arranque)
INSERT INTO zona (nombre, activo) SELECT 'Norte', TRUE WHERE NOT EXISTS (SELECT 1 FROM zona WHERE nombre = 'Norte');
INSERT INTO zona (nombre, activo) SELECT 'Sur', TRUE WHERE NOT EXISTS (SELECT 1 FROM zona WHERE nombre = 'Sur');

INSERT INTO empleado (nombre, telefono, domicilio, tipo, porcentaje_comision, activo, zona_id)
SELECT 'Vendedor Norte', '5511111111', 'Av. Norte 100', 'VENDEDOR', 3.00, TRUE, (SELECT zona_id FROM zona WHERE nombre = 'Norte')
WHERE NOT EXISTS (SELECT 1 FROM empleado WHERE nombre = 'Vendedor Norte');
INSERT INTO empleado (nombre, telefono, domicilio, tipo, porcentaje_comision, activo, zona_id)
SELECT 'Vendedor Sur', '5522222222', 'Av. Sur 200', 'VENDEDOR', 4.00, TRUE, (SELECT zona_id FROM zona WHERE nombre = 'Sur')
WHERE NOT EXISTS (SELECT 1 FROM empleado WHERE nombre = 'Vendedor Sur');
INSERT INTO empleado (nombre, telefono, domicilio, tipo, porcentaje_comision, activo, zona_id)
SELECT 'Cajero Demo', '5533333333', 'Calle Caja 1', 'CAJERO', 2.00, TRUE, NULL
WHERE NOT EXISTS (SELECT 1 FROM empleado WHERE nombre = 'Cajero Demo');

INSERT INTO cliente (nombre, rfc, domicilio, telefono, telefono_alterno, contacto, limite_credito, dias_credito, activo, zona_id)
SELECT 'Ferretería La Esquina', 'AAA010101AAA', 'Calle 1 #10', '5544444444', NULL, 'Laura Pérez', 20000.00, 30, TRUE, (SELECT zona_id FROM zona WHERE nombre = 'Norte')
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE rfc = 'AAA010101AAA');
INSERT INTO cliente (nombre, rfc, domicilio, telefono, telefono_alterno, contacto, limite_credito, dias_credito, activo, zona_id)
SELECT 'Construcciones del Sur', 'BBB020202BB1', 'Calle 2 #20', '5555555555', '5566666666', 'Mario Ruiz', 50000.00, 15, TRUE, (SELECT zona_id FROM zona WHERE nombre = 'Sur')
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE rfc = 'BBB020202BB1');
INSERT INTO cliente (nombre, rfc, domicilio, telefono, telefono_alterno, contacto, limite_credito, dias_credito, activo, zona_id)
SELECT 'Mostrador Contado', 'CCC030303CC2', 'Calle 3 #30', '5577777777', NULL, NULL, 0.00, 0, TRUE, (SELECT zona_id FROM zona WHERE nombre = 'Norte')
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE rfc = 'CCC030303CC2');
