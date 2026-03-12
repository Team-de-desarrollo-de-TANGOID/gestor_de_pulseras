package src;

import javax.print.*;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class ZebraService {

    private final int MARGEN_MM = 187; 
    private final int PUNTOS_POR_MM = 8;

    public List<String> getPrinters() {
        List<String> printerList = new ArrayList<>();
        try {
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (PrintService service : services) {
                printerList.add(service.getName());
            }
        } catch (Exception e) {
            printerList.add("Error al buscar impresoras");
        }
        if (printerList.isEmpty())
            printerList.add("No se detectaron impresoras");
        return printerList;
    }

    public String verificarEstadoReal(String printerName) {
        if (printerName == null || printerName.isEmpty() || printerName.contains("No se detectaron"))
            return "Desconectada";
        try {
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (PrintService service : services) {
                if (service.getName().equalsIgnoreCase(printerName)) {
                    PrinterIsAcceptingJobs accepting = service.getAttribute(PrinterIsAcceptingJobs.class);
                    return (accepting != null && accepting.equals(PrinterIsAcceptingJobs.ACCEPTING_JOBS))
                            ? "En línea / Lista"
                            : "Revisar Insumo";
                }
            }
        } catch (Exception e) {
            return "Error de estado";
        }
        return "No encontrada";
    }

    public void imprimir(Map<String, String> d, String printerName, String medida, String tipoQr) throws Exception {
        String zpl = generarZpl(d, medida, tipoQr);
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService selected = null;
        for (PrintService s : services) {
            if (s.getName().equalsIgnoreCase(printerName)) {
                selected = s;
                break;
            }
        }

        if (selected != null) {
            DocPrintJob job = selected.createPrintJob();
            // Usamos UTF-8 como estaba en tu archivo original
            byte[] by = zpl.getBytes("UTF-8");
            job.print(new SimpleDoc(by, DocFlavor.BYTE_ARRAY.AUTOSENSE, null), null);
            
            // Única adición: Guardar en el log CSV después de imprimir
            guardarEnCsv(d);
        } else {
            throw new Exception("Impresora no encontrada.");
        }
    }

    private String generarZpl(Map<String, String> d, String medida, String tipoQr) {
        int home = 1500;

        if ("ADULTO".equalsIgnoreCase(medida)) {
            // --- DISEÑO ADULTO ORIGINAL ---
            return "^XA" +
                    "^CI28^PON^LH0," + home + "^LL1600^PW191" +
                    "^HZ6,0" +
                    "^FO0,40^ADR,40,10^FDDNI: " + d.get("dni") + " | E: " + d.get("edad") + " | S: " + d.get("sexo")
                    + "^FS" +
                    "^FO40,40^A0R,70,70^FD" + d.get("nombre").toUpperCase() + "^FS" +
                    "^FO105,40^A0R,92,90^FD" + d.get("apellido").toUpperCase() + "^FS" +
                    "^HZ0,0" +
                    "^FO0,550^A0R,35,35^FD> " + d.get("riesgo").toUpperCase() + "^FS" +
                    "^FO0,1240^BQN,2,10^FDQA," + d.get("dni") + "^FS" +
                    "^XZ";
        } else {
            // --- DISEÑO NIÑO ORIGINAL ---
            return "^XA" +
                    "^FO0,75^A0N,60,20^FD- - - - - - - - - - - - - - - - ^FS" +
                    "^CI28^PON^LH0," + home + "^LL1600^PW191" +
                    "^FO0,440^ADR,40,10^FDDNI: " + d.get("dni") + " | E: " + d.get("edad") + " | S: " + d.get("sexo")
                    + "^FS" +
                    "^FO40,440^A0R,70,70^FD" + d.get("nombre").toUpperCase() + "^FS" +
                    "^FO105,440^A0R,92,90^FD" + d.get("apellido").toUpperCase() + "^FS" +
                    "^FO0,950^A0R,35,35^FD> " + d.get("riesgo").toUpperCase() + "^FS" +
                    "^FO0,1440^BQN,2,10^FDQA," + d.get("dni") + "^FS" +
                    "^XZ";
        }
    }

    private void guardarEnCsv(Map<String, String> datos) {
        ConfigService config = new ConfigService();
        File file = new File(config.getRutaCsv());
        if (file.getParentFile() != null) file.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
            pw.println(fecha + ";" + datos.get("dni") + ";" + datos.get("apellido") + ";" + datos.get("nombre") + ";" + datos.get("riesgo"));
        } catch (IOException e) {
            System.err.println("Error al guardar CSV.");
        }
    }

    public void imprimirPrueba(String printerName, String medida) throws Exception {
        Map<String, String> t = new HashMap<>();
        t.put("apellido", "PÉREZ");
        t.put("nombre", "JUAN");
        t.put("dni", "12345678");
        t.put("edad", "30");
        t.put("sexo", "M");
        t.put("riesgo", "RIESGO MODERADO");
        imprimir(t, printerName, medida, "FULL");
    }
}