package Vista;

import Controller.UsuarioController;
import Model.Usuario;

import javax.swing.*;
import java.awt.*;

public class Login extends JDialog {
    private final UsuarioController controller;
    private Usuario usuario;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public Login(Window owner, UsuarioController controller) {
        super(owner, "Inicio de Sesión", Dialog.ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        setSize(350, 200);
        setLocationRelativeTo(owner);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtUsername = new JTextField(15);
        panel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnIngresar = new JButton("Ingresar");
        JButton btnCancelar = new JButton("Cancelar");
        btnPanel.add(btnIngresar);
        btnPanel.add(btnCancelar);
        panel.add(btnPanel, gbc);

        add(panel, BorderLayout.CENTER);

        btnIngresar.addActionListener(e -> login());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnIngresar);

        txtPassword.addActionListener(e -> login());
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        usuario = controller.autenticar(username, password);
        if (usuario != null) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Usuario o contraseña incorrectos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
