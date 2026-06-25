package Controller;

import DAO.UsuarioDAO;
import Model.Usuario;

import java.util.List;

public class UsuarioController {
    private final UsuarioDAO dao;

    public UsuarioController() {
        this.dao = new UsuarioDAO();
    }

    public Usuario autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty()) return null;
        if (password == null || password.trim().isEmpty()) return null;
        return dao.autenticar(username.trim(), password);
    }

    public List<Usuario> listar() {
        return dao.listar();
    }

    public Usuario obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }

    public String guardar(Usuario u) {
        if (u.getUsuario() == null || u.getUsuario().trim().isEmpty()) {
            return "El nombre de usuario es obligatorio";
        }
        if (u.getContrasena() == null || u.getContrasena().trim().isEmpty()) {
            return "La contraseña es obligatoria";
        }
        dao.guardar(u);
        return null;
    }

    public String actualizar(Usuario u) {
        if (u.getUsuario() == null || u.getUsuario().trim().isEmpty()) {
            return "El nombre de usuario es obligatorio.";
        }
        dao.actualizar(u);
        return null;
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}
