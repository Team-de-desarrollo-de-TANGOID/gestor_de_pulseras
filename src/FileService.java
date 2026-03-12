package src;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FileService {
    private final String PATH = System.getProperty("user.home") + "/Documents/ingresos.csv";

    public void appendCsv(Map<String, String> d) {
        File f = new File(PATH);
        boolean existe = f.exists();
        try (PrintWriter w = new PrintWriter(new FileWriter(f, true))) {
            if (!existe) w.println("Fecha,Apellido,Nombre,DNI,Sexo,Edad");
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            w.printf("%s,%s,%s,%s,%s,%s%n", fecha, d.get("apellido"), d.get("nombre"), d.get("dni"), d.get("sexo"), d.get("edad"));
        } catch (IOException e) { e.printStackTrace(); }
    }
}