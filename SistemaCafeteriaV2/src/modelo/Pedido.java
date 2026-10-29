package modelo;

import java.util.ArrayList;

public class Pedido {

    private String numero;
    private String fecha;
    private ArrayList<DetallePedido> detalles;

    public Pedido(String numero, String fecha) {
        this.numero = numero;
        this.fecha = fecha;
        this.detalles = new ArrayList<>();
    }

    public String getNumero() {
        return numero;
    }

    public String getFecha() {
        return fecha;
    }

    public ArrayList<DetallePedido> getDetalles() {
        return detalles;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
    }

    public double calcularSubtotal() {
        double subtotal = 0;

        for (DetallePedido detalle : detalles) {
            subtotal += detalle.calcularSubtotal();
        }

        return subtotal;
    }

    public double calcularIVA() {
        return calcularSubtotal() * 0.15;
    }

    public double calcularTotal() {
        return calcularSubtotal() + calcularIVA();
    }
}