CREATE TABLE usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_usuario VARCHAR(12) NOT NULL,
    clave VARCHAR(60) NOT NULL,
    correo VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX uk_usuario_nombre_usuario
    ON usuario (nombre_usuario);

CREATE UNIQUE INDEX uk_usuario_correo
    ON usuario (correo);
