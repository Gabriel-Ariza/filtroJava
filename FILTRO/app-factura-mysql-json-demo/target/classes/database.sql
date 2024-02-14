
CREATE DATABASE IF NOT EXISTS filtro;

use filtro;


CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    direccion VARCHAR(255),
    celular VARCHAR(20),
    documento VARCHAR(20) NOT NULL,
    INDEX idx_cliente_documento (documento)
);

CREATE TABLE producto (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precioVenta DECIMAL(10, 2) NOT NULL,
    precioCompra DECIMAL(10, 2) NOT NULL,
    INDEX idx_producto_nombre (nombre)
);

CREATE TABLE factura (
    numeroFactura INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    cliente_id INT,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    INDEX idx_factura_cliente (cliente_id)
);

CREATE TABLE item_factura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    factura_numeroFactura INT,
    producto_codigo INT,
    cantidad INT NOT NULL,
    importe DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (factura_numeroFactura) REFERENCES factura(numeroFactura),
    FOREIGN KEY (producto_codigo) REFERENCES producto(codigo),
    INDEX idx_item_factura_factura (factura_numeroFactura),
    INDEX idx_item_factura_producto (producto_codigo)
);

CREATE TABLE descuento (
    id_descuento INT UNSIGNED NOT NULL AUTO_INCREMENT,
    tipo_descuento ENUM('porcentaje', 'fijo') NOT NULL,
    condiciones VARCHAR(255) NOT NULL,
    valor DOUBLE NOT NULL,
    producto VARCHAR(255) DEFAULT 'todos',
    estado ENUM('activo', 'inactivo') NOT NULL,
    PRIMARY KEY(id_descuento)
)

INSERT INTO descuento(id_descuento, tipo_descuento, condiciones, valor, producto, estado) VALUES
(1, 'porcentaje', 'Monto minimo de compra: $1000', '0.10', 'todos', 'activo'),
(2, 'fijo', 'Compra al menos 5 unidades del producto X', '5', 'X', 'activo'),
(3, 'porcentaje', 'Cliente Gold con mas de 10 compras previas', '0.15', 'todos', 'activo'),
(4, 'fijo', 'Valido solo los viernes', '3', 'todos', 'activo'),
(5, 'porcentaje', 'Compra durante temporada navideña', '0.05', 'todos', 'activo');



ALTER TABLE item_factura
ADD COLUMN iva DOUBLE NOT NULL


INSERT INTO item_factura(factura_numeroFactura, producto_codigo, cantidad) VALUES
(11, 1, 2)


DROP TRIGGER IF EXISTS generador_iva;
DELIMITER //
CREATE TRIGGER generador_iva BEFORE INSERT ON item_factura
FOR EACH ROW
BEGIN

    DECLARE precioProducto DOUBLE;
    DECLARE sumaProductos DOUBLE;

    SELECT precioVenta INTO precioProducto FROM producto WHERE codigo = NEW.producto_codigo;

    SET sumaProductos = precioProducto * NEW.cantidad;

    SET NEW.iva = sumaProductos * 0.19;
    SET NEW.importe = sumaProductos;

END;


CREATE VIEW productos_mas_vendidos AS
SELECT producto.nombre, COUNT(*) as ventas_totales FROM factura 
JOIN item_factura ON factura.numeroFactura = item_factura.factura_numeroFactura
JOIN producto ON producto.codigo = item_factura.producto_codigo
GROUP BY producto.codigo
ORDER BY ventas_totales DESC;


CREATE VIEW listado_clientes AS

SELECT COUNT(*) as compras_totales, CONCAT(cliente.nombre, " ", cliente.apellido) AS 'Cliente', cliente.documento 
FROM factura
JOIN cliente ON cliente.id = factura.cliente_id
GROUP BY cliente.documento;

SELECT SUM(importe) as total_ventas FROM item_factura

SELECT SUM(iva) as total_impuestos FROM item_factura

SELECT * FROM listado_clientes

SELECT * FROM productos_mas_vendidos