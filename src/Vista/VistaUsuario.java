package Vista;

import Controller.UsuarioController;
import Model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VistaUsuario extends JPanel {
    private final UsuarioController controller;
    private final Usuario usuario;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String[] COLUMNAS = {"ID", "Usuario", "Rol", "Activo"};

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JCheckBox chkActivo;
    private int idActual = -1;

    public VistaUsuario(UsuarioController controller, Usuario usuario) {
        this.controller = controller;
        this.usuario = usuario;
        setLayout(new BorderLayout(0, 10));

        if (usuario.esAdmin()) {
            initForm();
        }

        initTable();
        cargarTabla();
    }

    private void initForm() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtUsername = new JTextField(12);
        fields.add(txtUsername, gbc);
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        cmbRol = new JComboBox<>(new String[]{"ADMIN", "PROFESOR", "ESTUDIANTE"});
        fields.add(cmbRol, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtPassword = new JPasswordField(12);
        fields.add(txtPassword, gbc);
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        chkActivo = new JCheckBox("Activo");
        chkActivo.setSelected(true);
        fields.add(chkActivo, gbc);

        formPanel.add(fields, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnRefrescar = new JButton("Refrescar");
        btnPanel.add(btnNuevo);
        btnPanel.add(btnGuardar);
        btnPanel.add(btnEliminar);
        btnPanel.add(btnCancelar);
        btnPanel.add(btnRefrescar);
        formPanel.add(btnPanel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.NORTH);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnCancelar.addActionListener(e -> cancelar());
        btnRefrescar.addActionListener(e -> cargarTabla());
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = table.getSelectedRow();
                if (fila >= 0 && usuario.esAdmin()) cargarFormulario(fila);
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void cargarTabla() {
        tableModel.setRowCount(0);
        for (Usuario u : controller.listar()) {
            tableModel.addRow(new Object[]{u.getId(), u.getUsuario(), u.getRol(), u.isActivo() ? "Sí" : "No"});
        }
    }

    private void cargarFormulario(int fila) {
        int id = (int) tableModel.getValueAt(fila, 0);
        Usuario u = controller.obtenerPorId(id);
        if (u != null) {
            idActual = u.getId();
            txtUsername.setText(u.getUsuario());
            txtPassword.setText(u.getContrasena());
            cmbRol.setSelectedItem(u.getRol());
            chkActivo.setSelected(u.isActivo());
        }
    }

    private void guardar() {
        if (!usuario.esAdmin()) return;
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Usuario u = new Usuario();
        u.setUsuario(txtUsername.getText().trim());

        String pass = new String(txtPassword.getPassword());
        if (!pass.isEmpty() || idActual == -1) {
            u.setContrasena(pass);
        }
        u.setRol((String) cmbRol.getSelectedItem());

        String error;
        if (idActual == -1) {
            if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña es obligatoria para nuevos usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            error = controller.guardar(u);
        } else {
            u.setId(idActual);
            u.setActivo(chkActivo.isSelected());
            error = controller.actualizar(u);
        }
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            cargarTabla();
            limpiarFormulario();
        }
    }

    private void eliminar() {
        if (!usuario.esAdmin()) return;
        if (idActual == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.eliminar(idActual);
            cargarTabla();
            limpiarFormulario();
        }
    }

    private void cancelar() {
        limpiarFormulario();
        table.clearSelection();
    }

    private void limpiarFormulario() {
        idActual = -1;
        if (txtUsername != null) {
            txtUsername.setText("");
            txtPassword.setText("");
            cmbRol.setSelectedIndex(0);
            chkActivo.setSelected(true);
            txtUsername.requestFocus();
        }
    }
}
