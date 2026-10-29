package modelo;

import java.util.ArrayList;

public class OperacionesGenericas {

    // Agrega varios elementos a una lista.
    @SafeVarargs
    public static <T> void agregarElementos(
            ArrayList<T> lista, T... elementos) {

        for (T elemento : elementos) {
            lista.add(elemento);
        }
    }

    // Cuenta cuántos elementos recibidos están en la lista.
    @SafeVarargs
    public static <T> int contarCoincidencias(
            ArrayList<T> lista, T... elementos) {

        int contador = 0;

        for (T elemento : elementos) {
            if (lista.contains(elemento)) {
                contador++;
            }
        }

        return contador;
    }
}