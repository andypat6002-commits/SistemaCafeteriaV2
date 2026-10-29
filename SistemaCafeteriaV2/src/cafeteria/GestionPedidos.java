
package cafeteria;

import java.util.ArrayList;

public class GestionPedidos {

    private ArrayList<Pedido> pedidos;
    private int siguienteNumero;

    public GestionPedidos() {
        pedidos = new ArrayList<>();
        siguienteNumero = 1;
    }

    public Pedido registrarPedido(
            GestionProductos gestionProductos,
            ArrayList<DetallePedido> detalles) {

        if (detalles == null || detalles.isEmpty()) {
            return null;
        }

        // Verificar que haya suficiente stock
        for (DetallePedido detalle : detalles) {
            Producto producto = detalle.getProducto();
            int cantidad = detalle.getCantidad();

            if (cantidad <= 0 || producto.getStock() < cantidad) {
                return null;
            }
        }

        // Crear el pedido y descontar el inventario
        Pedido pedido = new Pedido(siguienteNumero++);

        for (DetallePedido detalle : detalles) {
            detalle.getProducto().vender(detalle.getCantidad());
            pedido.agregarDetalle(detalle);
        }

        pedidos.add(pedido);
        return pedido;
    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }
}
