
package cafeteria;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArchivoDatos {

    private final String archivoProductos = "productos.txt";
    private final String archivoVentas = "ventas.txt";

    public void guardarProductos(ArrayList<Producto> productos) {
        try (BufferedWriter escritor =
                new BufferedWriter(new FileWriter(archivoProductos))) {

            for (Producto producto : productos) {
                escritor.write(
                    producto.getCodigo() + ";" +
                    producto.getNombre() + ";" +
                    producto.getPrecio() + ";" +
                    producto.getStock()
                );
                escritor.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error al guardar productos: " + e.getMessage());
        }
    }

    public void cargarProductos(GestionProductos gestionProductos) {
        File archivo = new File(archivoProductos);

        if (!archivo.exists()) {
            return;
        }

        try (BufferedReader lector =
                new BufferedReader(new FileReader(archivoProductos))) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(";");

                if (datos.length == 4) {
                    String codigo = datos[0];
                    String nombre = datos[1];
                    double precio = Double.parseDouble(datos[2]);
                    int stock = Integer.parseInt(datos[3]);

                    Producto producto =
                            new Producto(codigo, nombre, precio, stock);

                    gestionProductos.registrarProducto(producto);
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
        }
    }

    public void guardarVenta(Pedido pedido) {
        try (BufferedWriter escritor =
                new BufferedWriter(new FileWriter(archivoVentas, true))) {

            StringBuilder detalles = new StringBuilder();

            for (DetallePedido detalle : pedido.getDetalles()) {
                if (detalles.length() > 0) {
                    detalles.append("|");
                }

                detalles.append(detalle.getProducto().getCodigo())
                        .append(":")
                        .append(detalle.getCantidad());
            }

            escritor.write(
                pedido.getNumero() + ";" +
                pedido.getFecha() + ";" +
                pedido.calcularTotal() + ";" +
                detalles
            );

            escritor.newLine();

        } catch (IOException e) {
            System.out.println("Error al guardar la venta: " + e.getMessage());
        }
    }

    public void generarReporteDiario() {
        File archivo = new File(archivoVentas);

        if (!archivo.exists()) {
            System.out.println("Todavía no hay ventas registradas.");
            return;
        }

        double totalVentas = 0;
        int cantidadPedidos = 0;
        Map<String, Integer> productosVendidos = new HashMap<>();

        System.out.println("\n--- REPORTE DE VENTAS DEL DÍA ---");

        try (BufferedReader lector =
                new BufferedReader(new FileReader(archivoVentas))) {

            String linea;
            LocalDate hoy = LocalDate.now();

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(";", -1);

                if (datos.length != 4) {
                    continue;
                }

                LocalDate fechaVenta =
                        java.time.LocalDateTime.parse(datos[1]).toLocalDate();

                if (fechaVenta.equals(hoy)) {
                    double total = Double.parseDouble(datos[2]);

                    totalVentas += total;
                    cantidadPedidos++;

                    System.out.println(
                        "Pedido N.º " + datos[0] +
                        " | Total: $" + String.format("%.2f", total)
                    );

                    if (!datos[3].isEmpty()) {
                        String[] detalles = datos[3].split("\\|");

                        for (String detalle : detalles) {
                            String[] partes = detalle.split(":");

                            if (partes.length == 2) {
                                String codigo = partes[0];
                                int cantidad = Integer.parseInt(partes[1]);

                                productosVendidos.put(
                                    codigo,
                                    productosVendidos.getOrDefault(codigo, 0)
                                            + cantidad
                                );
                            }
                        }
                    }
                }
            }

        } catch (IOException | NumberFormatException |
                 java.time.format.DateTimeParseException e) {
            System.out.println("Error al generar el reporte: " + e.getMessage());
            return;
        }

        System.out.println("Cantidad de pedidos: " + cantidadPedidos);
        System.out.println("Ingresos del día: $" +
                String.format("%.2f", totalVentas));

        if (productosVendidos.isEmpty()) {
            System.out.println("No hay productos vendidos hoy.");
        } else {
            String masVendido = "";
            int mayorCantidad = 0;

            for (Map.Entry<String, Integer> entrada :
                    productosVendidos.entrySet()) {

                System.out.println(
                    "Producto " + entrada.getKey() +
                    " | Unidades vendidas: " + entrada.getValue()
                );

                if (entrada.getValue() > mayorCantidad) {
                    mayorCantidad = entrada.getValue();
                    masVendido = entrada.getKey();
                }
            }

            System.out.println("Producto más vendido: " + masVendido
                    + " (" + mayorCantidad + " unidades)");
        }
    }
}
