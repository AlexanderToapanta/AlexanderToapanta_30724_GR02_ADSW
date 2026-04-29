package app.repository;

import app.model.Estudiante;
import java.util.*;

/**
 * REQ: Clase Repositorio Estudiante (Repository)
 * Métodos: existeId(id), guardar(estudiante), buscarPorId(id), actualizar(estudiante), eliminar(id), listarTodos()
 * Implementación en memoria usando HashMap para persistencia temporal.
 */
public class RepositorioEstudiante {
    private final Map<Integer, Estudiante> storage = new HashMap<>();

    // Comprueba si un ID existe en el repositorio
    public boolean existeId(int id) {
        return storage.containsKey(id);
    }

    // Guarda un nuevo estudiante (inserción)
    public void guardar(Estudiante estudiante) {
        storage.put(estudiante.getId(), estudiante);
    }

    // Busca un estudiante por su ID
    public Estudiante buscarPorId(int id) {
        return storage.get(id);
    }

    // Actualiza o sobrescribe el estudiante con el mismo ID
    public void actualizar(Estudiante estudiante) {
        storage.put(estudiante.getId(), estudiante);
    }

    // Elimina un estudiante por ID
    public void eliminar(int id) {
        storage.remove(id);
    }

    // Lista todos los estudiantes registrados
    public List<Estudiante> listarTodos() {
        return new ArrayList<>(storage.values());
    }
}
