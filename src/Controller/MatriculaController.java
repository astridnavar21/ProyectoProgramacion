package Controller;

import DAO.MatriculaDAO;
import Model.Curso;
import Model.Matricula;

import java.util.List;

public class MatriculaController {
    private final MatriculaDAO dao;
    private final CursoController cursoController;

    public MatriculaController(CursoController cursoController) {
        this.dao = new MatriculaDAO();
        this.cursoController = cursoController;
    }

    public List<Matricula> listar() {
        return dao.listar();
    }

    public List<Matricula> listarPorEstudiante(int estudianteId) {
        return dao.listarPorEstudianteId(estudianteId);
    }

    public List<Matricula> listarPorCursoProfesor(int profesorId) {
        return dao.listarPorCursoProfesorId(profesorId);
    }

    public Matricula obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }

    public int contarMatriculados(int cursoId) {
        return dao.contarMatriculados(cursoId);
    }

    public String guardar(Matricula m) {
        if (m.getEstudianteId() <= 0 || m.getCursoId() <= 0) {
            return "Debe seleccionar un estudiante y un curso.";
        }

        if (dao.existeMatricula(m.getEstudianteId(), m.getCursoId())) {
            return "El estudiante ya está matriculado en este curso.";
        }

        Curso curso = cursoController.obtenerPorId(m.getCursoId());
        if (curso != null) {
            int matriculados = dao.contarMatriculados(m.getCursoId());
            if (matriculados >= curso.getCupoMaximo()) {
                return "El curso ha alcanzado su cupo máximo (" + curso.getCupoMaximo() + ").";
            }
        }
        dao.guardar(m);
        return null;
    }

    public String actualizar(Matricula m) {
        if (m.getEstudianteId() <= 0 || m.getCursoId() <= 0) {
            return "Debe seleccionar un estudiante y un curso.";
        }
        dao.actualizar(m);
        return null;
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}
