USE casas_embrujadas;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS detalle_boleta;
DROP TABLE IF EXISTS boleta;
DROP TABLE IF EXISTS reserva;
DROP TABLE IF EXISTS favorito;
DROP TABLE IF EXISTS ficha_paranormal;
DROP TABLE IF EXISTS mito;
DROP TABLE IF EXISTS imagen;
DROP TABLE IF EXISTS producto_categoria;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS comuna;
DROP TABLE IF EXISTS region;
DROP TABLE IF EXISTS rol;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE rol (
    id_rol INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(20) NOT NULL,
    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE region (
    id_region INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    CONSTRAINT pk_region PRIMARY KEY (id_region),
    CONSTRAINT uq_region_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE comuna (
    id_comuna INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    id_region INT NOT NULL,
    CONSTRAINT pk_comuna PRIMARY KEY (id_comuna),
    CONSTRAINT uq_comuna_nombre_region UNIQUE (nombre, id_region),
    CONSTRAINT fk_comuna_region FOREIGN KEY (id_region) REFERENCES region (id_region) ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE usuario (
    id_usuario BIGINT NOT NULL AUTO_INCREMENT,
    run VARCHAR(10) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    direccion VARCHAR(150) NULL,
    id_comuna INT NULL,
    id_rol INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_run UNIQUE (run),
    CONSTRAINT uq_usuario_correo UNIQUE (correo),
    CONSTRAINT fk_usuario_comuna FOREIGN KEY (id_comuna) REFERENCES comuna (id_comuna) ON DELETE SET NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol) ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE categoria (
    id_categoria INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(255) NULL,
    CONSTRAINT pk_categoria PRIMARY KEY (id_categoria),
    CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE producto (
    id_producto BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(120) NOT NULL,
    descripcion TEXT NOT NULL,
    precio DECIMAL(14, 0) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    metros_cuadrados INT NOT NULL,
    dormitorios INT NOT NULL,
    banos INT NOT NULL,
    nivel_embrujo TINYINT NOT NULL,
    estado VARCHAR(12) NOT NULL DEFAULT 'DISPONIBLE',
    latitud DECIMAL(9, 6) NOT NULL,
    longitud DECIMAL(9, 6) NOT NULL,
    fecha_publicacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_comuna INT NOT NULL,
    CONSTRAINT pk_producto PRIMARY KEY (id_producto),
    CONSTRAINT fk_producto_comuna FOREIGN KEY (id_comuna) REFERENCES comuna (id_comuna) ON DELETE RESTRICT,
    CONSTRAINT chk_producto_precio CHECK (precio > 0),
    CONSTRAINT chk_producto_medidas CHECK (metros_cuadrados > 0 AND dormitorios >= 0 AND banos >= 0),
    CONSTRAINT chk_producto_nivel CHECK (nivel_embrujo BETWEEN 1 AND 5),
    CONSTRAINT chk_producto_estado CHECK (estado IN ('DISPONIBLE', 'RESERVADA', 'VENDIDA', 'INACTIVA')),
    CONSTRAINT chk_producto_coordenadas CHECK (latitud BETWEEN -90 AND 90 AND longitud BETWEEN -180 AND 180),
    INDEX idx_producto_estado (estado),
    INDEX idx_producto_precio (precio)
) ENGINE = InnoDB;

CREATE TABLE producto_categoria (
    id_producto BIGINT NOT NULL,
    id_categoria INT NOT NULL,
    CONSTRAINT pk_producto_categoria PRIMARY KEY (id_producto, id_categoria),
    CONSTRAINT fk_prodcat_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE CASCADE,
    CONSTRAINT fk_prodcat_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria) ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE imagen (
    id_imagen BIGINT NOT NULL AUTO_INCREMENT,
    url VARCHAR(500) NOT NULL,
    orden INT NOT NULL DEFAULT 1,
    autor VARCHAR(150) NOT NULL,
    licencia VARCHAR(80) NOT NULL,
    url_fuente VARCHAR(500) NULL,
    id_producto BIGINT NOT NULL,
    CONSTRAINT pk_imagen PRIMARY KEY (id_imagen),
    CONSTRAINT fk_imagen_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE CASCADE,
    CONSTRAINT chk_imagen_orden CHECK (orden >= 1)
) ENGINE = InnoDB;

CREATE TABLE mito (
    id_mito BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    relato TEXT NOT NULL,
    anio_origen SMALLINT NULL,
    fuente VARCHAR(500) NOT NULL,
    id_producto BIGINT NOT NULL,
    CONSTRAINT pk_mito PRIMARY KEY (id_mito),
    CONSTRAINT fk_mito_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE ficha_paranormal (
    id_producto BIGINT NOT NULL,
    tipo_entidad VARCHAR(80) NOT NULL,
    horario_actividad VARCHAR(80) NOT NULL,
    fenomenos_reportados TEXT NOT NULL,
    ultimo_avistamiento DATE NULL,
    CONSTRAINT pk_ficha_paranormal PRIMARY KEY (id_producto),
    CONSTRAINT fk_ficha_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE favorito (
    id_usuario BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_favorito PRIMARY KEY (id_usuario, id_producto),
    CONSTRAINT fk_favorito_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_favorito_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE reserva (
    id_reserva BIGINT NOT NULL AUTO_INCREMENT,
    id_producto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion DATETIME NOT NULL,
    estado VARCHAR(12) NOT NULL DEFAULT 'ACTIVA',
    producto_reserva_activa BIGINT GENERATED ALWAYS AS (CASE WHEN estado = 'ACTIVA' THEN id_producto END) VIRTUAL,
    CONSTRAINT pk_reserva PRIMARY KEY (id_reserva),
    CONSTRAINT uq_reserva_activa_por_producto UNIQUE (producto_reserva_activa),
    CONSTRAINT fk_reserva_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT chk_reserva_estado CHECK (estado IN ('ACTIVA', 'EXPIRADA', 'CANCELADA', 'CONVERTIDA')),
    CONSTRAINT chk_reserva_fechas CHECK (fecha_expiracion > fecha_inicio),
    INDEX idx_reserva_estado_expiracion (estado, fecha_expiracion)
) ENGINE = InnoDB;

CREATE TABLE boleta (
    id_boleta BIGINT NOT NULL AUTO_INCREMENT,
    numero INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    neto DECIMAL(14, 0) NOT NULL,
    iva DECIMAL(14, 0) NOT NULL,
    total DECIMAL(14, 0) NOT NULL,
    estado VARCHAR(12) NOT NULL DEFAULT 'PAGADA',
    id_usuario BIGINT NOT NULL,
    CONSTRAINT pk_boleta PRIMARY KEY (id_boleta),
    CONSTRAINT uq_boleta_numero UNIQUE (numero),
    CONSTRAINT fk_boleta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE RESTRICT,
    CONSTRAINT chk_boleta_estado CHECK (estado IN ('PAGADA', 'ANULADA')),
    CONSTRAINT chk_boleta_total CHECK (neto > 0 AND iva >= 0 AND total = neto + iva),
    INDEX idx_boleta_fecha (fecha)
) ENGINE = InnoDB;

CREATE TABLE detalle_boleta (
    id_detalle BIGINT NOT NULL AUTO_INCREMENT,
    id_boleta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(14, 0) NOT NULL,
    subtotal DECIMAL(14, 0) NOT NULL,
    CONSTRAINT pk_detalle_boleta PRIMARY KEY (id_detalle),
    CONSTRAINT uq_detalle_boleta_producto UNIQUE (id_boleta, id_producto),
    CONSTRAINT fk_detalle_boleta FOREIGN KEY (id_boleta) REFERENCES boleta (id_boleta) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE RESTRICT,
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad = 1),
    CONSTRAINT chk_detalle_subtotal CHECK (subtotal = precio_unitario * cantidad)
) ENGINE = InnoDB;
