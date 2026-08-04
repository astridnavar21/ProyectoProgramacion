package Vista;

import Controller.CursoController;
import Controller.ProfesorController;
import Model.Curso;
import Model.Profesor;
import Model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class VistaCurso extends JPanel {
    private final CursoController controller;
    private final ProfesorController profesorController;
    private final Usuario usuario;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String[] COLUMNAS = {"ID", "Nombre", "Descripción", "Créditos", "Profesor", "Cupo Máx."};

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtCreditos;
    private JComboBox<Profesor> cmbProfesor;
    private JTextField txtCupoMaximo;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnCancelar;
    private int idActual = -1;

    public VistaCurso(CursoController controller, ProfesorController profesorController, Usuario usuario) {
        this.controller = controller;
        this.profesorController = profesorController;
        this.usuario = usuario;
        setLayout(new BorderLayout(0, 10));
        initForm();
        cargarComboProfesores();
        initTable();
        cargarTabla();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                recargar();
            }
        });
    }

    private void initForm() {
        JPanel formPanel = new JPanel(new BorderLayout());
        String titulo = usuario.esAdmin() ? "Datos del Curso" : "Mis Cursos (solo lectura)";
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
        fields.add(new JLabel("Créditos:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        txtCreditos = new JTextField(8);
        fields.add(txtCreditos, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Profesor:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        cmbProfesor = new JComboBox<>();
        fields.add(cmbProfesor, gbc);
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Cupo Máx.:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        txtCupoMaximo = new JTextField(8);
        fields.add(txtCupoMaximo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHEAST;
        fields.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
        txtDescripcion = new JTextArea(2, 30);
        txtDescripcion.setLineWrap(true);
        fields.add(new JScrollPane(txtDescripcion), gbc);

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

        // Permisos
        boolean puedeEditar = usuario.esAdmin();
        btnNuevo.setEnabled(puedeEditar);
        btnGuardar.setEnabled(puedeEditar);
        btnEliminar.setEnabled(puedeEditar);
        txtNombre.setEditable(puedeEditar);
        txtDescripcion.setEditable(puedeEditar);
        txtCreditos.setEditable(puedeEditar);
        txtCupoMaximo.setEditable(puedeEditar);
        cmbProfesor.setEnabled(puedeEditar);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnCancelar.addActionListener(e -> cancelar());
        btnRefrescar.addActionListener(e -> cargarTabla());
    }

    public void recargar() {
        cargarComboProfesores();
        cargarTabla();
    }

    private void cargarComboProfesores() {
        Profesor seleccion = (Profesor) cmbProfesor.getSelectedItem();
        int idSeleccion = seleccion != null ? seleccion.getId() : 0;
        cmbProfesor.removeAllItems();
        cmbProfesor.addItem(new Profesor() {{ setId(0); setNombre("—"); setApellido(""); }});
        for (Profesor p : profesorController.listar()) {
            cmbProfesor.addItem(p);
            if (p.getId() == idSeleccion) cmbProfesor.setSelectedItem(p);
        }
        if (idSeleccion == 0 || cmbProfesor.getSelectedItem() == null) cmbProfesor.setSelectedIndex(0);
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
                if (fila >= 0) cargarFormulario(fila);
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void cargarTabla() {
        tableModel.setRowCount(0);
        java.util.List<Curso> cursos;
        if (usuario.esAdmin()) {
            cursos = controller.listar();
        } else if (usuario.esProfesor() && usuario.getProfesorId() != null) {
            cursos = controller.listarPorProfesor(usuario.getProfesorId());
        } else if (usuario.esEstudiante() && usuario.getEstudianteId() != null) {
            cursos = controller.listarPorEstudiante(usuario.getEstudianteId());
        } else {
            cursos = new java.util.ArrayList<>();
        }
        for (Curso c : cursos) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getDescripcion(),
                    c.getCreditos(), c.getNombreProfesor(), c.getCupoMaximo()
            });
        }
    }

    private void cargarFormulario(int fila) {
        if (!usuario.esAdmin()) return;
        int id = (int) tableModel.getValueAt(fila, 0);
        Curso c = controller.obtenerPorId(id);
        if (c != null) {
            idActual = c.getId();
            txtNombre.setText(c.getNombre());
            txtDescripcion.setText(c.getDescripcion());
            txtCreditos.setText(String.valueOf(c.getCreditos()));
            txtCupoMaximo.setText(String.valueOf(c.getCupoMaximo()));
            for (int i = 0; i < cmbProfesor.getItemCount(); i++) {
                if (cmbProfesor.getItemAt(i).getId() == c.getProfesorId()) {
                    cmbProfesor.setSelectedIndex(i); break;
                }
            }
        }
    }

    private void guardar() {
        if (!usuario.esAdmin()) return;
        try {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Curso c = new Curso();
            c.setNombre(txtNombre.getText().trim());
            c.setDescripcion(txtDescripcion.getText().trim());
            c.setCreditos(Integer.parseInt(txtCreditos.getText().trim()));
            Profesor p = (Profesor) cmbProfesor.getSelectedItem();
            c.setProfesorId(p != null ? p.getId() : 0);
            c.setCupoMaximo(Integer.parseInt(txtCupoMaximo.getText().trim()));

            String error;
            if (idActual == -1) {
                error = controller.guardar(c);
            } else {
                c.setId(idActual);
                error = controller.actualizar(c);
            }
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                cargarTabla();
                limpiarFormulario();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Créditos y cupo deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (!usuario.esAdmin()) return;
        if (idActual == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso de la tabla.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este curso?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCreditos.setText("");
        txtCupoMaximo.setText("30");
        cmbProfesor.setSelectedIndex(0);
        if (usuario.esAdmin()) txtNombre.requestFocus();
    }
}
