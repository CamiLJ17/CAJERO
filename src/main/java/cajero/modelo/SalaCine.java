package cajero.modelo;

import java.util.*;

public class SalaCine {

    private static final List<String> FILAS_GENERAL = Arrays.asList("A");
    private static final List<String> FILAS_PREFERENCIAL = Arrays.asList("E");
    private static final int ASIENTOS_POR_FILA = 20;
    private static final int LIMITE_GENERAL = 10;
    private static final int LIMITE_PREFERENCIAL = 20;

    private final Map<String, Map<String, Set<String>>> ocupadasPorPelicula;

    private static final SalaCine salaCine = new SalaCine(Arrays.asList(
        "Cómo entrenar a tu dragón",
        "Destino final: Lazos de sangre",
        "Elio",
        "Exterminio: La evolución",
        "Lilo & Stitch",
        "Misión imposible: La sentencia final"
        )
    );

    public SalaCine(List<String> peliculas) {
        ocupadasPorPelicula = new HashMap<>();
        for (String pelicula : peliculas) {
            Map<String, Set<String>> porLocalidad = new HashMap<>();
            porLocalidad.put("General", new HashSet<>());
            porLocalidad.put("Preferencial", new HashSet<>());
            ocupadasPorPelicula.put(pelicula, porLocalidad);
        }
    }

    public int sillasDisponibles(String pelicula, String localidad) {
        int total = localidad.equals("General") ? LIMITE_GENERAL : LIMITE_PREFERENCIAL;
        return total - ocupadasPorPelicula.get(pelicula).get(localidad).size();
    }

    public List<String> asignarSillas(String pelicula, String localidad, int cantidad) {
        List<String> filas = localidad.equals("General") ? FILAS_GENERAL : FILAS_PREFERENCIAL;
        Set<String> ocupadasLocal = ocupadasPorPelicula.get(pelicula).get(localidad);

        List<String> disponibles = new ArrayList<>();
        for (String fila : filas) {
            for (int i = 1; i <= ASIENTOS_POR_FILA; i++) {
                String silla = fila + i;
                if (!ocupadasLocal.contains(silla)) {
                    disponibles.add(silla);
                }
            }
        }

        if (disponibles.size() < cantidad) {
            return Collections.emptyList();
        }

        Collections.shuffle(disponibles);
        List<String> asignadas = disponibles.subList(0, cantidad);
        ocupadasLocal.addAll(asignadas);
        return asignadas;
    }
}
