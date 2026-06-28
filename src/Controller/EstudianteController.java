package Controller;

import DAO.EstudianteDAO;
import Model.Estudiante;

import java.util.List;

public class EstudianteController {
    private final EstudianteDAO dao;

    public EstudianteController() {
        this.dao = new EstudianteDAO();
    }

    public List<Estudiante> listar() {
        return dao.listar();
    }

    public List<Estudiante> listarPorCursoProfesor(int profesorId) {
        return dao.listarPorCursoProfesorId(profesorId);
    }

    public Estudiante obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }

    public String guardar(Estudiante e) {
        if (e.getNombre() == null || e.getNombre().trim().isEmpty()) return "El nombre es obligatorio.";
        if (e.getApellido() == null || e.getApellido().trim().isEmpty()) return "El apellido es obligatorio.";
        dao.guardar(e);
        return null;
    }

    public String actualizar(Estudiante e) {
        if (e.getNombre() == null || e.getNombre().trim().isEmpty()) return "El nombre es obligatorio.";
        if (e.getApellido() == null || e.getApellido().trim().isEmpty()) return "El apellido es obligatorio.";
        dao.actualizar(e);
        return null;
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}
