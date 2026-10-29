package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controlador.Delegado;
import modelo.Pedido;
import modelo.DetallePedido;

public class VentanaReportes extends JFrame {

    private Delegado delegado;
    private JLabel lblResumen;
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloPedidos;

    public VentanaReportes() {
        delegado = new Delegado();

        setTitle("Reportes de ventas - El Buen Sabor");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        crearInterfaz();
        actualizarReportes();
    }

    private void crearInterfaz() {
        lblResumen = new JLabel("", SwingConstants.CENTER);
        lblResumen.setFont(new Font("Arial", Font.BOLD, 15));

        modeloProductos = new DefaultTableModel(
            new String[]{"Producto", "Cantidad vendida"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        modeloPedidos = new DefaultTableModel(
            new String[]{"Número", "Fecha", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tablaProductos = new JTable(modeloProductos);
        JTable tablaPedidos = new JTable(modeloPedidos);

        JPanel panelCentro = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel panelProductos = new JPanel(new BorderLayout());
        panelProductos.add(
            new JLabel("PRODUCTOS MAS VENDIDOS"),
            BorderLayout.NORTH
        );
        panelProductos.add(new JScrollPane(tablaProductos));

        JPanel panelPedidos = new JPanel(new BorderLayout());
        panelPedidos.add(
            new JLabel("HISTORIAL DE PEDIDOS"),
            BorderLayout.NORTH
        );
        panelPedidos.add(new JScrollPane(tablaPedidos));

        panelCentro.add(panelProductos);
        panelCentro.add(panelPedidos);

        JButton btnActualizar = new JButton("Actualizar reportes");
        btnActualizar.addActionListener(e -> actualizarReportes());

        setLayout(new BorderLayout(10, 10));
        add(lblResumen, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
        add(btnActualizar, BorderLayout.SOUTH);
    }

    private void actualizarReportes() {
        modeloProductos.setRowCount(0);
        modeloPedidos.setRowCount(0);

        int pedidosHoy = 0;
        double ventasHoy = 0;

        Map<String, Integer> cantidades = new HashMap<>();
        Map<String, String> nombres = new HashMap<>();

        String hoy = LocalDate.now().toString();

        for (Pedido pedido : delegado.getPedidos()) {

            double total = pedido.calcularTotal();

            modeloPedidos.addRow(new Object[]{
                pedido.getNumero(),
                pedido.getFecha(),
                String.format("$%.2f", total)
            });

            if (pedido.getFecha().equals(hoy)) {
                pedidosHoy++;
                ventasHoy += total;
            }

            for (DetallePedido detalle : pedido.getDetalles()) {
                String codigo = detalle.getProducto().getCodigo();

                cantidades.put(
                    codigo,
                    cantidades.getOrDefault(codigo, 0)
                        + detalle.getCantidad()
                );

                nombres.put(
                    codigo,
                    detalle.getProducto().getNombre()
                );
            }
        }

        // Ordenar los productos de mayor a menor cantidad vendida.
        ArrayList<Map.Entry<String, Integer>> lista =
            new ArrayList<>(cantidades.entrySet());

        lista.sort((a, b) ->
            Integer.compare(b.getValue(), a.getValue())
        );

        // Mostrar los productos ordenados en la tabla.
        for (Map.Entry<String, Integer> entrada : lista) {
            String codigo = entrada.getKey();
            int cantidadVendida = entrada.getValue();

            modeloProductos.addRow(new Object[]{
                nombres.get(codigo),
                cantidadVendida
            });
        }

        lblResumen.setText(
            "VENTAS DE HOY (" + hoy + ") | Pedidos: "
            + pedidosHoy + " | Total vendido: "
            + String.format("$%.2f", ventasHoy)
        );
    }
}