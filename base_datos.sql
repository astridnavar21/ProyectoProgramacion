CREATE DATABASE proyecto_progra5;
USE proyecto_progra5;

CREATE TABLE estudiantes (
                             id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
                             nombre VARCHAR(50) NOT NULL,
                             apellido VARCHAR(50) NOT NULL,
                             correo VARCHAR(100),
                             telefono VARCHAR(20),
                             direccion VARCHAR(200),
                             fecha_nacimiento DATE,
                             activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE profesores (
                            id_profesor INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(50) NOT NULL,
                            apellido VARCHAR(50) NOT NULL,
                            correo VARCHAR(100),
                            telefono VARCHAR(20),
                            especialidad VARCHAR(100),
                            activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE cursos (
                        id_curso INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        descripcion VARCHAR(200),
                        creditos INT,
                        cupo_maximo INT,
                        id_profesor INT,
                        activo BOOLEAN DEFAULT TRUE,
                        FOREIGN KEY (id_profesor) REFERENCES profesores(id_profesor)
);

CREATE TABLE matriculas (
                            id_matricula INT AUTO_INCREMENT PRIMARY KEY,
                            id_estudiante INT,
                            id_curso INT,
                            fecha_matricula DATE,
                            activo BOOLEAN DEFAULT TRUE,
                            FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante),
                            FOREIGN KEY (id_curso) REFERENCES cursos(id_curso)
);

CREATE TABLE calificaciones (
                                id_calificacion INT AUTO_INCREMENT PRIMARY KEY,
                                id_estudiante INT,
                                id_curso INT,
                                nota DECIMAL(5,2),
                                FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante),
                                FOREIGN KEY (id_curso) REFERENCES cursos(id_curso)
);

CREATE TABLE usuarios (
                          id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                          usuario VARCHAR(50) NOT NULL,
                          contrasena VARCHAR(256) NOT NULL,
                          rol VARCHAR(20) NOT NULL,
                          profesor_id INT,
                          estudiante_id INT,
                          activo BOOLEAN DEFAULT TRUE,
                          FOREIGN KEY (profesor_id) REFERENCES profesores(id_profesor),
                          FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id_estudiante)
);

INSERT INTO usuarios (usuario, contrasena, rol)
VALUES ('admin', SHA2('1234', 256), 'ADMIN');

INSERT INTO usuarios (usuario, contrasena, rol)
VALUES ('Astrid', SHA2('5678', 256), 'PROFESOR');

INSERT INTO usuarios (usuario, contrasena, rol)
VALUES ('Daniela', SHA2('1200', 256), 'ESTUDIANTE');