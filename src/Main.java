package src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main_view.fxml"));
            Parent root = loader.load();
            
            UiController controller = loader.getController();
            
            primaryStage.setTitle("TangoID - Gestión de Pulseras");
            
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/src/icono.png")));
            } catch (Exception e) {
                System.out.println("Icono no encontrado.");
            }

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            // Verificación inicial
            if (!LicenseManager.isActivated()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Software no activado");
                alert.setHeaderText("Licencia no encontrada");
                alert.setContentText("El sistema se encuentra bloqueado.\n" +
                                   "Por favor, active el producto en la pestaña INFO.");
                alert.showAndWait();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}