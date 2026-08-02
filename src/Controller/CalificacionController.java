package Controller;

import DAO.CalificacionDAO;
import Model.Calificacion;

import java.util.List;

public class CalificacionController {
    private final CalificacionDAO dao;

    public CalificacionController() {
        this.dao = new CalificacionDAO();
    }

    public List<Calificacion> listar() {
        return dao.listar();
    }

    public List<Calificacion> listarPorEstudiante(int estudianteId) {
        return dao.listarPorEstudianteId(estudianteId);
    }

    public List<Calificacion> listarPorCursoProfesor(int profesorId) {
        return dao.listarPorCursoProfesorId(profesorId);
    }

    public double calcularPromedio(int estudianteId) {
        return dao.calcularPromedioPorEstudiante(estudianteId);
    }

    public Calificacion obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }

    public String guardar(Calificacion c) {
        if (c.getEstudianteId() <= 0 || c.getCursoId() <= 0) {
            return "Debe seleccionar un estudiante y un curso.";
        }
        if (c.getCalificacion() < 0 || c.getCalificacion() > 100) {
            return "La calificación debe estar entre 0 y 100.";
        }
        dao.guardar(c);
        return null;
    }

    public String actualizar(Calificacion c) {
        if (c.getEstudianteId() <= 0 || c.getCursoId() <= 0) {
            return "Debe seleccionar un estudiante y un curso.";
        }
        if (c.getCalificacion() < 0 || c.getCalificacion() > 100) {
            return "La calificación debe estar entre 0 y 100.";
        }
        dao.actualizar(c);
        return null;
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}
