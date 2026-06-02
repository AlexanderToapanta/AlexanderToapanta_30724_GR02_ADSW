package U2T1.datos;

import U2T1.modelo.Estudiante;
import java.util.ArrayList;
import java.util.List;

/**
 * CAPA: Datos
 * Responsabilidad: almacenar y gestionar la coleccion de estudiantes en memoria.
 * No aplica reglas de negocio ni validaciones.
 */
public class RepositorioEstudiante {
    private final List<Estudiante> estudiantes = new ArrayList<>();

    public boolean existeId(int id) {
        return estudiantes.stream().anyMatch(e -> e.getId() == id);
    }

    public void guardar(Estudiante estudiante) {
        estudiantes.add(estudiante);
    }

    public Estudiante buscarPorId(int id) {
        return estudiantes.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public void actualizar(Estudiante estudiante) {
        Estudiante existente = buscarPorId(estudiante.getId());
        if (existente != null) {
            existente.setNombre(estudiante.getNombre());
            existente.setEdad(estudiante.getEdad());
        }
    }

    public void eliminar(int id) {
        estudiantes.removeIf(e -> e.getId() == id);
    }

    public List<Estudiante> listarTodos() {
        return new ArrayList<>(estudiantes);
    }
}
