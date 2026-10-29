package modelo;

import java.io.*;
import java.util.ArrayList;

public class ArchivoDatos {

    private static final String ARCHIVO_PRODUCTOS = "productos.txt";
    private static final String ARCHIVO_VENTAS = "ventas.txt";

    public static void guardarProductos(ArrayList<Producto> productos) {
        try (PrintWriter salida = new PrintWriter(
                new FileWriter(ARCHIVO_PRODUCTOS))) {

            for (Producto p : productos) {
                salida.println(
                    p.getCodigo() + ";" +
                    p.getNombre() + ";" +
                    p.getPrecio() + ";" +
                    p.getStock()
                );
            }

        } catch (IOException e) {
            System.out.println("Error al guardar productos: " + e.getMessage());
        }
    }

    public static ArrayList<Producto> cargarProductos() {
        ArrayList<Producto> productos = new ArrayList<>();
        File archivo = new File(ARCHIVO_PRODUCTOS);

        if (!archivo.exists()) {
            return productos;
        }

        try (BufferedReader entrada = new BufferedReader(
                new FileReader(archivo))) {

            String linea;

            while ((linea = entrada.readLine()) != null) {
                String[] datos = linea.split(";", -1);

                if (datos.length == 4) {
                    Producto p = new Producto(
                        datos[0],
                        datos[1],
                        Double.parseDouble(datos[2]),
                        Integer.parseInt(datos[3])
                    );

                    productos.add(p);
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
        }

        return productos;
    }

    public static void guardarPedidos(ArrayList<Pedido> pedidos) {
        try (PrintWriter salida = new PrintWriter(
                new FileWriter(ARCHIVO_VENTAS))) {

            for (Pedido pedido : pedidos) {
                StringBuilder linea = new StringBuilder();
                linea.append(pedido.getNumero())
                     .append(";")
                     .append(pedido.getFecha())
                     .append(";");

                for (int i = 0; i < pedido.getDetalles().size(); i++) {
                    DetallePedido d = pedido.getDetalles().get(i);

                    if (i > 0) {
                        linea.append(",");
                    }

                    linea.append(d.getProducto().getCodigo())
                         .append(":")
                         .append(d.getCantidad());
                }

                salida.println(linea);
            }

        } catch (IOException e) {
            System.out.println("Error al guardar ventas: " + e.getMessage());
        }
    }

    public static ArrayList<Pedido> cargarPedidos(
            ArrayList<Producto> productos) {

        ArrayList<Pedido> pedidos = new ArrayList<>();
        File archivo = new File(ARCHIVO_VENTAS);

        if (!archivo.exists()) {
            return pedidos;
        }

        try (BufferedReader entrada = new BufferedReader(
                new FileReader(archivo))) {

            String linea;

            while ((linea = entrada.readLine()) != null) {
                String[] datos = linea.split(";", -1);

                if (datos.length != 3) {
                    continue;
                }

                Pedido pedido = new Pedido(datos[0], datos[1]);

                if (!datos[2].isEmpty()) {
                    String[] detalles = datos[2].split(",");

                    for (String textoDetalle : detalles) {
                        String[] partes = textoDetalle.split(":");

                        if (partes.length != 2) {
                            continue;
                        }

                        String codigo = partes[0];
                        int cantidad = Integer.parseInt(partes[1]);

                        for (Producto p : productos) {
                            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                                pedido.agregarDetalle(
                                    new DetallePedido(p, cantidad)
                                );
                                break;
                            }
                        }
                    }
                }

                pedidos.add(pedido);
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar ventas: " + e.getMessage());
        }

        return pedidos;
    }
}