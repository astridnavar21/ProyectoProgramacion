package DAO;

import Model.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO implements IDAO<Curso>{
    @Override
    public List<Curso> listar() {
        List<Curso> lista = new ArrayList<>();
        String sql = "SELECT c.*, COALESCE(CONCAT(p.nombre, ' ', p.apellido), '—') AS nombre_profesor " +
                "FROM cursos c LEFT JOIN profesores p ON c.id_profesor = p.id_profesor " +
                "WHERE c.activo = true ORDER BY c.nombre";
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

    public List<Curso> listarPorProfesorId(int profesorId) {
        List<Curso> lista = new ArrayList<>();
        String sql = "SELECT c.*, COALESCE(CONCAT(p.nombre, ' ', p.apellido), '—') AS nombre_profesor " +
                "FROM cursos c LEFT JOIN profesores p ON c.id_profesor = p.id_profesor " +
                "WHERE c.activo = true AND c.id_profesor = ? ORDER BY c.nombre";
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

    public List<Curso> listarPorEstudianteId(int estudianteId) {
        List<Curso> lista = new ArrayList<>();
        String sql = "SELECT c.*, COALESCE(CONCAT(p.nombre, ' ', p.apellido), '—') AS nombre_profesor " +
                "FROM cursos c " +
                "LEFT JOIN profesores p ON c.id_profesor = p.id_profesor " +
                "JOIN matriculas m ON c.id_curso = m.id_curso " +
                "WHERE c.activo = true AND m.id_estudiante = ? AND m.activo = true " +
                "ORDER BY c.nombre";
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

    @Override
    public Curso obtenerPorId(int id) {
        String sql = "SELECT c.*, COALESCE(CONCAT(p.nombre, ' ', p.apellido), '—') AS nombre_profesor " +
                "FROM cursos c LEFT JOIN profesores p ON c.id_profesor = p.id_profesor WHERE c.id_curso = ?";
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
    public void guardar(Curso curso) {
        String sql = "INSERT INTO cursos (nombre, descripcion, creditos, id_profesor, cupo_maximo) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, curso.getNombre());
                ps.setString(2, curso.getDescripcion());
                ps.setInt(3, curso.getCreditos());
                ps.setInt(4, curso.getProfesorId() > 0 ? curso.getProfesorId() : 0);
                ps.setInt(5, curso.getCupoMaximo());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Curso curso) {
        String sql = "UPDATE cursos SET nombre = ?, descripcion = ?, creditos = ?, id_profesor = ?, cupo_maximo = ? WHERE id_curso = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, curso.getNombre());
                ps.setString(2, curso.getDescripcion());
                ps.setInt(3, curso.getCreditos());
                ps.setInt(4, curso.getProfesorId() > 0 ? curso.getProfesorId() : 0);
                ps.setInt(5, curso.getCupoMaximo());
                ps.setInt(6, curso.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "UPDATE cursos SET activo = false WHERE id_curso = ?";
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

    private Curso mapear(ResultSet rs) throws SQLException {
        Curso c = new Curso();
        c.setId(rs.getInt("id_curso"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setCreditos(rs.getInt("creditos"));
        c.setProfesorId(rs.getInt("id_profesor"));
        c.setNombreProfesor(rs.getString("nombre_profesor"));
        c.setCupoMaximo(rs.getInt("cupo_maximo"));
        c.setActivo(rs.getBoolean("activo"));
        return c;
    }
}
