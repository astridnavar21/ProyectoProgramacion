package DAO;

import Model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements IDAO<Usuario> {

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY usuario";
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
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
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

    public Usuario autenticar(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ? AND activo = true";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario obtenerPorProfesorId(int profesorId) {
        String sql = "SELECT * FROM usuarios WHERE profesor_id = ? AND activo = true";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, profesorId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario obtenerPorEstudianteId(int estudianteId) {
        String sql = "SELECT * FROM usuarios WHERE estudiante_id = ? AND activo = true";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, estudianteId);
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
    public void guardar(Usuario u) {
        String sql = "INSERT INTO usuarios (usuario, contrasena, rol, profesor_id, estudiante_id) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, u.getUsuario());
                ps.setString(2, u.getContrasena());
                ps.setString(3, u.getRol());
                if (u.getProfesorId() != null) ps.setInt(4, u.getProfesorId());
                else ps.setNull(4, Types.INTEGER);
                if (u.getEstudianteId() != null) ps.setInt(5, u.getEstudianteId());
                else ps.setNull(5, Types.INTEGER);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET usuario = ?, contrasena = ?, rol = ?, profesor_id = ?, estudiante_id = ?, activo = ? WHERE id_usuario = ?";
        try {
            Connection conn = Conexion.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, u.getUsuario());
                ps.setString(2, u.getContrasena());
                ps.setString(3, u.getRol());
                if (u.getProfesorId() != null) ps.setInt(4, u.getProfesorId());
                else ps.setNull(4, Types.INTEGER);
                if (u.getEstudianteId() != null) ps.setInt(5, u.getEstudianteId());
                else ps.setNull(5, Types.INTEGER);
                ps.setBoolean(6, u.isActivo());
                ps.setInt(7, u.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "UPDATE usuarios SET activo = false WHERE id_usuario = ?";
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

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setUsuario(rs.getString("usuario"));
        u.setContrasena(rs.getString("contrasena"));
        u.setRol(rs.getString("rol"));
        int profId = rs.getInt("profesor_id");
        u.setProfesorId(rs.wasNull() ? null : profId);
        int estId = rs.getInt("estudiante_id");
        u.setEstudianteId(rs.wasNull() ? null : estId);
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}
