
package cafeteria;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static Scanner teclado = new Scanner(System.in);
    private static GestionProductos gestionProductos =
            new GestionProductos();
    private static GestionPedidos gestionPedidos =
            new GestionPedidos();
    private static ArchivoDatos archivoDatos = new ArchivoDatos();

    public static void main(String[] args) {

        archivoDatos.cargarProductos(gestionProductos);

        int opcion;

        do {
            System.out.println("\n===== SISTEMA DE CAFETERÍA =====");
            System.out.println("1. Registrar producto");
            System.out.println("2. Listar productos");
            System.out.println("3. Reponer stock");
            System.out.println("4. Registrar pedido");
            System.out.println("5. Reporte diario de ventas");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    registrarProducto();
                    break;

                case 2:
                    gestionProductos.listarProductos();
                    break;

                case 3:
                    reponerStock();
                    break;

                case 4:
                    registrarPedido();
                    break;

                case 5:
                    archivoDatos.generarReporteDiario();
                    break;

                case 6:
                    System.out.println("¡Gracias por utilizar el sistema!");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 6);

        teclado.close();
    }

    private static int leerEntero() {
        while (!teclado.hasNextInt()) {
            System.out.print("Ingrese un número entero válido: ");
            teclado.next();
        }

        int numero = teclado.nextInt();
        teclado.nextLine();
        return numero;
    }

    private static double leerDouble() {
        while (!teclado.hasNextDouble()) {
            System.out.print("Ingrese un número válido: ");
            teclado.next();
        }

        double numero = teclado.nextDouble();
        teclado.nextLine();
        return numero;
    }

    private static void registrarProducto() {
        System.out.println("\n--- REGISTRAR PRODUCTO ---");

        System.out.print("Código: ");
        String codigo = teclado.nextLine().trim();

        System.out.print("Nombre: ");
        String nombre = teclado.nextLine().trim();

        if (codigo.isEmpty() || nombre.isEmpty()
                || codigo.contains(";") || nombre.contains(";")
                || codigo.contains("|") || nombre.contains("|")
                || codigo.contains(":") || nombre.contains(":")) {
            System.out.println("Código o nombre no válido.");
            return;
        }

        System.out.print("Precio: ");
        double precio = leerDouble();

        System.out.print("Stock inicial: ");
        int stock = leerEntero();

        if (precio <= 0 || stock < 0) {
            System.out.println("El precio debe ser mayor que cero y el stock no puede ser negativo.");
            return;
        }

        Producto producto = new Producto(codigo, nombre, precio, stock);

        if (gestionProductos.registrarProducto(producto)) {
            archivoDatos.guardarProductos(
                    gestionProductos.getProductos());
            System.out.println("Producto registrado correctamente.");
        } else {
            System.out.println("Ya existe un producto con ese código.");
        }
    }

    private static void reponerStock() {
        System.out.println("\n--- REPONER STOCK ---");

        gestionProductos.listarProductos();

        System.out.print("Código del producto: ");
        String codigo = teclado.nextLine().trim();

        System.out.print("Cantidad a reponer: ");
        int cantidad = leerEntero();

        if (gestionProductos.reponerStock(codigo, cantidad)) {
            archivoDatos.guardarProductos(
                    gestionProductos.getProductos());
            System.out.println("Stock actualizado correctamente.");
        } else {
            System.out.println("Producto inexistente o cantidad no válida.");
        }
    }

    private static void registrarPedido() {
        System.out.println("\n--- REGISTRAR PEDIDO ---");

        if (gestionProductos.getProductos().isEmpty()) {
            System.out.println("Primero debe registrar productos.");
            return;
        }

        ArrayList<DetallePedido> detalles = new ArrayList<>();

        while (true) {
            gestionProductos.listarProductos();

            System.out.print(
                    "Código del producto (o FIN para terminar): ");
            String codigo = teclado.nextLine().trim();

            if (codigo.equalsIgnoreCase("FIN")) {
                break;
            }

            Producto producto = gestionProductos.buscarProducto(codigo);

            if (producto == null) {
                System.out.println("Producto no encontrado.");
                continue;
            }

            boolean repetido = false;

            for (DetallePedido detalle : detalles) {
                if (detalle.getProducto().getCodigo()
                        .equalsIgnoreCase(codigo)) {
                    repetido = true;
                    break;
                }
            }

            if (repetido) {
                System.out.println(
                        "Ese producto ya está en el pedido. "
                        + "Registre cada producto una sola vez.");
                continue;
            }

            System.out.print("Cantidad: ");
            int cantidad = leerEntero();

            if (cantidad <= 0 || cantidad > producto.getStock()) {
                System.out.println(
                        "Cantidad no válida o stock insuficiente.");
                continue;
            }

            detalles.add(new DetallePedido(producto, cantidad));
            System.out.println("Producto agregado al pedido.");
        }

        if (detalles.isEmpty()) {
            System.out.println("No se registró ningún pedido.");
            return;
        }

        Pedido pedido = gestionPedidos.registrarPedido(
                gestionProductos, detalles);

        if (pedido != null) {
            archivoDatos.guardarProductos(
                    gestionProductos.getProductos());
            archivoDatos.guardarVenta(pedido);

            System.out.println("\nPedido registrado correctamente.");

            for (DetallePedido detalle : pedido.getDetalles()) {
                System.out.println(detalle);
            }

            System.out.println("TOTAL: $"
                    + String.format("%.2f", pedido.calcularTotal()));
        } else {
            System.out.println(
                    "No se pudo registrar el pedido. Revise el stock.");
        }
    }
}
