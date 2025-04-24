ALTER TABLE usuarios
ADD COLUMN rol VARCHAR(50) NOT NULL DEFAULT 'EMPLEADO';

-- Agregar relación con la tabla empleados
ALTER TABLE usuarios
ADD COLUMN empleado_id BIGINT;

-- Restricción de clave foránea
ALTER TABLE usuarios
ADD CONSTRAINT fk_usuario_empleado
FOREIGN KEY (empleado_id) REFERENCES empleados(id);