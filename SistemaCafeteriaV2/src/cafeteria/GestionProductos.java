
package cafeteria;

import java.util.ArrayList;

public class GestionProductos {

    private ArrayList<Producto> productos;

    public GestionProductos() {
        productos = new ArrayList<>();
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public boolean registrarProducto(Producto producto) {
        if (buscarProducto(producto.getCodigo()) != null) {
            return false;
        }

        productos.add(producto);
        return true;
    }

    public Producto buscarProducto(String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equalsIgnoreCase(codigo)) {
                return producto;
            }
        }

        return null;
    }

    public void listarProductos() {
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (Producto producto : productos) {
            System.out.println(producto);
        }
    }

    public boolean reponerStock(String codigo, int cantidad) {
        Producto producto = buscarProducto(codigo);

        if (producto == null || cantidad <= 0) {
            return false;
        }

        producto.reponerStock(cantidad);
        return true;
    }
}
