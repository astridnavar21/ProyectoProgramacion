package Vista;

import Controller.CalificacionController;
import Controller.CursoController;
import Controller.EstudianteController;
import Model.Calificacion;
import Model.Curso;
import Model.Estudiante;
import Model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class VistaCalificacion extends JPanel {
    private final CalificacionController controller;
    private final EstudianteController estudianteController;
    private final CursoController cursoController;
    private final Usuario usuario;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String[] COLUMNAS = {"ID", "Estudiante", "Curso", "Calificación"};

    private JComboBox<Estudiante> cmbEstudiante;
    private JComboBox<Curso> cmbCurso;
    private JTextField txtCalificacion;
    private int idActual = -1;
    private JLabel lblPromedio;

    public VistaCalificacion(CalificacionController controller, EstudianteController estudianteController,
                             CursoController cursoController, Usuario usuario) {
        this.controller = controller;
        this.estudianteController = estudianteController;
        this.cursoController = cursoController;
        this.usuario = usuario;
        setLayout(new BorderLayout(0, 10));

        if (usuario.esEstudiante()) {
            lblPromedio = new JLabel(" ");
            lblPromedio.setFont(lblPromedio.getFont().deriveFont(Font.BOLD, 14f));
            add(lblPromedio, BorderLayout.NORTH);
        } else {
            initForm();
            cargarCombos();
        }

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
        if (cmbEstudiante != null && cmbCurso != null) {
            cargarCombos();
        }
        cargarTabla();
    }

    private void initForm() {
        JPanel formPanel = new JPanel(new BorderLayout());
        String titulo = usuario.esAdmin() ? "Datos de la Calificación" : "Calificaciones - Mis Cursos";
        formPanel.setBorder(BorderFactory.createTitledBorder(titulo));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Estudiante:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        cmbEstudiante = new JComboBox<>();
        fields.add(cmbEstudiante, gbc);

        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Curso:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        cmbCurso = new JComboBox<>();
        fields.add(cmbCurso, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Calificación (0-100):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        txtCalificacion = new JTextField(10);
        fields.add(txtCalificacion, gbc);

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

        boolean puedeEditar = usuario.esAdmin() || usuario.esProfesor();
        btnNuevo.setEnabled(puedeEditar);
        btnGuardar.setEnabled(puedeEditar);
        btnEliminar.setEnabled(usuario.esAdmin());
        cmbEstudiante.setEnabled(puedeEditar);
        cmbCurso.setEnabled(puedeEditar);
        txtCalificacion.setEditable(puedeEditar);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnCancelar.addActionListener(e -> cancelar());
        btnRefrescar.addActionListener(e -> cargarTabla());
    }

    private void cargarCombos() {
        int idEstudiante = cmbEstudiante.getSelectedItem() != null ? ((Estudiante) cmbEstudiante.getSelectedItem()).getId() : -1;
        int idCurso = cmbCurso.getSelectedItem() != null ? ((Curso) cmbCurso.getSelectedItem()).getId() : -1;

        cmbEstudiante.removeAllItems();
        java.util.List<Estudiante> estudiantes;
        if (usuario.esAdmin()) {
            estudiantes = estudianteController.listar();
        } else if (usuario.esProfesor() && usuario.getProfesorId() != null) {
            estudiantes = estudianteController.listarPorCursoProfesor(usuario.getProfesorId());
        } else {
            estudiantes = new java.util.ArrayList<>();
        }
        for (Estudiante e : estudiantes) {
            cmbEstudiante.addItem(e);
            if (e.getId() == idEstudiante) cmbEstudiante.setSelectedItem(e);
        }
        if (cmbEstudiante.getSelectedItem() == null && cmbEstudiante.getItemCount() > 0) cmbEstudiante.setSelectedIndex(0);

        cmbCurso.removeAllItems();
        java.util.List<Curso> cursos;
        if (usuario.esAdmin()) {
            cursos = cursoController.listar();
        } else if (usuario.esProfesor() && usuario.getProfesorId() != null) {
            cursos = cursoController.listarPorProfesor(usuario.getProfesorId());
        } else {
            cursos = new java.util.ArrayList<>();
        }
        for (Curso c : cursos) {
            cmbCurso.addItem(c);
            if (c.getId() == idCurso) cmbCurso.setSelectedItem(c);
        }
        if (cmbCurso.getSelectedItem() == null && cmbCurso.getItemCount() > 0) cmbCurso.setSelectedIndex(0);
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
                if (fila >= 0 && (usuario.esAdmin() || usuario.esProfesor())) cargarFormulario(fila);
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void cargarTabla() {
        tableModel.setRowCount(0);
        java.util.List<Calificacion> list;
        if (usuario.esAdmin()) {
            list = controller.listar();
        } else if (usuario.esProfesor() && usuario.getProfesorId() != null) {
            list = controller.listarPorCursoProfesor(usuario.getProfesorId());
        } else if (usuario.esEstudiante() && usuario.getEstudianteId() != null) {
            list = controller.listarPorEstudiante(usuario.getEstudianteId());
            double promedio = controller.calcularPromedio(usuario.getEstudianteId());
            lblPromedio.setText("Mi Promedio: " + String.format("%.1f", promedio));
        } else {
            list = new java.util.ArrayList<>();
        }
        for (Calificacion c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getNombreEstudiante(), c.getNombreCurso(),
                    c.getCalificacion()
            });
        }
    }

    private void cargarFormulario(int fila) {
        int id = (int) tableModel.getValueAt(fila, 0);
        Calificacion c = controller.obtenerPorId(id);
        if (c != null) {
            idActual = c.getId();
            for (int i = 0; i < cmbEstudiante.getItemCount(); i++) {
                if (cmbEstudiante.getItemAt(i).getId() == c.getEstudianteId()) {
                    cmbEstudiante.setSelectedIndex(i); break;
                }
            }
            for (int i = 0; i < cmbCurso.getItemCount(); i++) {
                if (cmbCurso.getItemAt(i).getId() == c.getCursoId()) {
                    cmbCurso.setSelectedIndex(i); break;
                }
            }
            txtCalificacion.setText(String.valueOf(c.getCalificacion()));
        }
    }

    private void guardar() {
        if (!usuario.esAdmin() && !usuario.esProfesor()) return;
        try {
            Estudiante est = (Estudiante) cmbEstudiante.getSelectedItem();
            Curso cur = (Curso) cmbCurso.getSelectedItem();
            if (est == null || cur == null) {
                JOptionPane.showMessageDialog(this, "Seleccione estudiante y curso.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Calificacion c = new Calificacion();
            c.setEstudianteId(est.getId());
            c.setCursoId(cur.getId());
            c.setCalificacion(Double.parseDouble(txtCalificacion.getText().trim()));

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
            JOptionPane.showMessageDialog(this, "La calificación debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (!usuario.esAdmin()) return;
        if (idActual == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una calificación de la tabla.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar esta calificación?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
        if (cmbEstudiante != null && cmbEstudiante.getItemCount() > 0) cmbEstudiante.setSelectedIndex(0);
        if (cmbCurso != null && cmbCurso.getItemCount() > 0) cmbCurso.setSelectedIndex(0);
        if (txtCalificacion != null) {
            txtCalificacion.setText("");
            txtCalificacion.requestFocus();
        }
    }
}
