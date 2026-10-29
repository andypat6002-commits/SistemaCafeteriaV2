package vista;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {

        setTitle("Cafeteria El Buen Sabor");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titulo = new JLabel(
            "SISTEMA DE GESTION DE PEDIDOS",
            SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JButton btnProductos = new JButton("Gestion de productos");
        JButton btnPedidos = new JButton("Registrar pedidos");
        JButton btnReportes = new JButton("Reportes de ventas");
        JButton btnSalir = new JButton("Salir");

        btnProductos.addActionListener(e -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });

        btnPedidos.addActionListener(e -> {
            VentanaPedidos ventana = new VentanaPedidos();
            ventana.setVisible(true);
        });

        btnReportes.addActionListener(e -> {
            VentanaReportes ventana = new VentanaReportes();
            ventana.setVisible(true);
        });

        btnSalir.addActionListener(e -> System.exit(0));

        JPanel panelBotones = new JPanel(
            new GridLayout(4, 1, 10, 10)
        );

        panelBotones.setBorder(
            BorderFactory.createEmptyBorder(20, 60, 20, 60)
        );

        panelBotones.add(btnProductos);
        panelBotones.add(btnPedidos);
        panelBotones.add(btnReportes);
        panelBotones.add(btnSalir);

        setLayout(new BorderLayout(10, 10));
        add(titulo, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
    }
}