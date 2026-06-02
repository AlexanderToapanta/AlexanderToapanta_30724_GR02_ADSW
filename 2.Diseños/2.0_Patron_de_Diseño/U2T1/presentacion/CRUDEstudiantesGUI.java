package U2T1.presentacion;

import U2T1.logica.ControlEstudiante;
import U2T1.modelo.Estudiante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * CAPA: Presentacion
 * Responsabilidad: capturar datos, invocar la logica de negocio y mostrar resultados.
 */
public class CRUDEstudiantesGUI extends JFrame {
    private final ControlEstudiante control = new ControlEstudiante();

    private final JTextField txtId = new JTextField(10);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtEdad = new JTextField(5);
    private final JTextArea areaSalida = new JTextArea(12, 40);

    public CRUDEstudiantesGUI() {
        setTitle("CRUD Estudiantes - Tres Capas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initLayout();
        pack();
        setLocationRelativeTo(null);
    }

    private void initLayout() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("ID:")); form.add(txtId);
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Edad:")); form.add(txtEdad);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnListar = new JButton("Listar Todos");

        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnListar);

        areaSalida.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaSalida);

        panel.add(form, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.CENTER);
        panel.add(scroll, BorderLayout.SOUTH);

        setContentPane(panel);

        btnAgregar.addActionListener(this::onAgregar);
        btnActualizar.addActionListener(this::onActualizar);
        btnEliminar.addActionListener(this::onEliminar);
        btnListar.addActionListener(e -> actualizarLista());
    }

    private void onAgregar(ActionEvent e) {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String nombre = txtNombre.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());
            mostrarMensaje(control.agregarEstudiante(id, nombre, edad));
            actualizarLista();
        } catch (NumberFormatException ex) {
            mostrarMensaje("ID y Edad deben ser numeros enteros.");
        }
    }

    private void onActualizar(ActionEvent e) {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String nombre = txtNombre.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());
            mostrarMensaje(control.actualizarEstudiante(id, nombre, edad));
            actualizarLista();
        } catch (NumberFormatException ex) {
            mostrarMensaje("ID y Edad deben ser numeros enteros.");
        }
    }

    private void onEliminar(ActionEvent e) {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            mostrarMensaje(control.eliminarEstudiante(id));
            actualizarLista();
        } catch (NumberFormatException ex) {
            mostrarMensaje("ID debe ser un numero entero.");
        }
    }

    private void actualizarLista() {
        List<Estudiante> lista = control.listarTodos();
        StringBuilder sb = new StringBuilder("--- LISTA DE ESTUDIANTES ---\n");
        if (lista.isEmpty()) {
            sb.append("(No hay estudiantes registrados)");
        } else {
            for (Estudiante estudiante : lista) {
                sb.append(estudiante).append('\n');
            }
        }
        areaSalida.setText(sb.toString());
    }

    private void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CRUDEstudiantesGUI().setVisible(true));
    }
}
