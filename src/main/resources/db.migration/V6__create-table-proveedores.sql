CREATE TABLE proveedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razonSocial varchar(255) NOT NULL,
    nit varchar(255) NULL,
    telefono bigint NOT NULL
);