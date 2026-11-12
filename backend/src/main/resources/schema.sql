-- Ludexandria - esquema MySQL (MVP + catálogos del PDF)

CREATE TABLE IF NOT EXISTS usuario (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(180) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  nombre VARCHAR(120) NOT NULL,
  rol VARCHAR(30) NOT NULL DEFAULT 'USER',
  fecha_alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS juego (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  igdb_id BIGINT UNIQUE,
  nombre VARCHAR(255) NOT NULL,
  resumen TEXT,
  fecha_salida DATE NULL,
  nota_igdb DOUBLE NULL,
  duracion_minutos INT NULL,
  url_portada VARCHAR(500) NULL,
  ultima_actualizacion TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS genero (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  igdb_id BIGINT UNIQUE,
  nombre VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS tema (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  igdb_id BIGINT UNIQUE,
  nombre VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS plataforma (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  igdb_id BIGINT UNIQUE,
  nombre VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS compania (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  igdb_id BIGINT UNIQUE,
  nombre VARCHAR(180) NOT NULL
);

CREATE TABLE IF NOT EXISTS juego_genero (
  id_juego BIGINT NOT NULL,
  id_genero BIGINT NOT NULL,
  PRIMARY KEY (id_juego, id_genero),
  CONSTRAINT fk_jg_juego FOREIGN KEY (id_juego) REFERENCES juego(id) ON DELETE CASCADE,
  CONSTRAINT fk_jg_genero FOREIGN KEY (id_genero) REFERENCES genero(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS juego_tema (
  id_juego BIGINT NOT NULL,
  id_tema BIGINT NOT NULL,
  PRIMARY KEY (id_juego, id_tema),
  CONSTRAINT fk_jt_juego FOREIGN KEY (id_juego) REFERENCES juego(id) ON DELETE CASCADE,
  CONSTRAINT fk_jt_tema FOREIGN KEY (id_tema) REFERENCES tema(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS juego_plataforma (
  id_juego BIGINT NOT NULL,
  id_plataforma BIGINT NOT NULL,
  PRIMARY KEY (id_juego, id_plataforma),
  CONSTRAINT fk_jp_juego FOREIGN KEY (id_juego) REFERENCES juego(id) ON DELETE CASCADE,
  CONSTRAINT fk_jp_plat FOREIGN KEY (id_plataforma) REFERENCES plataforma(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS juego_compania (
  id_juego BIGINT NOT NULL,
  id_compania BIGINT NOT NULL,
  es_desarrolladora TINYINT(1) NOT NULL DEFAULT 0,
  es_editora TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (id_juego, id_compania),
  CONSTRAINT fk_jc_juego FOREIGN KEY (id_juego) REFERENCES juego(id) ON DELETE CASCADE,
  CONSTRAINT fk_jc_comp FOREIGN KEY (id_compania) REFERENCES compania(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS biblioteca (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_usuario BIGINT NOT NULL,
  id_juego BIGINT NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'pendiente',
  nota_personal DOUBLE NULL,
  horas_jugadas DOUBLE NULL DEFAULT 0,
  fecha_anadido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_bib_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_bib_juego FOREIGN KEY (id_juego) REFERENCES juego(id) ON DELETE CASCADE,
  CONSTRAINT uq_usuario_juego UNIQUE (id_usuario, id_juego)
);

CREATE TABLE IF NOT EXISTS recomendacion_ia (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_usuario BIGINT NOT NULL,
  peticion TEXT,
  respuesta TEXT,
  tokens INT NULL,
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_rec_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS consumo_ia (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_usuario BIGINT NOT NULL,
  fecha DATE NOT NULL,
  tokens INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_cons_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT uq_consumo_dia UNIQUE (id_usuario, fecha)
);
