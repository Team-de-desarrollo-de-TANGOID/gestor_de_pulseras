package src;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DniParser {
    public static Map<String, String> parse(String line) {
        Map<String, String> datos = new HashMap<>();
        String[] partes = line.split("@");

        if (partes.length >= 8) {
            datos.put("apellido", partes[1].toUpperCase().trim());
            datos.put("nombre", partes[2].toUpperCase().trim());
            datos.put("sexo", partes[3].toUpperCase().trim());
            datos.put("dni", partes[4].trim());
            
            String fechaNacStr = partes[6].trim(); // Formato esperado DD/MM/AAAA
            datos.put("nacimiento", fechaNacStr);
            datos.put("edad", calcularEdadExacta(fechaNacStr));
        } else {
            datos.put("apellido", ""); datos.put("nombre", "");
            datos.put("dni", ""); datos.put("sexo", "M");
            datos.put("edad", "");
        }
        return datos;
    }

    private static String calcularEdadExacta(String fechaStr) {
        try {
            // El scanner suele entregar DD/MM/AAAA
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaNac = LocalDate.parse(fechaStr, fmt);
            LocalDate ahora = LocalDate.now();
            
            return String.valueOf(Period.between(fechaNac, ahora).getYears());
        } catch (Exception e) {
            return "0";
        }
    }
}