package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.Estudiante;

public class EstudianteDAO implements IDAO<Estudiante> {

    @Override
    public List<Estudiante> listar() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes WHERE activo = true ORDER BY apellido, nombre";
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

    public List<Estudiante> listarPorCursoProfesorId(int profesorId) {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT e.* FROM estudiantes e " +
                     "JOIN matriculas m ON e.id_estudiante = m.id_estudiante " +
                     "JOIN cursos c ON m.curso_id = c.id " +
                     "WHERE c.id_profesor = ? AND e.activo = true AND m.activo = true " +
                     "ORDER BY e.apellido, e.nombre";
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

    @Override
    public Estudiante obtenerPorId(int id) {
        String sql = "SELECT * FROM estudiantes WHERE id_estudiante = ?";
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
    public void guardar(Estudiante e) {
        String sql = "INSERT INTO estudiantes (nombre, apellido, correo, telefono, direccion, fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, e.getNombre());
                ps.setString(2, e.getApellido());
                ps.setString(3, e.getCorreo());
                ps.setString(4, e.getTelefono());
                ps.setString(5, e.getDireccion());
                ps.setDate(6, e.getFechaNacimiento() != null ? Date.valueOf(e.getFechaNacimiento()) : null);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actualizar(Estudiante e) {
        String sql = "UPDATE estudiantes SET nombre = ?, apellido = ?, correo = ?, telefono = ?, direccion = ?, fecha_nacimiento = ? WHERE id_estudiante = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, e.getNombre());
                ps.setString(2, e.getApellido());
                ps.setString(3, e.getCorreo());
                ps.setString(4, e.getTelefono());
                ps.setString(5, e.getDireccion());
                ps.setDate(6, e.getFechaNacimiento() != null ? Date.valueOf(e.getFechaNacimiento()) : null);
                ps.setInt(7, e.getId());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "UPDATE estudiantes SET activo = false WHERE id_estudiante = ?";
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

    private Estudiante mapear(ResultSet rs) throws SQLException {
        Estudiante e = new Estudiante();
        e.setId(rs.getInt("id_estudiante"));
        e.setNombre(rs.getString("nombre"));
        e.setApellido(rs.getString("apellido"));
        e.setCorreo(rs.getString("correo"));
        e.setTelefono(rs.getString("telefono"));
        e.setDireccion(rs.getString("direccion"));
        Date fn = rs.getDate("fecha_nacimiento");
        if (fn != null) e.setFechaNacimiento(fn.toLocalDate());
        e.setActivo(rs.getBoolean("activo"));
        return e;
    }
}
