/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modelo.Persona;

/**
 * FXML Controller class
 *
 * @author jsole
 */
public class VistaPersonaController_1 implements Initializable {

    @FXML
    private Stage stage;
    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField apellidosTextField;
    @FXML
    private ComboBox<String> imagenCombo;
    private Persona persona;
    // Variable para saber si pulsó aceptar
    private boolean okPressed = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        imagenCombo.getItems().addAll("/resources/images/Lloroso.png", "/resources/images/Pregunta.png", "/resources/images/Sonriente.png");
        //Usar la celda personalizada para ver los iconos en la lista y en el botón
        //Modifica lo que se muestra en la lista desplegable
        imagenCombo.setCellFactory(c -> new ImagenTabCell());
        //Modifica lo que se muestra en el cuadro de selección (botón)
        imagenCombo.setButtonCell(new ImagenTabCell());
    }      

    @FXML
    private void aceptar(ActionEvent event) {
        // Actualizamos el objeto persona con lo que hay en los campos
        persona.setNombre(nombreTextField.getText());
        persona.setApellidos(apellidosTextField.getText());
        persona.setImagenPath(imagenCombo.getSelectionModel().getSelectedItem());
        //Marcamos que se pulso aceptar
        okPressed = true;
        //Cerramos la ventana
        nombreTextField.getScene().getWindow().hide(); 
    }
    // Método que devuelve el valor de la variable
    public boolean isOKPressed() {
        return okPressed;
    }

    @FXML
    private void cancelar(ActionEvent event) {
        okPressed = false;
        nombreTextField.getScene().getWindow().hide();
    }
    class ImagenTabCell extends ComboBoxListCell<String> {
        private ImageView view = new ImageView();
        private Image imagen;

        @Override
        // Añadimos una clase que extiende TableCell e implementa el método updateItem()
        public void updateItem(String t, boolean bln) {
            super.updateItem(t, bln); 
            if (t == null || bln) {
                setText(null);
                setGraphic(null);
            } else {
                imagen = new Image(t,25,25,true,true);
                view.setImage(imagen);
                setGraphic(view);
                setText(null);
            }
        }
    }
    public void setPersona(Persona p) {
        this.persona = p;
        nombreTextField.setText(p.getNombre());
        apellidosTextField.setText(p.getApellidos());
        imagenCombo.getSelectionModel().select(p.getImagenPath());
    }
}
