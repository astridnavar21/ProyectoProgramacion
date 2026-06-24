package DAO;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    private static final String URL =
            "jdbc:mysql://localhost:3306/proyecto_progra5";

    private static final String USER = "root";

    private static final String PASSWORD = "A123456789";

    public static Connection conectar() {

        try {

            Connection conexion =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Conexión exitosa");

            return conexion;

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

            return null;
        }
    }
}
