package DAO;

import Model.Matricula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatriculaDAO implements IDAO<Matricula> {
    @Override
    public List<Matricula> listar() {
        List<Matricula> lista = new ArrayList<>();
        String sql = "SELECT m.*, CONCAT(e.nombre, ' ', e.apellido) AS nombre_estudiante, cu.nombre AS nombre_curso " +
                "FROM matriculas m " +
                "JOIN estudiantes e ON m.id_estudiante = e.id_estudiante " +
                "JOIN cursos cu ON m.id_curso = cu.id_curso " +
                "ORDER BY m.fecha_matricula DESC";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Matricula> listarPorEstudianteId(int estudianteId) {
        List<Matricula> lista = new ArrayList<>();
        String sql = "SELECT m.*, CONCAT(e.nombre, ' ', e.apellido) AS nombre_estudiante, cu.nombre AS nombre_curso " +
                "FROM matriculas m " +
                "JOIN estudiantes e ON m.id_estudiante = e.id_estudiante " +
                "JOIN cursos cu ON m.id_curso = cu.id_curso " +
                "WHERE m.id_estudiante = ? " +
                "ORDER BY m.fecha_matricula DESC";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, estudianteId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Matricula> listarPorCursoProfesorId(int profesorId) {
        List<Matricula> lista = new ArrayList<>();
        String sql = "SELECT m.*, CONCAT(e.nombre, ' ', e.apellido) AS nombre_estudiante, cu.nombre AS nombre_curso " +
                "FROM matriculas m " +
                "JOIN estudiantes e ON " +
                "m.id_estudiante = e.id_estudiante " +
                "JOIN cursos cu ON m.id_curso = cu.id_curso " +
                "WHERE cu.id_profesor = ? " +
                "ORDER BY m.fecha_matricula DESC";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, profesorId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean existeMatricula(int estudianteId, int cursoId) {
        String sql = "SELECT COUNT(*) FROM matriculas WHERE id_estudiante = ? AND id_curso = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, estudianteId);
                ps.setInt(2, cursoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int contarMatriculados(int cursoId) {
        String sql = "SELECT COUNT(*) FROM matriculas WHERE id_curso = ? AND activo = true";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, cursoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Matricula obtenerPorId(int id) {
        String sql = "SELECT m.*, CONCAT(e.nombre, ' ', e.apellido) AS nombre_estudiante, cu.nombre AS nombre_curso " +
                "FROM matriculas m " +
                "JOIN estudiantes e ON " +
                "m.id_estudiante = e.id_estudiante " +
                "JOIN cursos cu ON m.id_curso = cu.id_curso " +
                "WHERE m.id_matricula = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void guardar(Matricula m) {
        String sql = "INSERT INTO matriculas (id_estudiante, id_curso, fecha_matricula) VALUES (?, ?, ?)";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, m.getEstudianteId());
                ps.setInt(2, m.getCursoId());
                ps.setDate(3, m.getFechaMatricula() != null ? Date.valueOf(m.getFechaMatricula()) : null);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Matricula m) {
        String sql = "UPDATE matriculas SET id_estudiante = ?, id_curso = ?, fecha_matricula = ?, activo = ? WHERE id_matricula = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, m.getEstudianteId());
                ps.setInt(2, m.getCursoId());
                ps.setDate(3, m.getFechaMatricula() != null ? Date.valueOf(m.getFechaMatricula()) : null);
                ps.setBoolean(4, m.isActivo());
                ps.setInt(5, m.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM matriculas WHERE id_matricula = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Matricula mapear(ResultSet rs) throws SQLException {
        Matricula m = new Matricula();
        m.setId(rs.getInt("id_matricula"));
        m.setEstudianteId(rs.getInt("id_estudiante"));
        m.setCursoId(rs.getInt("id_curso"));
        Date fm = rs.getDate("fecha_matricula");
        if (fm != null) m.setFechaMatricula(fm.toLocalDate());
        m.setActivo(rs.getBoolean("activo"));
        m.setNombreEstudiante(rs.getString("nombre_estudiante"));
        m.setNombreCurso(rs.getString("nombre_curso"));
        return m;
    }
}
