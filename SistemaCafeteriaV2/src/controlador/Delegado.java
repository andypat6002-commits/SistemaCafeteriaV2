package controlador;

import java.util.ArrayList;

import modelo.Producto;
import modelo.Pedido;
import modelo.DetallePedido;
import modelo.OperacionesGenericas;
import modelo.ArchivoDatos;

public class Delegado {

    private static final ArrayList<Producto> productos =
            new ArrayList<>();

    private static final ArrayList<Pedido> pedidos =
            new ArrayList<>();

    private static boolean inicializado = false;

    public Delegado() {
        if (!inicializado) {
            ArrayList<Producto> cargados =
                    ArchivoDatos.cargarProductos();

            if (cargados.isEmpty()) {
                cargarProductosIniciales();
            } else {
                productos.addAll(cargados);
            }

            // Agregar los productos iniciales que falten.
            agregarSiNoExiste(
                new Producto("P01", "Cafe", 1.00, 20)
            );
            agregarSiNoExiste(
                new Producto("P02", "Sanduche", 2.50, 15)
            );
            agregarSiNoExiste(
                new Producto("P03", "Jugo", 1.25, 10)
            );

            pedidos.addAll(ArchivoDatos.cargarPedidos(productos));

            ArchivoDatos.guardarProductos(productos);
            inicializado = true;
        }
    }

    private void cargarProductosIniciales() {
        OperacionesGenericas.agregarElementos(
            productos,
            new Producto("P01", "Cafe", 1.00, 20),
            new Producto("P02", "Sanduche", 2.50, 15),
            new Producto("P03", "Jugo", 1.25, 10)
        );
    }

    private void agregarSiNoExiste(Producto nuevo) {
        for (Producto p : productos) {
            if (p.getCodigo().equalsIgnoreCase(nuevo.getCodigo())) {
                return;
            }
        }
        productos.add(nuevo);
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public boolean registrarProducto(Producto producto) {
        for (Producto existente : productos) {
            if (existente.getCodigo().equalsIgnoreCase(
                    producto.getCodigo())) {
                return false;
            }
        }

        productos.add(producto);
        ArchivoDatos.guardarProductos(productos);
        return true;
    }

    public boolean eliminarProducto(String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equalsIgnoreCase(codigo)) {
                for (Pedido pedido : pedidos) {
                    for (DetallePedido detalle : pedido.getDetalles()) {
                        if (detalle.getProducto() == producto) {
                            return false;
                        }
                    }
                }

                productos.remove(producto);
                ArchivoDatos.guardarProductos(productos);
                return true;
            }
        }

        return false;
    }

    public int contarProductosRegistrados() {
        return OperacionesGenericas.contarCoincidencias(
            productos, productos.toArray(new Producto[0])
        );
    }

    public void registrarPedido(Pedido pedido) {
        pedidos.add(pedido);
        ArchivoDatos.guardarProductos(productos);
        ArchivoDatos.guardarPedidos(pedidos);
    }
}