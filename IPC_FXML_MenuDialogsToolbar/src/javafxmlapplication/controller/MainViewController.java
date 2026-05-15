/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmlapplication.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author sovacu
 */
public class MainViewController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Button amazon;
    @FXML
    private Button ebay;
    @FXML
    private Button facebook;
    @FXML
    private Button google;
    @FXML
    private RadioMenuItem compraAmazon;
    @FXML
    private ToggleGroup grupoTiendas;
    @FXML
    private RadioMenuItem compraEbay;
    @FXML
    private Label labelEstado;
    @FXML
    private Button blogger;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO    
       
    } 

    @FXML
    private void salir(ActionEvent event) {
    }
    @FXML
    private void lanzarWeb(ActionEvent event) {
        // Identificamos qué botón se ha pulsado (Amazon o Ebay)
        Button botonPulsado = (Button) event.getSource();
        String idBoton = botonPulsado.getId(); // Usaremos el ID de Scene Builder
        //Creamos el objeto WebView (el navegador)
        WebView webView = new WebView();

        //Si el boton seleccionado es igual a la opcion en comprar
        if (idBoton.equals("amazon")) {
            if (compraAmazon.isSelected()) {
                //Carga la página de amazon
                webView.getEngine().load("https://www.amazon.es");
                borderPane.setCenter(webView);
                //Cambiamos el texto en el labelEstado
                labelEstado.setText("Visitando Amazon"); 
            } else { //Si la opcion es diferenete al boton muestra error
                mostrarError("Amazon");
            }
        } 
        //Repetimos el proceso con el boton de ebay
        else if (idBoton.equals("ebay")) {
            if (compraEbay.isSelected()) {
                //Carga la página
                webView.getEngine().load("http://www.ebay.es");
                //Centra la página en el centro
                borderPane.setCenter(webView);
                //Cambiamos el texto en el labelEstdo
                labelEstado.setText("Visitando Ebay");
            } else {
                mostrarError("Ebay");
            }
        }
        else if(idBoton.equals("google")){
            webView.getEngine().load("https://www.google.com/?zx=1777287533686");
            borderPane.setCenter(webView);
            labelEstado.setText("Visitando Google");
        }
    } 
    //Dialogo de error
    private void mostrarError(String tienda) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //Titulo del recuadro
        alert.setTitle("Error en la selección");
        //Mensaje de alerta
        alert.setHeaderText("No puede comprar en " + tienda + ".");
        //Solucion al problema
        alert.setContentText("Cambie la selección actual en el menú Opciones");
        alert.showAndWait(); 
    }
    @FXML
    private void seleccionarBlog(ActionEvent event) {
        List<String> choices = new ArrayList<>();
        choices.add("El blog de Athos");
        choices.add("El blog de Porthos");
        choices.add("El blog de Aramis");

        // "El blog de Athos" será la opción por defecto
        ChoiceDialog<String> dialog = new ChoiceDialog<>("El blog de Athos", choices);
        dialog.setTitle("Selecciona un blog");
        dialog.setHeaderText("¿Qué blog quieres visitar?");
        dialog.setContentText("Elige:");

        Optional<String> result = dialog.showAndWait();
    
        // Si el usuario elige algo, actualizamos la barra de estado
        result.ifPresent(nombreBlog -> {
             labelEstado.setText("Visitando " + nombreBlog);
        });
    }
    @FXML
    private void escribeUser(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog("Felix");
        dialog.setTitle("Introduce tu nombre");
        //Texto principal
        dialog.setHeaderText("¿Con qué usuario quieres escribir en Facebook?");
        dialog.setContentText("Introduce tu nombre:");//Texto antes del recuadro
        
        Optional<String> result = dialog.showAndWait();
        //Obtiene el resultado del texto
        if (result.isPresent()){
            System.out.println("Hola " + result.get());
        }
    }
}
