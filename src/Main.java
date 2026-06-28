//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import Controller.UsuarioController;
import DAO.Conexion;
import Model.Usuario;
import Vista.Login;
import Vista.Menu;
import Vista.VistaUsuario;

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

                Menu mainFrame = new Menu(usuario, new UsuarioController());
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}