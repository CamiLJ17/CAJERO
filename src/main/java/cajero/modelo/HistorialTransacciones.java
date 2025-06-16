package cajero.modelo;

import java.util.ArrayList;
import java.util.List;

public class HistorialTransacciones {
    private static List<Transaccion> historial = new ArrayList<>();

    public static void registrar(Transaccion t) {
        historial.add(t);
    }

    public static List<Transaccion> obtenerHistorial() {
        return historial;
    }
}
