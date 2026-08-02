package Vista;

import Controller.CursoController;
import Controller.EstudianteController;
import Controller.MatriculaController;
import Model.Curso;
import Model.Estudiante;
import Model.Matricula;
import Model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class VistaMatricula extends JPanel {
    private final MatriculaController controller;
    private final EstudianteController estudianteController;
    private final CursoController cursoController;
    private final Usuario usuario;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String[] COLUMNAS = {"ID", "Estudiante", "Curso", "Fecha Matrícula", "Activo"};

    private JComboBox<Estudiante> cmbEstudiante;
    private JComboBox<Curso> cmbCurso;
    private JTextField txtFecha;
    private JCheckBox chkActivo;
    private int idActual = -1;

    public VistaMatricula(MatriculaController controller, EstudianteController estudianteController,
                          CursoController cursoController, Usuario usuario) {
        this.controller = controller;
        this.estudianteController = estudianteController;
        this.cursoController = cursoController;
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de la Matrícula"));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Estudiante | Curso
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Estudiante:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        cmbEstudiante = new JComboBox<>();
        for (Estudiante e : estudianteController.listar()) cmbEstudiante.addItem(e);
        fields.add(cmbEstudiante, gbc);

        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Curso:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        cmbCurso = new JComboBox<>();
        for (Curso c : cursoController.listar()) cmbCurso.addItem(c);
        fields.add(cmbCurso, gbc);

        // Row 1: Fecha | Activo
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Fecha (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtFecha = new JTextField(10);
        fields.add(txtFecha, gbc);
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
        java.util.List<Matricula> list;
        if (usuario.esAdmin()) {
            list = controller.listar();
        } else if (usuario.esProfesor() && usuario.getProfesorId() != null) {
            list = controller.listarPorCursoProfesor(usuario.getProfesorId());
        } else if (usuario.esEstudiante() && usuario.getEstudianteId() != null) {
            list = controller.listarPorEstudiante(usuario.getEstudianteId());
        } else {
            list = new java.util.ArrayList<>();
        }
        for (Matricula m : list) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getNombreEstudiante(), m.getNombreCurso(),
                    m.getFechaMatricula() != null ? m.getFechaMatricula().toString() : "",
                    m.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void cargarFormulario(int fila) {
        int id = (int) tableModel.getValueAt(fila, 0);
        Matricula m = controller.obtenerPorId(id);
        if (m != null) {
            idActual = m.getId();
            for (int i = 0; i < cmbEstudiante.getItemCount(); i++) {
                if (cmbEstudiante.getItemAt(i).getId() == m.getEstudianteId()) {
                    cmbEstudiante.setSelectedIndex(i); break;
                }
            }
            for (int i = 0; i < cmbCurso.getItemCount(); i++) {
                if (cmbCurso.getItemAt(i).getId() == m.getCursoId()) {
                    cmbCurso.setSelectedIndex(i); break;
                }
            }
            txtFecha.setText(m.getFechaMatricula() != null ? m.getFechaMatricula().toString() : "");
            chkActivo.setSelected(m.isActivo());
        }
    }

    private void guardar() {
        if (!usuario.esAdmin()) return;
        try {
            Estudiante est = (Estudiante) cmbEstudiante.getSelectedItem();
            Curso cur = (Curso) cmbCurso.getSelectedItem();
            if (est == null || cur == null) {
                JOptionPane.showMessageDialog(this, "Seleccione estudiante y curso.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Matricula m = new Matricula();
            m.setEstudianteId(est.getId());
            m.setCursoId(cur.getId());
            String fecha = txtFecha.getText().trim();
            m.setFechaMatricula(fecha.isEmpty() ? LocalDate.now() : LocalDate.parse(fecha));
            m.setActivo(chkActivo.isSelected());

            String error;
            if (idActual == -1) {
                error = controller.guardar(m);
            } else {
                m.setId(idActual);
                error = controller.actualizar(m);
            }
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                cargarTabla();
                limpiarFormulario();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida. Use el formato yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (!usuario.esAdmin()) return;
        if (idActual == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una matrícula de la tabla.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar esta matrícula?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
        if (txtFecha != null) {
            txtFecha.setText("");
            chkActivo.setSelected(true);
            txtFecha.requestFocus();
        }
    }
}
