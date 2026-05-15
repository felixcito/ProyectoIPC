/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tarea2.controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import modelo.Persona;

/**
 * FXML Controller class
 *
 * @author jsoler
 */
public class VistaPersonaController implements Initializable {

    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField apellidosTextField;
    private Persona persona;
    
    private boolean pulsadoOK = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    public boolean isOKPressed(){
        return pulsadoOK;
    }
    public Persona getPersona(){
        return persona;
    }
    @FXML
    private void aceptar(ActionEvent event) {
        // Ponemos el estado a true 
        pulsadoOK = true;

        // Actualizamos el objeto persona con los datos de los cuadros de texto 
        this.initPersona(new Persona(nombreTextField.getText(), apellidosTextField.getText()));

        //Cerramos la ventana
        nombreTextField.getScene().getWindow().hide();    
    }

    @FXML
    private void cancelar(ActionEvent event) {
        //Ponemos estado a false
        pulsadoOK = false;
        //Cerramos la ventana
        nombreTextField.getScene().getWindow().hide();
    }
    public void initPersona(Persona p){
        persona = p;
        nombreTextField.setText(p.getNombre());
        apellidosTextField.setText(p.getApellidos());
    }


    
}
