package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import controlador.Delegado;
import modelo.Producto;
import modelo.Pedido;
import modelo.DetallePedido;

public class VentanaPedidos extends JFrame {

    private Delegado delegado;
    private JComboBox<Producto> comboProductos;
    private JTextField txtCantidad;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JLabel lblTotal;
    private ArrayList<DetallePedido> detalles;

    public VentanaPedidos() {
        delegado = new Delegado();
        detalles = new ArrayList<>();

        setTitle("Registro de pedidos");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        crearInterfaz();
    }

    private void crearInterfaz() {
        JPanel superior = new JPanel();

        comboProductos = new JComboBox<>();

        for (Producto p : delegado.getProductos()) {
            comboProductos.addItem(p);
        }

        txtCantidad = new JTextField(5);

        JButton btnAgregar = new JButton("Agregar producto");

        superior.add(new JLabel("Producto:"));
        superior.add(comboProductos);
        superior.add(new JLabel("Cantidad:"));
        superior.add(txtCantidad);
        superior.add(btnAgregar);

        modeloTabla = new DefaultTableModel(
            new String[]{"Producto", "Cantidad", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        lblTotal = new JLabel(
            "Subtotal: $0.00 | IVA: $0.00 | Total: $0.00"
        );

        JButton btnRegistrar = new JButton("Registrar pedido");

        btnAgregar.addActionListener(e -> agregarProducto());
        btnRegistrar.addActionListener(e -> registrarPedido());

        JPanel inferior = new JPanel();
        inferior.add(lblTotal);
        inferior.add(btnRegistrar);

        setLayout(new BorderLayout(10, 10));
        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);
    }

    private void agregarProducto() {
        try {
            Producto producto =
                (Producto) comboProductos.getSelectedItem();

            int cantidad = Integer.parseInt(
                txtCantidad.getText().trim()
            );

            if (producto == null || cantidad <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un producto y escriba una cantidad valida."
                );
                return;
            }

            // Sumar la cantidad que ya se agregó de este producto.
            int cantidadAcumulada = cantidad;

            for (DetallePedido detalle : detalles) {
                if (detalle.getProducto().getCodigo()
                        .equalsIgnoreCase(producto.getCodigo())) {
                    cantidadAcumulada += detalle.getCantidad();
                }
            }

            // Comprobar el stock considerando todas las cantidades.
            if (cantidadAcumulada > producto.getStock()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Stock insuficiente. Ya agregaste "
                    + (cantidadAcumulada - cantidad)
                    + " unidades de este producto y solo hay "
                    + producto.getStock() + " disponibles."
                );
                return;
            }

            DetallePedido detalleNuevo =
                new DetallePedido(producto, cantidad);

            detalles.add(detalleNuevo);

            modeloTabla.addRow(new Object[]{
                producto.getNombre(),
                cantidad,
                String.format("$%.2f", detalleNuevo.calcularSubtotal())
            });

            actualizarTotal();
            txtCantidad.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "La cantidad debe ser un numero entero."
            );
        }
    }

    private void actualizarTotal() {
        double subtotal = 0;

        for (DetallePedido detalle : detalles) {
            subtotal += detalle.calcularSubtotal();
        }

        double iva = subtotal * 0.15;
        double total = subtotal + iva;

        lblTotal.setText(
            String.format(
                "Subtotal: $%.2f | IVA: $%.2f | Total: $%.2f",
                subtotal, iva, total
            )
        );
    }

    private void registrarPedido() {
        if (detalles.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Agregue al menos un producto."
            );
            return;
        }

        // Volver a comprobar las cantidades antes de registrar.
        for (Producto producto : delegado.getProductos()) {
            int cantidadNecesaria = 0;

            for (DetallePedido detalle : detalles) {
                if (detalle.getProducto().getCodigo()
                        .equalsIgnoreCase(producto.getCodigo())) {
                    cantidadNecesaria += detalle.getCantidad();
                }
            }

            if (cantidadNecesaria > producto.getStock()) {
                JOptionPane.showMessageDialog(
                    this,
                    "No hay suficiente stock de "
                    + producto.getNombre() + "."
                );
                return;
            }
        }

        String numero = "PED-" + (delegado.getPedidos().size() + 1);
        String fecha = java.time.LocalDate.now().toString();

        Pedido pedido = new Pedido(numero, fecha);

        for (DetallePedido detalle : detalles) {
            pedido.agregarDetalle(detalle);

            Producto producto = detalle.getProducto();

            producto.setStock(
                producto.getStock() - detalle.getCantidad()
            );
        }

        delegado.registrarPedido(pedido);

        JOptionPane.showMessageDialog(
            this,
            "Pedido registrado correctamente. Total: $"
            + String.format("%.2f", pedido.calcularTotal())
        );

        detalles.clear();
        modeloTabla.setRowCount(0);
        actualizarTotal();
    }
}