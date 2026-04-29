package app.controller;

import app.model.Estudiante;
import app.repository.RepositorioEstudiante;

import java.util.List;

/**
 * REQ: Clase ControlEstudiante (Control)
 * Métodos: agregarEstudiante(id, nombre, edad), actualizarEstudiante(id, nombre, edad),
 * eliminarEstudiante(id), mostrarTodos(), validarDatos(id, nombre, edad)
 *
 * Lógica: validarDatos debe correrse antes de agregar/actualizar. agregar verifica existeId primero.
 */
public class ControlEstudiante {
    private final RepositorioEstudiante repo;

    public ControlEstudiante(RepositorioEstudiante repo) {
        this.repo = repo;
    }

    // Valida los datos mínimos: ID > 0, nombre no vacío, edad razonable (>0)
    public boolean validarDatos(int id, String nombre, int edad) {
        if (id <= 0) return false;
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (edad <= 0) return false;
        return true;
    }

    // Agregar estudiante: valida, verifica existencia, crea y guarda
    public String agregarEstudiante(int id, String nombre, int edad) {
        // REQ: flujo Agregar -> validar -> verificar existencia -> crear -> guardar -> notificar
        if (!validarDatos(id, nombre, edad)) {
            return "ERROR: Datos inválidos";
        }
        if (repo.existeId(id)) {
            return "ERROR: ID ya existe";
        }
        Estudiante est = new Estudiante(id, nombre, edad);
        repo.guardar(est);
        return "OK: Estudiante agregado";
    }

    // Actualizar estudiante: valida, busca, modifica y guarda
    public String actualizarEstudiante(int id, String nombre, int edad) {
        // REQ: flujo Actualizar -> validar -> buscar por ID -> actualizar atributos -> guardar -> notificar
        if (!validarDatos(id, nombre, edad)) {
            return "ERROR: Datos inválidos";
        }
        Estudiante existente = repo.buscarPorId(id);
        if (existente == null) {
            return "ERROR: Estudiante no encontrado";
        }
        // Crear nueva instancia o actualizar existente
        Estudiante actualizado = new Estudiante(id, nombre, edad);
        repo.actualizar(actualizado);
        return "OK: Estudiante actualizado";
    }

    // Eliminar estudiante: buscar por ID -> eliminar
    public String eliminarEstudiante(int id) {
        // REQ: flujo Eliminar -> buscar por ID -> eliminar -> notificar
        Estudiante existente = repo.buscarPorId(id);
        if (existente == null) {
            return "ERROR: Estudiante no encontrado";
        }
        repo.eliminar(id);
        return "OK: Estudiante eliminado";
    }

    // Mostrar todos: solicita listado al repositorio
    public List<Estudiante> mostrarTodos() {
        // REQ: flujo Mostrar -> listarTodos -> retornar lista
        return repo.listarTodos();
    }
}
