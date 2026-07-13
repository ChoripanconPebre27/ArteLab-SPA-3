CREATE TABLE categoria (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    des VARCHAR(30) NOT NULL
);

CREATE TABLE producto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    des VARCHAR(30) NOT NULL,
    precio INTEGER NOT NULL,
    stock INTEGER NOT NULL,
    id_categoria INTEGER NOT NULL,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categoria (id)
);

CREATE TABLE promocion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    des VARCHAR(30) NOT NULL,
    fecha_ini DATE NOT NULL,
    fecha_ter DATE NOT NULL,
    descuento INTEGER NOT NULL,
    id_categoria INTEGER NOT NULL,
    CONSTRAINT fk_promocion_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categoria (id)
);
