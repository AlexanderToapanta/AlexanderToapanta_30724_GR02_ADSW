package Implementacion.logica;

import Implementacion.datos.RepositorioEstudiante;
import Implementacion.modelo.Estudiante;
import Implementacion.modelo.IPrototipoEstudiante;
import java.util.List;

/**
 * CAPA: Logica de Negocio
 * Responsabilidad: Centraliza las validaciones, reglas academicas y coordina
 * las operaciones CRUD entre la capa de Presentacion y la capa de Datos.
 * Patrones aplicados: Builder (agregar via construccion paso a paso),
 *                     Prototype (agregar via clonacion de plantilla).
 * Requisitos cubiertos: RF-01, RF-02, RF-03, RF-04, RF-05
 */
public class ControlEstudiante {
    private final RepositorioEstudiante repo = new RepositorioEstudiante();
    private IPrototipoEstudiante estudiantePrototipo;

    public ControlEstudiante(IPrototipoEstudiante prototipo) {
        this.estudiantePrototipo = prototipo;
    }

    // RF-01: CREATE usando Builder
    public String agregarConBuilder(int id, String nombre, int edad) {
        if (!validarDatos(id, nombre, edad)) {
            return "Error: Datos invalidos. ID y Edad deben ser mayores a 0, Nombre no puede estar vacio.";
        }
        if (repo.existeId(id)) {
            return "Error: Ya existe un estudiante con el ID " + id + ".";
        }

        Estudiante nuevo = new EstudianteBuilder()
                .conId(id)
                .conNombre(nombre)
                .conEdad(edad)
                .build();

        repo.guardar(nuevo);
        return "Agregado exitosamente (Via Builder): " + nuevo.getNombre();
    }

    // RF-01: CREATE usando Prototype
    public String agregarConPrototipo(int id, String nombre, int edad) {
        if (!validarDatos(id, nombre, edad)) {
            return "Error: Datos invalidos. ID y Edad deben ser mayores a 0, Nombre no puede estar vacio.";
        }
        if (repo.existeId(id)) {
            return "Error: Ya existe un estudiante con el ID " + id + ".";
        }

        Estudiante clon = estudiantePrototipo.clonar();
        clon.setId(id);
        clon.setNombre(nombre);
        clon.setEdad(edad);

        repo.guardar(clon);
        return "Agregado exitosamente (Via Prototype): " + clon.getNombre();
    }

    // RF-04: READ
    public List<Estudiante> listarTodos() {
        return repo.listarTodos();
    }

    // RF-02: UPDATE — usa clonacion como borrador antes de confirmar cambios
    public String actualizarEstudiante(int id, String nuevoNombre, int nuevaEdad) {
        Estudiante original = repo.buscarPorId(id);
        if (original == null) {
            return "Error: No existe un estudiante con el ID " + id + ".";
        }
        if (!validarDatos(id, nuevoNombre, nuevaEdad)) {
            return "Error: Datos invalidos para la actualizacion.";
        }

        Estudiante borrador = original.clonar();
        borrador.setNombre(nuevoNombre);
        borrador.setEdad(nuevaEdad);

        repo.actualizar(borrador);
        return "Actualizado exitosamente: ID " + id;
    }

    // RF-03: DELETE — aplica regla academica: no se puede eliminar un estudiante menor de 18 anios
    public String eliminarEstudiante(int id) {
        Estudiante estudiante = repo.buscarPorId(id);
        if (estudiante == null) {
            return "Error: No existe un estudiante con el ID " + id + ".";
        }
        if (!cumpleReglaEliminacion(estudiante)) {
            return "Error: Regla academica — no se puede eliminar a '"
                    + estudiante.getNombre() + "' porque es menor de 18 anios (Edad: "
                    + estudiante.getEdad() + ").";
        }
        repo.eliminar(id);
        return "Eliminado exitosamente: " + estudiante.getNombre() + " (ID " + id + ")";
    }

    // RF-05: Validacion de datos obligatorios antes de guardar o actualizar
    public boolean validarDatos(int id, String nombre, int edad) {
        return id > 0 && nombre != null && !nombre.trim().isEmpty() && edad > 0;
    }

    // Regla academica (RF-03): solo se pueden eliminar estudiantes mayores o iguales a 18 anios
    private boolean cumpleReglaEliminacion(Estudiante e) {
        return e.getEdad() >= 18;
    }
}
