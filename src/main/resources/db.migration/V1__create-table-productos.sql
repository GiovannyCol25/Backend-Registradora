CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_producto VARCHAR(255) UNIQUE NOT NULL,
    precio_venta DOUBLE NOT NULL,
    codigo_barras BIGINT UNIQUE NOT NULL
);