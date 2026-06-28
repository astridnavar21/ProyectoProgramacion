package Vista;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Controller.EstudianteController;
import Model.Estudiante;
import Model.Usuario;

import java.awt.*;
import java.time.LocalDate;

public class VistaEstudiante extends JPanel {
    private final EstudianteController controller;
    private final Usuario usuario;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String[] COLUMNAS = {"ID", "Nombre", "Apellido", "Correo", "Teléfono", "Dirección", "Fecha Nac."};

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JTextField txtFecha;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnCancelar;
    private int idActual = -1;

    public VistaEstudiante(EstudianteController controller, Usuario usuario) {
        this.controller = controller;
        this.usuario = usuario;
        setLayout(new BorderLayout(0, 10));
        initForm();
        initTable();
        cargarTabla();
    }

    private void initForm() {
        JPanel formPanel = new JPanel(new BorderLayout());
        String titulo = usuario.esAdmin() ? "Datos del Estudiante"
                       : usuario.esProfesor() ? "Estudiantes en mis cursos (solo lectura)"
                       : "Estudiantes";
        formPanel.setBorder(BorderFactory.createTitledBorder(titulo));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtNombre = new JTextField(12);
        fields.add(txtNombre, gbc);
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        txtApellido = new JTextField(12);
        fields.add(txtApellido, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtEmail = new JTextField(12);
        fields.add(txtEmail, gbc);
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        txtTelefono = new JTextField(12);
        fields.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtDireccion = new JTextField(12);
        fields.add(txtDireccion, gbc);
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Fecha Nac. (yyyy-MM-dd):"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        txtFecha = new JTextField(12);
        fields.add(txtFecha, gbc);

        formPanel.add(fields, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");
        btnCancelar = new JButton("Cancelar");
        JButton btnRefrescar = new JButton("Refrescar");
        btnPanel.add(btnNuevo);
        btnPanel.add(btnGuardar);
        btnPanel.add(btnEliminar);
        btnPanel.add(btnCancelar);
        btnPanel.add(btnRefrescar);
        formPanel.add(btnPanel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.NORTH);

        boolean crud = usuario.esAdmin();
        btnNuevo.setEnabled(crud);
        btnGuardar.setEnabled(crud);
        btnEliminar.setEnabled(crud);
        txtNombre.setEditable(crud);
        txtApellido.setEditable(crud);
        txtEmail.setEditable(crud);
        txtTelefono.setEditable(crud);
        txtDireccion.setEditable(crud);
        txtFecha.setEditable(crud);

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
        java.util.List<Estudiante> list;
        if (usuario.esAdmin()) {
            list = controller.listar();
        } else if (usuario.esProfesor() && usuario.getProfesorId() != null) {
            list = controller.listarPorCursoProfesor(usuario.getProfesorId());
        } else {
            list = new java.util.ArrayList<>();
        }
        for (Estudiante e : list) {
            tableModel.addRow(new Object[]{
                e.getId(), e.getNombre(), e.getApellido(), e.getCorreo(),
                e.getTelefono(), e.getDireccion(),
                e.getFechaNacimiento() != null ? e.getFechaNacimiento().toString() : ""
            });
        }
    }

    private void cargarFormulario(int fila) {
        if (!usuario.esAdmin()) return;
        int id = (int) tableModel.getValueAt(fila, 0);
        Estudiante e = controller.obtenerPorId(id);
        if (e != null) {
            idActual = e.getId();
            txtNombre.setText(e.getNombre());
            txtApellido.setText(e.getApellido());
            txtEmail.setText(e.getCorreo());
            txtTelefono.setText(e.getTelefono());
            txtDireccion.setText(e.getDireccion());
            txtFecha.setText(e.getFechaNacimiento() != null ? e.getFechaNacimiento().toString() : "");
        }
    }

    private void guardar() {
        if (!usuario.esAdmin()) return;
        try {
            if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y apellido son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Estudiante e = new Estudiante();
            e.setNombre(txtNombre.getText().trim());
            e.setApellido(txtApellido.getText().trim());
            e.setCorreo(txtEmail.getText().trim());
            e.setTelefono(txtTelefono.getText().trim());
            e.setDireccion(txtDireccion.getText().trim());
            String fecha = txtFecha.getText().trim();
            e.setFechaNacimiento(fecha.isEmpty() ? null : LocalDate.parse(fecha));
            String error;
            if (idActual == -1) {
                error = controller.guardar(e);
            } else {
                e.setId(idActual);
                error = controller.actualizar(e);
            }
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                cargarTabla();
                limpiarFormulario();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (!usuario.esAdmin()) return;
        if (idActual == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este estudiante?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
        txtNombre.setText(""); txtApellido.setText(""); txtEmail.setText("");
        txtTelefono.setText(""); txtDireccion.setText(""); txtFecha.setText("");
        if (usuario.esAdmin()) txtNombre.requestFocus();
    }
}
