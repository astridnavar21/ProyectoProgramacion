CREATE DATABASE proyecto_progra5;
USE proyecto_progra5;

CREATE TABLE estudiantes (
                             id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
                             nombre VARCHAR(50) NOT NULL,
                             apellido VARCHAR(50) NOT NULL,
                             correo VARCHAR(100),
                             telefono VARCHAR(20),
                             direccion VARCHAR(200),
                             fecha_nacimiento DATE
);

CREATE TABLE profesores (
                            id_profesor INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(50) NOT NULL,
                            apellido VARCHAR(50) NOT NULL,
                            correo VARCHAR(100),
                            telefono VARCHAR(20),
                            especialidad VARCHAR(100)
);

CREATE TABLE cursos (
                        id_curso INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        descripcion VARCHAR(200),
                        creditos INT,
                        cupo_maximo INT,
                        id_profesor INT,
                        FOREIGN KEY (id_profesor) REFERENCES profesores(id_profesor)
);

CREATE TABLE matriculas (
                            id_matricula INT AUTO_INCREMENT PRIMARY KEY,
                            id_estudiante INT,
                            id_curso INT,
                            fecha_matricula DATE,
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
                          contrasena VARCHAR(50) NOT NULL,
                          rol VARCHAR(20) NOT NULL
);

INSERT INTO usuarios (usuario, contrasena, rol)
VALUES ('admin', '1234', 'ADMIN');

INSERT INTO usuarios (usuario, contrasena, rol)
VALUES ('Gabriel', '5678', 'PROFESOR');

INSERT INTO usuarios (usuario, contrasena, rol)
VALUES ('Daniela', '1200', 'ESTUDIANTE');