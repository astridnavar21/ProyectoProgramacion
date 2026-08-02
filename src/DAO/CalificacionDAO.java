package DAO;

import Model.Calificacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalificacionDAO implements IDAO<Calificacion>{
    @Override
    public List<Calificacion> listar() {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(e.nombre, ' ', e.apellido) AS nombre_estudiante, cu.nombre AS nombre_curso " +
                "FROM calificaciones c " +
                "JOIN estudiantes e ON c.id_estudiante = e.id_estudiante " +
                "JOIN cursos cu ON c. id_curso = cu. id_curso " +
                "ORDER BY c.fecha_registro DESC";
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

    public List<Calificacion> listarPorEstudianteId(int estudianteId) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(e.nombre, ' ', e.apellido) AS nombre_estudiante, cu.nombre AS nombre_curso " +
                "FROM calificaciones c " +
                "JOIN estudiantes e ON c.id_estudiante = e.id_estudiante " +
                "JOIN cursos cu ON c. id_curso = cu. id_curso " +
                "WHERE c.id_estudiante = ? " +
                "ORDER BY c.fecha_registro DESC";
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

    public List<Calificacion> listarPorCursoProfesorId(int profesorId) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(e.nombre, ' ', e.apellido) AS nombre_estudiante, cu.nombre AS nombre_curso " +
                "FROM calificaciones c " +
                "JOIN estudiantes e ON c.id_estudiante = e.id_estudiante " +
                "JOIN cursos cu ON c. id_curso = cu. id_curso " +
                "WHERE cu.id_profesor = ? " +
                "ORDER BY c.fecha_registro DESC";
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

    public double calcularPromedioPorEstudiante(int estudianteId) {
        String sql = "SELECT COALESCE(AVG(calificacion), 0) FROM calificaciones WHERE id_estudiante = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, estudianteId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Calificacion obtenerPorId(int id) {
        String sql = "SELECT c.*, CONCAT(e.nombre, ' ', e.apellido) AS nombre_estudiante, cu.nombre AS nombre_curso " +
                "FROM calificaciones c " +
                "JOIN estudiantes e ON c.id_estudiante = e.id_estudiante " +
                "JOIN cursos cu ON c. id_curso = cu. id_curso " +
                "WHERE c.id = ?";
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
    public void guardar(Calificacion c) {
        String sql = "INSERT INTO calificaciones (id_estudiante,  id_curso, calificacion) VALUES (?, ?, ?)";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, c.getEstudianteId());
                ps.setInt(2, c.getCursoId());
                ps.setDouble(3, c.getCalificacion());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Calificacion c) {
        String sql = "UPDATE calificaciones SET id_estudiante = ?,  id_curso = ?, calificacion = ? WHERE id = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, c.getEstudianteId());
                ps.setInt(2, c.getCursoId());
                ps.setDouble(3, c.getCalificacion());
                ps.setInt(4, c.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM calificaciones WHERE id = ?";
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

    private Calificacion mapear(ResultSet rs) throws SQLException {
        Calificacion c = new Calificacion();
        c.setId(rs.getInt("id"));
        c.setEstudianteId(rs.getInt("estudiante_id"));
        c.setCursoId(rs.getInt("curso_id"));
        c.setCalificacion(rs.getDouble("calificacion"));
        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) c.setFechaRegistro(ts.toLocalDateTime());
        c.setNombreEstudiante(rs.getString("nombre_estudiante"));
        c.setNombreCurso(rs.getString("nombre_curso"));
        return c;
    }
}
