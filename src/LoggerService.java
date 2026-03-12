package src;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerService {
    private static final String LOG_FILE = "debug.log";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void log(String level, String message, Exception e) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            String timestamp = dtf.format(LocalDateTime.now());
            pw.println("[" + timestamp + "] " + level + ": " + message);
            
            if (e != null) {
                e.printStackTrace(pw); // Guarda todo el rastro del error
            }
            pw.println("-------------------------------------------------------");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void info(String message) { log("INFO", message, null); }
    public static void error(String message, Exception e) { log("ERROR", message, e); }
}