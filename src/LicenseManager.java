package src;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class LicenseManager {
    private static final String LICENSE_FILE = "license.dat";

    // Obtiene el ID único de la Placa Madre en Windows
    public static String getHardwareID() {
        try {
            Process process = Runtime.getRuntime().exec("wmic baseboard get serialnumber");
            Scanner sc = new Scanner(process.getInputStream());
            sc.next(); // Saltar el encabezado "SerialNumber"
            String id = sc.next().trim();
            return id.isEmpty() ? "PC-GENERICA-01" : id;
        } catch (Exception e) {
            return "PC-ERROR-ID";
        }
    }

    public static String generateKey(String hwid) {
        // Debe ser exactamente igual al del KeyGenerator
        String salt = "TangoID_2024_SecSecret";
        int hash = (hwid + salt).hashCode();
        return "TANGO-" + Integer.toHexString(hash).toUpperCase();
    }

    public static boolean isActivated() {
        try {
            if (!Files.exists(Paths.get(LICENSE_FILE)))
                return false;
            String savedKey = new String(Files.readAllBytes(Paths.get(LICENSE_FILE))).trim();
            return savedKey.equals(generateKey(getHardwareID()));
        } catch (Exception e) {
            return false;
        }
    }

    public static void activate(String key) throws Exception {
        if (key.equals(generateKey(getHardwareID()))) {
            Files.write(Paths.get(LICENSE_FILE), key.getBytes());
        } else {
            throw new Exception("Clave de activación incorrecta para este equipo.");
        }
    }
}