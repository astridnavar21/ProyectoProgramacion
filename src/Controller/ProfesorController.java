package Controller;

import java.util.List;

import DAO.ProfesorDAO;
import Model.Profesor;

public class ProfesorController {
    private final ProfesorDAO dao;

    public ProfesorController() {
        this.dao = new ProfesorDAO();
    }

    public List<Profesor> listar() {
        return dao.listar();
    }

    public Profesor obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }

    public String guardar(Profesor p) {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) return "El nombre es obligatorio.";
        if (p.getApellido() == null || p.getApellido().trim().isEmpty()) return "El apellido es obligatorio.";
        dao.guardar(p);
        return null;
    }

    public String actualizar(Profesor p) {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) return "El nombre es obligatorio.";
        if (p.getApellido() == null || p.getApellido().trim().isEmpty()) return "El apellido es obligatorio.";
        dao.actualizar(p);
        return null;
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}
