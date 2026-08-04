package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Controller.ProfesorController;
import Model.Profesor;
import Model.Usuario;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class VistaProfesor extends JPanel {
    private final ProfesorController controller;
    private final Usuario usuario;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String[] COLUMNAS = {"ID", "Nombre", "Apellido", "Correo", "Teléfono", "Especialidad"};

    private JTextField txtNombre, txtApellido, txtCorreo, txtTelefono, txtEspecialidad;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnCancelar;
    private int idActual = -1;

    public VistaProfesor(ProfesorController controller, Usuario usuario) {
        this.controller = controller;
        this.usuario = usuario;
        setLayout(new BorderLayout(0, 10));
        initForm();
        initTable();
        cargarTabla();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                recargar();
            }
        });
    }

    public void recargar() {
        cargarTabla();
    }

    private void initForm() {
        JPanel formPanel = new JPanel(new BorderLayout());
        String titulo = usuario.esAdmin() ? "Datos del Profesor" : "Mi Perfil (solo lectura)";
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
        fields.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtCorreo = new JTextField(12);
        fields.add(txtCorreo, gbc);
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        txtTelefono = new JTextField(12);
        fields.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Especialidad:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
        txtEspecialidad = new JTextField(30);
        fields.add(txtEspecialidad, gbc);

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
        txtCorreo.setEditable(crud);
        txtTelefono.setEditable(crud);
        txtEspecialidad.setEditable(crud);

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
        java.util.List<Profesor> list;
        if (usuario.esAdmin()) {
            list = controller.listar();
        } else if (usuario.esProfesor() && usuario.getProfesorId() != null) {
            Profesor p = controller.obtenerPorId(usuario.getProfesorId());
            list = p != null ? java.util.List.of(p) : new java.util.ArrayList<>();
        } else {
            list = new java.util.ArrayList<>();
        }
        for (Profesor p : list) {
            tableModel.addRow(new Object[]{p.getId(), p.getNombre(), p.getApellido(), p.getCorreo(), p.getTelefono(), p.getEspecialidad()});
        }
    }

    private void cargarFormulario(int fila) {
        if (!usuario.esAdmin()) return;
        int id = (int) tableModel.getValueAt(fila, 0);
        Profesor p = controller.obtenerPorId(id);
        if (p != null) {
            idActual = p.getId();
            txtNombre.setText(p.getNombre());
            txtApellido.setText(p.getApellido());
            txtCorreo.setText(p.getCorreo());
            txtTelefono.setText(p.getTelefono());
            txtEspecialidad.setText(p.getEspecialidad());
        }
    }

    private void guardar() {
        if (!usuario.esAdmin()) return;
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y apellido son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Profesor p = new Profesor();
        p.setNombre(txtNombre.getText().trim());
        p.setApellido(txtApellido.getText().trim());
        p.setCorreo(txtCorreo.getText().trim());
        p.setTelefono(txtTelefono.getText().trim());
        p.setEspecialidad(txtEspecialidad.getText().trim());
        String error;
        if (idActual == -1) {
            error = controller.guardar(p);
        } else {
            p.setId(idActual);
            error = controller.actualizar(p);
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
            JOptionPane.showMessageDialog(this, "Seleccione un profesor.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este profesor?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
        txtNombre.setText(""); txtApellido.setText(""); txtCorreo.setText("");
        txtTelefono.setText(""); txtEspecialidad.setText("");
        if (usuario.esAdmin()) txtNombre.requestFocus();
    }
}
