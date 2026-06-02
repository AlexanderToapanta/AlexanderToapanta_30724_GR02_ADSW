package Implementacion.logica;

import Implementacion.modelo.Estudiante;

/**
 * Adapter: convierte una fuente externa de texto a la entidad interna Estudiante.
 * Formatos aceptados: "id;nombre;edad" o "id,nombre,edad".
 */
public class AdaptadorTextoEstudiante implements AdaptadorEntradaEstudiante {
    @Override
    public Estudiante adaptar(String entrada) {
        if (entrada == null || entrada.trim().isEmpty()) {
            throw new IllegalArgumentException("Entrada vacia. Use el formato id;nombre;edad.");
        }

        String[] partes = entrada.trim().split("[;,]");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato invalido. Use id;nombre;edad o id,nombre,edad.");
        }

        int id = Integer.parseInt(partes[0].trim());
        String nombre = partes[1].trim();
        int edad = Integer.parseInt(partes[2].trim());
        return new Estudiante(id, nombre, edad);
    }
}
