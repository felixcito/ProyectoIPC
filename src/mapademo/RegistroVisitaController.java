/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.File;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Fokus
 */
public class RegistroVisitaController implements Initializable {

    @FXML
    private VBox fondo_register;
    @FXML
    private ImageView avatarImage;
    @FXML
    private Circle circleImage;
    @FXML
    private PasswordField passwordHide;
    @FXML
    private TextField passwordShow;
    @FXML
    private ImageView ojoVisible;
    @FXML
    private ImageView ojoTachado;
    @FXML
    private Hyperlink hyperlinkTerminos;
    @FXML
    private TextField campoNickname; //fx:id "campoNickname"
    
    @FXML
    private TextField campoEmail;    // fx:id "campoEmail"
    
    @FXML
    private DatePicker campoFecha;   // fx:id "campoFecha" 
     

    // Variable extra para guardar la ruta de la imagen si el usuario elige una
    private String avatarPath = null;
    @FXML
    private Button register;
    @FXML
    private CheckBox checkTerminos;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Circle clip = new Circle();
        clip.setRadius(40);
        clip.setCenterX(62.5);
        clip.setCenterY(62.5);
        avatarImage.setClip(clip);
        
        passwordShow.setVisible(false);
        passwordShow.setManaged(false);
        ojoVisible.setVisible(false);
        
        tooltip.setAutoHide(true);
        fondo_register.setOnMouseClicked(e -> {
            tooltip.hide();
        });
    }    

    @FXML
    private void changeAvatar(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select avatar");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "Images",
                "*.png",
                "*.jpg",
                "*.jpeg"
            )
        );

        File file = fileChooser.showOpenDialog(null);

        if(file != null) {
            //Guarda el path de la imagen del avatar
            avatarPath = file.getAbsolutePath();
            Image image = new Image(file.toURI().toString());
            
            avatarImage.setFitWidth(125);
            avatarImage.setFitHeight(125);

            avatarImage.setPreserveRatio(false);

            avatarImage.setImage(image);
            
            circleImage.setVisible(false);
        }
    }

    @FXML
    private void showPassword(MouseEvent event) {
        passwordShow.setText(passwordHide.getText());

        passwordShow.setVisible(true);
        passwordShow.setManaged(true);

        passwordHide.setVisible(false);
        passwordHide.setManaged(false);

        ojoVisible.setVisible(true);
        ojoTachado.setVisible(false);
    }

    @FXML
    private void hidePassword(MouseEvent event) {
        passwordHide.setText(passwordShow.getText());

        passwordHide.setVisible(true);
        passwordHide.setManaged(true);

        passwordShow.setVisible(false);
        passwordShow.setManaged(false);

        ojoVisible.setVisible(false);
        ojoTachado.setVisible(true);
    }

    Tooltip tooltip = new Tooltip(
        "Con la aceptación de estos términos y condiciones,\n"
      + "el usuario reconoce el talento descomunal de\n"
      + "los desarrolladores de este proyecto de IPC.\n"
      + "Además se compromete a ponerle un 10 a todos y\n"
      + "cada uno de ellos."
    );

    @FXML
    private void showTerms(ActionEvent event) {
        tooltip.show(
            hyperlinkTerminos,
            hyperlinkTerminos.localToScreen(0, 0).getX() + 10,
            hyperlinkTerminos.localToScreen(0, 0).getY() + 10
        );
    }
    @FXML
    private void realizarRegistro(ActionEvent event) {
        // 1. Recoger los datos de los campos
        String nick = campoNickname.getText();
        String email = campoEmail.getText();
        String pass = passwordHide.getText(); // Cogemos la del campo oculto
        
        // Si no ha elegido fecha, mostramos un error y paramos
        if (campoFecha.getValue() == null) {
            mostrarError("Por favor, selecciona tu fecha de nacimiento.");
            return;
        }
        
        if (!checkTerminos.isSelected()) {
            mostrarError("Debes aceptar los términos y condiciones.");
            return;
        }
        
        java.time.LocalDate fecha = campoFecha.getValue();

        // 2. Validar los datos usando los métodos estáticos de la clase User
        if (!upv.ipc.sportlib.User.checkNickName(nick)) {
            mostrarError("El nickname debe tener entre 6 y 15 caracteres (letras, números, - o _).");
            return;
        }
        if (!upv.ipc.sportlib.User.checkEmail(email)) {
            mostrarError("El formato del correo no es válido.");
            return;
        }
        if (!upv.ipc.sportlib.User.checkPassword(pass)) {
            mostrarError("La contraseña debe tener 8-20 caracteres, mayúscula, minúscula, número y símbolo.");
            return;
        }
        if (!upv.ipc.sportlib.User.isOlderThan(fecha, 12)) {
            mostrarError("Debes tener más de 12 años para registrarte.");
            return;
        }

        // 3. Si todo es correcto, registramos al usuario en la base de datos
        boolean registrado = upv.ipc.sportlib.SportActivityApp.getInstance().registerUser(nick, email, pass, fecha, avatarPath);

        if (registrado) {
            // Mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("¡Registro completado con éxito! Ahora puedes iniciar sesión.");
            alert.showAndWait();

            // Opcional: Volver automáticamente a la pantalla de Login después de registrarse
            try {
                javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("LoginVisita.fxml"));
                javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

        } else {
            mostrarError("El nickname '" + nick + "' ya está en uso. Elige otro.");
        }
    }

    // Método auxiliar para no repetir código creando alertas de error
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.initStyle(StageStyle.TRANSPARENT);

        alert.setGraphic(null);

        alert.setHeaderText("Error de validación");

        alert.setContentText(mensaje);

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.getStylesheets().add(
            getClass()
                .getResource("/resources/estilos.css")
                .toExternalForm()
        );

        dialogPane.getStyleClass().add("custom-alert");

        Scene scene = dialogPane.getScene();

        scene.setFill(Color.TRANSPARENT);

        alert.showAndWait();
    }

    @FXML
    private void irLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
            getClass().getResource("LoginVisita.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));

        stage.show();
    }
}
