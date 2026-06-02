package Implementacion.logica;

import Implementacion.datos.EstudianteExterno;
import Implementacion.modelo.Estudiante;

/**
 * Adapter: convierte el formato externo codigo/nombreCompleto/anios
 * al modelo interno Estudiante id/nombre/edad.
 */
public class AdaptadorEstudianteExterno implements AdaptadorEntradaEstudiante {
    @Override
    public Estudiante adaptar(EstudianteExterno entrada) {
        if (entrada == null) {
            throw new IllegalArgumentException("La entrada externa no puede ser nula.");
        }
        return new Estudiante(
                entrada.getCodigo(),
                entrada.getNombreCompleto(),
                entrada.getAnios()
        );
    }
}
