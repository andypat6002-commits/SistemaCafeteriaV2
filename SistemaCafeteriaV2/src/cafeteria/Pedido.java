
package cafeteria;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Pedido {

    private int numero;
    private LocalDateTime fecha;
    private ArrayList<DetallePedido> detalles;

    public Pedido(int numero) {
        this.numero = numero;
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
    }

    public int getNumero() {
        return numero;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public ArrayList<DetallePedido> getDetalles() {
        return detalles;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
    }

    public double calcularTotal() {
        double total = 0;

        for (DetallePedido detalle : detalles) {
            total += detalle.calcularSubtotal();
        }

        return total;
    }

    @Override
    public String toString() {
        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return "Pedido N.º " + numero
                + " | Fecha: " + fecha.format(formato)
                + " | Total: $" + String.format("%.2f", calcularTotal());
    }
}
