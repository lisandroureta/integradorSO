import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LectorArchivo {
    public List<Trabajo> leerTrabajosDesdeArchivo(String nombreArchivo) {
        List<Trabajo> trabajos = new ArrayList<>();

        Path path = Paths.get(nombreArchivo);
        if (!Files.exists(path)) {
            System.err.println("El archivo no existe: " + path.toAbsolutePath());
            return trabajos;
        }

        if (!Files.isReadable(path)) {
            System.err.println("No se puede leer el archivo: " + path.toAbsolutePath());
            return trabajos;
        }

        try {
            List<String> lineas = Files.readAllLines(path);
            trabajos = lineas.stream()
                    .map(this::parsearLinea)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + path.toAbsolutePath());
            e.printStackTrace();
        }

        return trabajos;
    }

    private Trabajo parsearLinea(String linea) {
        String[] datos = linea.trim().split("\\s+");
        if (datos.length != 4) {
            throw new IllegalArgumentException("Formato de línea inválido: " + linea);
        }

        try {
            String nombre = datos[0].trim();
            int instanteArribo = Integer.parseInt(datos[1].trim());
            int duracion = Integer.parseInt(datos[2].trim());
            int memoriaRequerida = Integer.parseInt(datos[3].trim());

            return new Trabajo(nombre, instanteArribo, duracion, memoriaRequerida);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error al convertir datos numéricos en la línea: " + linea, e);
        }
    }
}
