package app.model;

/**
 * REQ: Clase Estudiante (Entity)
 * Atributos: id:int, nombre:String, edad:int
 */
public class Estudiante {
    private int id;
    private String nombre;
    private int edad;

    // Constructor requerido por la especificación
    public Estudiante(int id, String nombre, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
    }

    // Getters solicitados
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    // Setter auxiliar (no solicitado explícitamente, pero útil internamente)
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return "Estudiante{" + "id=" + id + ", nombre='" + nombre + '\'' + ", edad=" + edad + '}';
    }
}
