package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import controlador.Delegado;
import modelo.Producto;

public class VentanaPrincipal extends JFrame {

    private Delegado delegado;
    private DefaultTableModel modeloTabla;
    private JTable tabla;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;

    public VentanaPrincipal() {
        delegado = new Delegado();

        setTitle("Sistema de Gestion - Cafeteria El Buen Sabor");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        crearInterfaz();
        actualizarTabla();
    }

    private void crearInterfaz() {
        JPanel formulario = new JPanel(new GridLayout(2, 4, 8, 8));

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        txtStock = new JTextField();

        formulario.add(new JLabel("Codigo:"));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(new JLabel("Precio:"));
        formulario.add(new JLabel("Stock:"));

        formulario.add(txtCodigo);
        formulario.add(txtNombre);
        formulario.add(txtPrecio);
        formulario.add(txtStock);

        modeloTabla = new DefaultTableModel(
            new String[]{"Codigo", "Nombre", "Precio", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        // Al seleccionar un producto, mostrar sus datos en el formulario.
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProductoSeleccionado();
            }
        });

        JButton btnRegistrar = new JButton("Registrar producto");
        JButton btnEditar = new JButton("Actualizar producto");
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        JButton btnLimpiar = new JButton("Limpiar campos");

        btnRegistrar.addActionListener(e -> registrarProducto());
        btnEditar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        JPanel botones = new JPanel(new FlowLayout());

        botones.add(btnRegistrar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        setLayout(new BorderLayout(10, 10));
        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void registrarProducto() {
        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (codigo.isEmpty() || nombre.isEmpty()
                    || precio <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(
                    this, "Ingrese datos validos."
                );
                return;
            }

            Producto producto = new Producto(
                codigo, nombre, precio, stock
            );

            if (delegado.registrarProducto(producto)) {
                actualizarTabla();
                limpiarCampos();

                JOptionPane.showMessageDialog(
                    this, "Producto registrado correctamente."
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "Ya existe un producto con ese codigo."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "El precio debe ser decimal y el stock un entero."
            );
        }
    }

    private void cargarProductoSeleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return;
        }

        txtCodigo.setText(
            modeloTabla.getValueAt(fila, 0).toString()
        );

        txtNombre.setText(
            modeloTabla.getValueAt(fila, 1).toString()
        );

        txtPrecio.setText(
            modeloTabla.getValueAt(fila, 2).toString()
        );

        txtStock.setText(
            modeloTabla.getValueAt(fila, 3).toString()
        );

        // El código identifica al producto y no se modifica al editar.
        txtCodigo.setEditable(false);
    }

    private void actualizarProducto() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                this, "Seleccione un producto de la tabla."
            );
            return;
        }

        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (nombre.isEmpty() || precio <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(
                    this, "Ingrese datos validos."
                );
                return;
            }

            boolean actualizado = false;

            for (Producto p : delegado.getProductos()) {
                if (p.getCodigo().equalsIgnoreCase(codigo)) {
                    p.setNombre(nombre);
                    p.setPrecio(precio);
                    p.setStock(stock);
                    actualizado = true;
                    break;
                }
            }

            if (actualizado) {
                modelo.ArchivoDatos.guardarProductos(
                    delegado.getProductos()
                );

                actualizarTabla();
                limpiarCampos();

                JOptionPane.showMessageDialog(
                    this, "Producto actualizado correctamente."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "El precio debe ser decimal y el stock un entero."
            );
        }
    }

    private void eliminarProducto() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                this, "Seleccione un producto de la tabla."
            );
            return;
        }

        String codigo = modeloTabla.getValueAt(fila, 0).toString();

        if (delegado.eliminarProducto(codigo)) {
            actualizarTabla();
            limpiarCampos();

            JOptionPane.showMessageDialog(
                this, "Producto eliminado."
            );
        } else {
            JOptionPane.showMessageDialog(
                this, "No se puede eliminar: el producto ya aparece en un pedido."
            );
        }
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);

        for (Producto p : delegado.getProductos()) {
            modeloTabla.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock()
            });
        }
    }

    private void limpiarCampos() {
        txtCodigo.setEditable(true);
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        tabla.clearSelection();
        txtCodigo.requestFocus();
    }
}