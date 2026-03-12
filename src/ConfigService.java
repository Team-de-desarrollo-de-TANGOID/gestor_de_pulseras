package src;

import java.io.*;
import java.util.Properties;

public class ConfigService {
    private Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";
    private static final String PRINTER_KEY = "ultima_impresora";
    private static final String CSV_PATH_KEY = "ruta_csv";

    public ConfigService() {
        load();
    }

    private void load() {
        try (InputStream is = new FileInputStream(CONFIG_FILE)) {
            properties.load(is);
        } catch (IOException e) {
            // Valores por defecto si el archivo no existe
        }
    }

    public void save() {
        try (OutputStream os = new FileOutputStream(CONFIG_FILE)) {
            properties.store(os, "Configuracion TangoID");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUltimaImpresora() {
        return properties.getProperty(PRINTER_KEY, "");
    }

    public void setUltimaImpresora(String printer) {
        properties.setProperty(PRINTER_KEY, printer);
        save();
    }

    public String getRutaCsv() {
        String defaultPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "ingresos.csv";
        return properties.getProperty(CSV_PATH_KEY, defaultPath);
    }

    public void setRutaCsv(String ruta) {
        properties.setProperty(CSV_PATH_KEY, ruta);
        save();
    }
}