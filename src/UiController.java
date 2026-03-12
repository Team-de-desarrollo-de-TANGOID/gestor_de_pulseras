package src;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.util.*;
import java.io.File;
import java.awt.Desktop;
import java.net.URI;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class UiController {

    @FXML
    private TextField txtLector, txtApe, txtNom, txtDni, txtEdad, txtRutaCsv;
    @FXML
    private ComboBox<String> comboSexo, comboTipoQr, comboPrinters, comboMedida, comboRiesgo;
    @FXML
    private Label lblEstado, lblEstadoLicencia, licenseInfo;
    @FXML
    private Button btnActivarProducto;
    @FXML
    private Canvas canvasPreview;
    @FXML
    private Tab tabOperacion;

    private ZebraService zebraService = new ZebraService();
    private ConfigService config = new ConfigService();

    @FXML
    public void initialize() {
        try {
            // 1. Llenado de ComboBoxes
            if (comboSexo != null)
                comboSexo.getItems().addAll("M", "F");
            if (comboTipoQr != null)
                comboTipoQr.getItems().addAll("ESTÁNDAR CLÍNICO", "SOLO IDENTIDAD");
            if (comboRiesgo != null)
                comboRiesgo.getItems().addAll("RIESGO BAJO", "RIESGO MEDIO", "RIESGO ALTO");

            if (comboPrinters != null) {
                comboPrinters.getItems().setAll(zebraService.getPrinters());
                String ultima = config.getUltimaImpresora();
                if (ultima != null && !ultima.isEmpty())
                    comboPrinters.setValue(ultima);
                else if (!comboPrinters.getItems().isEmpty())
                    comboPrinters.setValue(comboPrinters.getItems().get(0));
            }

            if (comboMedida != null) {
                comboMedida.getItems().addAll("ADULTO", "NIÑO");
                comboMedida.setValue("ADULTO");
            }

            if (comboRiesgo != null)
                comboRiesgo.setValue("RIESGO BAJO");
            if (comboSexo != null)
                comboSexo.setValue("M");

            // 2. Cargar ruta de CSV guardada
            if (txtRutaCsv != null) {
                txtRutaCsv.setText(config.getRutaCsv());
            }

            // 3. Inicializar Listeners, Verificación de Licencia y Dibujo
            configurarListeners();
            verificarLicencia();
            dibujarPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verificarLicencia() {
        boolean estaActivado = LicenseManager.isActivated();

        if (estaActivado) {
            lblEstadoLicencia.setText("ESTADO: LICENCIA ACTIVA");
            lblEstadoLicencia.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            
            btnActivarProducto.setVisible(false);
            if (tabOperacion != null)
                tabOperacion.setDisable(false);
            lblEstado.setText("Listo. Sistema Activado.");
        } else {
            licenseInfo.setText("");
            lblEstadoLicencia.setText("ESTADO: SIN ACTIVAR");
            lblEstadoLicencia.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            btnActivarProducto.setVisible(true);
            if (tabOperacion != null)
                tabOperacion.setDisable(true);
            lblEstado.setText("SISTEMA BLOQUEADO - REQUIERE LICENCIA");
        }
    }

    @FXML
    private void handleSeleccionarRutaCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo de ingresos");
        fileChooser.setInitialFileName("ingresos.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));

        File file = fileChooser.showSaveDialog(txtRutaCsv.getScene().getWindow());
        if (file != null) {
            String nuevaRuta = file.getAbsolutePath();
            txtRutaCsv.setText(nuevaRuta);
            config.setRutaCsv(nuevaRuta);
            lblEstado.setText("Ruta de guardado actualizada.");
        }
    }

    private void configurarListeners() {
        txtApe.textProperty().addListener((o, v, n) -> dibujarPreview());
        txtNom.textProperty().addListener((o, v, n) -> dibujarPreview());
        txtDni.textProperty().addListener((o, v, n) -> dibujarPreview());
        txtEdad.textProperty().addListener((o, v, n) -> dibujarPreview());

        if (comboMedida != null)
            comboMedida.valueProperty().addListener((o, v, n) -> dibujarPreview());
        if (comboRiesgo != null)
            comboRiesgo.valueProperty().addListener((o, v, n) -> dibujarPreview());
        if (comboSexo != null)
            comboSexo.valueProperty().addListener((o, v, n) -> dibujarPreview());
    }

    private void dibujarPreview() {
        if (canvasPreview == null)
            return;

        GraphicsContext gc = canvasPreview.getGraphicsContext2D();
        double w = canvasPreview.getWidth();
        double h = canvasPreview.getHeight();

        gc.clearRect(0, 0, w, h);

        String nom = (txtNom.getText() == null || txtNom.getText().isEmpty()) ? "NOMBRE"
                : txtNom.getText().toUpperCase();
        String ape = (txtApe.getText() == null || txtApe.getText().isEmpty()) ? "APELLIDO"
                : txtApe.getText().toUpperCase();
        String dni = (txtDni.getText() == null || txtDni.getText().isEmpty()) ? "00000000" : txtDni.getText();
        String edad = (txtEdad.getText() == null || txtEdad.getText().isEmpty()) ? "0" : txtEdad.getText();
        String sexo = (comboSexo.getValue() == null) ? "M" : comboSexo.getValue();
        String medida = (comboMedida.getValue() == null) ? "ADULTO" : comboMedida.getValue();

        gc.setFill(Color.WHITE);
        gc.fillRoundRect(0, 0, w, h, 10, 10);
        gc.setStroke(Color.web("#244570"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(0, 0, w, h, 10, 10);

        gc.setFill(Color.BLACK);

        if ("NIÑO".equalsIgnoreCase(medida)) {
            gc.setFont(Font.font("Monospaced", 14));
            gc.fillText("DNI: " + dni + " | E: " + edad + " | S: " + sexo, 20, 40);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            gc.fillText(nom, 20, 70);
            gc.setFont(Font.font("Arial", FontWeight.BLACK, 28));
            gc.fillText(ape, 20, 105);
            gc.fillRect(w - 100, 20, 80, 80);
        } else {
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
            gc.fillText("DNI: " + dni + " | E: " + edad + " | S: " + sexo, 20, 30);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 26));
            gc.fillText(nom, 20, 65);
            gc.setFont(Font.font("Arial", FontWeight.BLACK, 34));
            gc.fillText(ape, 20, 105);
            gc.fillRect(w - 180, 20, 80, 80);
        }
    }

    @FXML
    private void handleImprimir() {
        if (!LicenseManager.isActivated()) {
            mostrarAlerta("Error de Licencia", "Sistema no activado.");
            return;
        }

        try {
            lblEstado.setText("Enviando orden a la impresora...");
            lblEstado.setStyle("-fx-text-fill: blue;");

            String printerName = comboPrinters.getValue();
            Map<String, String> datos = new HashMap<>();
            datos.put("nombre", txtNom.getText());
            datos.put("apellido", txtApe.getText());
            datos.put("dni", txtDni.getText());
            datos.put("edad", txtEdad.getText());
            datos.put("sexo", comboSexo.getValue());
            datos.put("riesgo", comboRiesgo.getValue());

            // El método imprimir ahora lanza excepciones si la impresora falla físicamente
            zebraService.imprimir(datos, printerName, comboMedida.getValue(), comboTipoQr.getValue());

            // Si llegó aquí, es que no hubo excepción
            lblEstado.setText("IMPRESIÓN EXITOSA: " + datos.get("apellido"));
            lblEstado.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            limpiarFormulario();

        } catch (Exception e) {
            // AQUÍ SE MUESTRAN LOS ERRORES DE HARDWARE (Sin papel, desconectada, etc.)
            lblEstado.setText("ERROR: " + e.getMessage());
            lblEstado.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            mostrarAlerta("Fallo de Impresión", e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNom.clear();
        txtApe.clear();
        txtDni.clear();
        txtEdad.clear();
        txtLector.clear();
        dibujarPreview();
        txtLector.requestFocus();
    }

    @FXML
    private void handleLectorAction() {
        try {
            Map<String, String> d = DniParser.parse(txtLector.getText());
            if (d != null) {
                txtApe.setText(d.getOrDefault("apellido", ""));
                txtNom.setText(d.getOrDefault("nombre", ""));
                txtDni.setText(d.getOrDefault("dni", ""));
                txtEdad.setText(d.getOrDefault("edad", ""));
                if (d.get("sexo") != null)
                    comboSexo.setValue(d.get("sexo"));
            }
        } catch (Exception e) {
            lblEstado.setText("Error en lectura de DNI.");
        }
        txtLector.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleOpenWeb() {
        try {
            Desktop.getDesktop().browse(new URI("http://www.tangoid.com.ar"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleActivarLicencia() {
        String hwid = LicenseManager.getHardwareID();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Activación de Software");
        alert.setHeaderText("ID de Equipo: " + hwid);
        alert.setContentText("Envíe este ID a soporte para recibir su clave.");

        ButtonType btnCopiar = new ButtonType("Copiar ID");
        ButtonType btnContinuar = new ButtonType("Ingresar Clave");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnCopiar, btnContinuar, btnCancelar);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == btnCopiar) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(hwid);
            clipboard.setContent(content);
            lblEstado.setText("ID Copiado.");
            handleActivarLicencia();

        } else if (result.isPresent() && result.get() == btnContinuar) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Activar Producto");
            dialog.setHeaderText("Registro de Licencia");
            dialog.setContentText("Ingrese la Product Key:");

            Optional<String> keyResult = dialog.showAndWait();
            keyResult.ifPresent(key -> {
                try {
                    LicenseManager.activate(key);
                    mostrarAlerta("Éxito", "Software activado correctamente.");
                    verificarLicencia();
                    licenseInfo.setText("Product Key: " + key);
                } catch (Exception e) {
                    mostrarAlerta("Error", "Clave inválida.");
                }
            });
        }
    }
}