package Implementacion.logica;

import Implementacion.modelo.Estudiante;

/**
 * Patron Builder — construye un Estudiante paso a paso validando cada campo.
 * Usado por ControlEstudiante para el caso de uso RF-01 (Agregar via Builder).
 */
public class EstudianteBuilder {
    private int id;
    private String nombre;
    private int edad;

    public EstudianteBuilder conId(int id) {
        this.id = id;
        return this;
    }

    public EstudianteBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public EstudianteBuilder conEdad(int edad) {
        this.edad = edad;
        return this;
    }

    public Estudiante build() {
        if (id <= 0) throw new IllegalArgumentException("ID debe ser mayor que 0");
        if (nombre == null || nombre.isEmpty()) throw new IllegalArgumentException("Nombre invalido");
        if (edad <= 0) throw new IllegalArgumentException("Edad invalida");
        return new Estudiante(id, nombre, edad);
    }
}
