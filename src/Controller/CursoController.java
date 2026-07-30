package Controller;

import DAO.CursoDAO;
import Model.Curso;

import java.util.List;

public class CursoController {
    private final CursoDAO dao;

    public CursoController() {
        this.dao = new CursoDAO();
    }

    public List<Curso> listar() {
        return dao.listar();
    }

    public List<Curso> listarPorProfesor(int profesorId) {
        return dao.listarPorProfesorId(profesorId);
    }

    public List<Curso> listarPorEstudiante(int estudianteId) {
        return dao.listarPorEstudianteId(estudianteId);
    }

    public Curso obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }

    public String guardar(Curso curso) {
        if (curso.getNombre() == null || curso.getNombre().trim().isEmpty()) {
            return "El nombre del curso es obligatorio.";
        }
        if (curso.getCreditos() < 0) {
            return "Los créditos no pueden ser negativos.";
        }
        if (curso.getCupoMaximo() < 1) {
            return "El cupo máximo debe ser al menos 1.";
        }
        dao.guardar(curso);
        return null;
    }

    public String actualizar(Curso curso) {
        if (curso.getNombre() == null || curso.getNombre().trim().isEmpty()) {
            return "El nombre del curso es obligatorio.";
        }
        if (curso.getCreditos() < 0) {
            return "Los créditos no pueden ser negativos.";
        }
        if (curso.getCupoMaximo() < 1) {
            return "El cupo máximo debe ser al menos 1.";
        }
        dao.actualizar(curso);
        return null;
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}
