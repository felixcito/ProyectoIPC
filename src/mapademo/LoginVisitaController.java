/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Fokus
 */
public class LoginVisitaController implements Initializable {

    @FXML
    private VBox fondo_login;
    @FXML
    private GridPane grid;
    @FXML
    private TextField campoNickname;
    @FXML
    private ImageView person_icon;
    @FXML
    private PasswordField campoPassword;
    @FXML
    private ImageView lock_icon;
    @FXML
    private Button login;
    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        campoNickname.textProperty().addListener((obs, oldText, newText) -> {
            boolean visible = newText.length() < 25;
            person_icon.setVisible(visible);
            person_icon.setManaged(visible);
        });
        
        campoPassword.textProperty().addListener((obs, oldText, newText) -> {
            boolean visible = newText.length() < 25;
            lock_icon.setVisible(visible);
            lock_icon.setManaged(visible);
        });
    }    

    @FXML
    private void irRegister(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
            getClass().getResource("RegistroVisita.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));

        stage.show();
    }
    @FXML
    private void realizarLogin(ActionEvent event) {
        String nick = campoNickname.getText();
        String pass = campoPassword.getText();

        // 1. Intentar iniciar sesión con la librería
        // Retorna true si las credenciales son correctas [cite: 267]
        boolean ok = upv.ipc.sportlib.SportActivityApp.getInstance().login(nick, pass);

        if (ok) {
            try {
                // 2. Si el login es correcto, cargamos la pantalla del mapa (FXMLDocument.fxml)
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                javafx.scene.Parent root = loader.load();

                // 3. Obtener la ventana actual y cambiar la escena
                javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Running la Safor - Principal");
                stage.show();
            } catch (java.io.IOException e) {
                mostrarError("Error al cargar la vista principal: " + e.getMessage());
            }
        } else {
            // 4. Si falla, mostramos un error de credenciales [cite: 57, 267]
            mostrarError("Usuario o contraseña incorrectos.");
        }
    }
    
    private void mostrarError(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
