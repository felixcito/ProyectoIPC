/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
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
}
