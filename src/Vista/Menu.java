package Vista;

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
        setTitle("Sistema de Registro de Notas - " + usuario.getUsuario() + " (" + usuario.getRol() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);

        VistaUsuario vistaUsuario = new VistaUsuario(usuarioController, usuario);

        contentPane.add(vistaUsuario, "Usuarios");

        add(contentPane, BorderLayout.CENTER);
        initMenuBar();
        cardLayout.show(contentPane, "Usuarios");
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu archivoMenu = new JMenu("Archivo");
        JMenuItem salirItem = new JMenuItem("Salir");
        salirItem.addActionListener(e -> System.exit(0));
        archivoMenu.add(salirItem);

        JMenu catalogMenu = new JMenu("Catálogo");

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
}
