package DAO;

import java.util.List;

public interface IDAO<T> {
    List<T> listar();
    T obtenerPorId(int id);
    void guardar(T t);
    void actualizar(T t);
    void eliminar(int id);
}
