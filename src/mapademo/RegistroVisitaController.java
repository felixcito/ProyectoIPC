/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

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
        // TODO
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
        }
    }
    
}
