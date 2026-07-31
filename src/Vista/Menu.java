package Vista;

import Controller.CursoController;
import Controller.EstudianteController;
import Controller.ProfesorController;
import Controller.UsuarioController;
import Model.Usuario;

import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPane;
    private final Usuario usuario;

    public Menu(Usuario usuario,
                UsuarioController usuarioController) {
        this.usuario = usuario;
        setTitle("Sistema de Gestión Académica Universitaria - " + usuario.getUsuario() + " (" + usuario.getRol() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);

        VistaEstudiante vistaEstudiante = new VistaEstudiante(new Controller.EstudianteController(), usuario);
        VistaProfesor vistaProfesor = new VistaProfesor(new Controller.ProfesorController(), usuario);
        VistaCurso vistaCurso = new VistaCurso(new Controller.CursoController(), new Controller.ProfesorController(), usuario);
        VistaUsuario vistaUsuario = new VistaUsuario(usuarioController, usuario);

        contentPane.add(vistaEstudiante, "Estudiantes");
        contentPane.add(vistaProfesor, "Profesores");
        contentPane.add(vistaCurso, "Cursos");
        contentPane.add(vistaUsuario, "Usuarios");

        add(contentPane, BorderLayout.CENTER);
        initMenuBar();
        cardLayout.show(contentPane, "Cursos");
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu archivoMenu = new JMenu("Archivo");
        JMenuItem cerrarSesionItem = new JMenuItem("Cerrar Sesión");
        cerrarSesionItem.addActionListener(e -> cerrarSesion());
        archivoMenu.add(cerrarSesionItem);
        archivoMenu.addSeparator();
        JMenuItem salirItem = new JMenuItem("Salir");
        salirItem.addActionListener(e -> System.exit(0));
        archivoMenu.add(salirItem);

        JMenu catalogMenu = new JMenu("Catálogo");
        JMenuItem cursosItem = new JMenuItem("Cursos");
        cursosItem.addActionListener(e -> cardLayout.show(contentPane, "Cursos"));
        catalogMenu.add(cursosItem);

        if (usuario.esAdmin() || usuario.esProfesor()) {
            JMenuItem estudiantesItem = new JMenuItem("Estudiantes");
            estudiantesItem.addActionListener(e -> cardLayout.show(contentPane, "Estudiantes"));
            catalogMenu.add(estudiantesItem);
        }

        if (usuario.esAdmin()) {
            JMenuItem profesoresItem = new JMenuItem("Profesores");
            profesoresItem.addActionListener(e -> cardLayout.show(contentPane, "Profesores"));
            catalogMenu.add(profesoresItem);
        }

        menuBar.add(archivoMenu);
        menuBar.add(catalogMenu);

        if (usuario.esAdmin()) {
            JMenu configMenu = new JMenu("Configuración");
            JMenuItem usuariosItem = new JMenuItem("Usuarios");
            usuariosItem.addActionListener(e -> cardLayout.show(contentPane, "Usuarios"));
            configMenu.add(usuariosItem);
            menuBar.add(configMenu);
        }

        setJMenuBar(menuBar);
    }

    private void cerrarSesion() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            UsuarioController controller = new UsuarioController();
            Login login = new Login(null, controller);
            login.setVisible(true);
            Usuario nuevoUsuario = login.getUsuario();
            if (nuevoUsuario != null) {
                new Menu(nuevoUsuario, controller).setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
