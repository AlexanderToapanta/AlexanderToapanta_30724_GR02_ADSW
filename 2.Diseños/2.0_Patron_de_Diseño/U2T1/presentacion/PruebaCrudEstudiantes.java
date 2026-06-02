package U2T1.presentacion;

import U2T1.logica.ControlEstudiante;

public class PruebaCrudEstudiantes {
    public static void main(String[] args) {
        ControlEstudiante control = new ControlEstudiante();

        System.out.println("=== RF-01 Registrar ===");
        System.out.println(control.agregarEstudiante(1, "Ana Torres", 20));
        System.out.println(control.agregarEstudiante(2, "Luis Perez", 17));

        System.out.println("\n=== RF-04 Consultar ===");
        control.listarTodos().forEach(System.out::println);

        System.out.println("\n=== RF-02 Actualizar ===");
        System.out.println(control.actualizarEstudiante(1, "Ana Torres Actualizada", 21));

        System.out.println("\n=== RF-03 Eliminar ===");
        System.out.println(control.eliminarEstudiante(2));
        System.out.println(control.eliminarEstudiante(1));

        System.out.println("\n=== Lista final ===");
        control.listarTodos().forEach(System.out::println);
    }
}
