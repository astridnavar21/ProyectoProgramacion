import Controller.*;
import DAO.Conexion;
import Model.Usuario;
import Vista.Login;
import Vista.Menu;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            if (Conexion.getInstance().getConnection() != null) {
                System.out.println("Conexión exitosa a la base de datos");
            }
        } catch (Exception e) {
            System.out.println("Error de conexión");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            UsuarioController controller = new UsuarioController();

            Login login = new Login(null, controller);
            login.setVisible(true);

            Usuario usuario = login.getUsuario();
            if (usuario != null) {
                CursoController cursoController = new CursoController();
                EstudianteController estudianteController = new EstudianteController();
                ProfesorController profesorController = new ProfesorController();
                CalificacionController calificacionController = new CalificacionController();
                MatriculaController matriculaController = new MatriculaController(cursoController);

                Menu mainFrame = new Menu(usuario, new UsuarioController());
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}