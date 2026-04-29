package app;

import app.view.FormularioCrudEstudiante;
import java.util.Scanner;

/**
 * Clase Main interactiva para usar la aplicación en consola.
 * Permite al usuario ejecutar operaciones CRUD siguiendo el patrón MVC definido.
 */
public class Main {
    public static void main(String[] args) {
        // Lanzar la interfaz gráfica Swing
        javax.swing.SwingUtilities.invokeLater(() -> new FormularioCrudEstudiante());
    }
}
