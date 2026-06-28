package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.Profesor;

public class ProfesorDAO implements IDAO<Profesor> {

    @Override
    public List<Profesor> listar() {
        List<Profesor> lista = new ArrayList<>();
        String sql = "SELECT * FROM profesores WHERE activo = true ORDER BY apellido, nombre";
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

    @Override
    public Profesor obtenerPorId(int id) {
        String sql = "SELECT * FROM profesores WHERE id_profesor = ?";
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
    public void guardar(Profesor p) {
        String sql = "INSERT INTO profesores (nombre, apellido, correo, telefono, especialidad) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getApellido());
                ps.setString(3, p.getCorreo());
                ps.setString(4, p.getTelefono());
                ps.setString(5, p.getEspecialidad());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Profesor p) {
        String sql = "UPDATE profesores SET nombre = ?, apellido = ?, correo = ?, telefono = ?, especialidad = ? WHERE id_profesor = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getApellido());
                ps.setString(3, p.getCorreo());
                ps.setString(4, p.getTelefono());
                ps.setString(5, p.getEspecialidad());
                ps.setInt(6, p.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "UPDATE profesores SET activo = false WHERE id_profesor = ?";
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

    private Profesor mapear(ResultSet rs) throws SQLException {
        Profesor p = new Profesor();
        p.setId(rs.getInt("id_profesor"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setCorreo(rs.getString("correo"));
        p.setTelefono(rs.getString("telefono"));
        p.setEspecialidad(rs.getString("especialidad"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
}
