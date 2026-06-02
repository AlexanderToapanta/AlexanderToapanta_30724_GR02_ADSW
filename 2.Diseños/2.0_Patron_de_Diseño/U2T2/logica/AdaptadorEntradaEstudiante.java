package Implementacion.logica;

import Implementacion.datos.EstudianteExterno;
import Implementacion.modelo.Estudiante;

public interface AdaptadorEntradaEstudiante {
    Estudiante adaptar(EstudianteExterno entrada);
}
