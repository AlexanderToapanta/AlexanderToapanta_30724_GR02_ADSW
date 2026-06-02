package U2T1.logica;

import U2T1.datos.RepositorioEstudiante;
import U2T1.modelo.Estudiante;
import java.util.List;

/**
 * CAPA: Logica de negocio
 * Responsabilidad: validar datos, aplicar reglas academicas y coordinar el CRUD.
 */
public class ControlEstudiante {
    private final RepositorioEstudiante repo = new RepositorioEstudiante();

    // RF-01: Registrar estudiante
    public String agregarEstudiante(int id, String nombre, int edad) {
        if (!validarDatos(id, nombre, edad)) {
            return "Error: Datos invalidos. ID y Edad deben ser mayores a 0, Nombre no puede estar vacio.";
        }
        if (repo.existeId(id)) {
            return "Error: Ya existe un estudiante con el ID " + id + ".";
        }

        Estudiante estudiante = new Estudiante(id, nombre.trim(), edad);
        repo.guardar(estudiante);
        return "Agregado exitosamente: " + estudiante.getNombre();
    }

    // RF-04: Consultar/Listar estudiantes
    public List<Estudiante> listarTodos() {
        return repo.listarTodos();
    }

    // RF-02: Actualizar estudiante
    public String actualizarEstudiante(int id, String nuevoNombre, int nuevaEdad) {
        if (repo.buscarPorId(id) == null) {
            return "Error: No existe un estudiante con el ID " + id + ".";
        }
        if (!validarDatos(id, nuevoNombre, nuevaEdad)) {
            return "Error: Datos invalidos para la actualizacion.";
        }

        repo.actualizar(new Estudiante(id, nuevoNombre.trim(), nuevaEdad));
        return "Actualizado exitosamente: ID " + id;
    }

    // RF-03: Eliminar estudiante
    public String eliminarEstudiante(int id) {
        Estudiante estudiante = repo.buscarPorId(id);
        if (estudiante == null) {
            return "Error: No existe un estudiante con el ID " + id + ".";
        }
        if (!cumpleReglaEliminacion(estudiante)) {
            return "Error: Regla academica - no se puede eliminar a '"
                    + estudiante.getNombre() + "' porque es menor de 18 anios (Edad: "
                    + estudiante.getEdad() + ").";
        }

        repo.eliminar(id);
        return "Eliminado exitosamente: " + estudiante.getNombre() + " (ID " + id + ")";
    }

    // RF-05: Validar datos obligatorios
    public boolean validarDatos(int id, String nombre, int edad) {
        return id > 0 && nombre != null && !nombre.trim().isEmpty() && edad > 0;
    }

    private boolean cumpleReglaEliminacion(Estudiante estudiante) {
        return estudiante.getEdad() >= 18;
    }
}
