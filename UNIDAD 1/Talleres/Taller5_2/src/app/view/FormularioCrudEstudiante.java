package app.view;

import app.controller.ControlEstudiante;
import app.model.Estudiante;
import app.repository.RepositorioEstudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * REQ: Clase FormularioCrudEstudiante (Boundary)
 * Implementación GUI con Swing que respeta las firmas y flujos especificados.
 */
public class FormularioCrudEstudiante extends JFrame implements ActionListener {
    private final JTextField txtId = new JTextField(10);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtEdad = new JTextField(5);

    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnMostrar = new JButton("Mostrar Todo");

    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Edad"}, 0);
    private final JTable table = new JTable(tableModel);

    private final ControlEstudiante control;

    public FormularioCrudEstudiante() {
        super("CRUD Estudiantes - MVC");
        RepositorioEstudiante repo = new RepositorioEstudiante();
        this.control = new ControlEstudiante(repo);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.add(new JLabel("ID:"));
        top.add(txtId);
        top.add(new JLabel("Nombre:"));
        top.add(txtNombre);
        top.add(new JLabel("Edad:"));
        top.add(txtEdad);

        JPanel buttons = new JPanel();
        buttons.add(btnAgregar);
        buttons.add(btnActualizar);
        buttons.add(btnEliminar);
        buttons.add(btnMostrar);

        btnAgregar.addActionListener(this);
        btnActualizar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnMostrar.addActionListener(this);

        add(top, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        setVisible(true);
    }

    // Métodos solicitados en la especificación para compatibilidad (callable por otros componentes)
    public void setTxtId(int id) { txtId.setText(String.valueOf(id)); }
    public void setTxtNombre(String nombre) { txtNombre.setText(nombre); }
    public void setTxtEdad(int edad) { txtEdad.setText(String.valueOf(edad)); }

    public int getTxtId() { return Integer.parseInt(txtId.getText().trim()); }
    public String getTxtNombre() { return txtNombre.getText().trim(); }
    public int getTxtEdad() { return Integer.parseInt(txtEdad.getText().trim()); }

    // Acciones - conectan la vista con el controlador
    public void clickAgregar() {
        try {
            String res = control.agregarEstudiante(getTxtId(), getTxtNombre(), getTxtEdad());
            mostrarMensaje(res);
            if (res != null && res.startsWith("OK")) {
                clickMostrarTodo();
            }
        } catch (NumberFormatException ex) {
            mostrarMensaje("ERROR: Formato de datos inválido");
        }
    }

    public void clickActualizar() {
        try {
            String res = control.actualizarEstudiante(getTxtId(), getTxtNombre(), getTxtEdad());
            mostrarMensaje(res);
            if (res != null && res.startsWith("OK")) {
                clickMostrarTodo();
            }
        } catch (NumberFormatException ex) {
            mostrarMensaje("ERROR: Formato de datos inválido");
        }
    }

    public void clickEliminar() {
        try {
            String res = control.eliminarEstudiante(getTxtId());
            mostrarMensaje(res);
            if (res != null && res.startsWith("OK")) {
                clickMostrarTodo();
            }
        } catch (NumberFormatException ex) {
            mostrarMensaje("ERROR: Formato de datos inválido");
        }
    }

    public void clickMostrarTodo() {
        List<Estudiante> lista = control.mostrarTodos();
        mostrarTabla(lista);
    }

    // Salida: mostrar mensaje al usuario (ventana emergente)
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // Salida: mostrar tabla de estudiantes
    public void mostrarTabla(List<Estudiante> estudiantes) {
        tableModel.setRowCount(0);
        if (estudiantes == null || estudiantes.isEmpty()) {
            // dejar tabla vacía
            return;
        }
        for (Estudiante e : estudiantes) {
            tableModel.addRow(new Object[]{e.getId(), e.getNombre(), e.getEdad()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAgregar) clickAgregar();
        else if (src == btnActualizar) clickActualizar();
        else if (src == btnEliminar) clickEliminar();
        else if (src == btnMostrar) clickMostrarTodo();
    }
}
