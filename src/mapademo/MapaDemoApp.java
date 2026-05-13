/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapademo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

/**
 *
 * @author jose
 */
public class MapaDemoApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        //Ahora cargamos la pagina login primero
        Parent root = FXMLLoader.load(getClass().getResource("LoginVisita.fxml"));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        Scene scene = new Scene(root);
        //stage.setTitle("Demo mapas - IPC");
        stage.setTitle("Running la Safor - Login"); 
        stage.setScene(scene);
        stage.show();
    }

    //Este método se ejecuta automáticamente al cerrar la ventana
    @Override
    public void stop() throws Exception {
        try {
            // Guardamos las estadísticas de la sesión en la base de datos
            SportActivityApp.getInstance().logout();
            System.out.println("Sesión cerrada y guardada correctamente.");
        } catch (Exception e) {
            System.out.println("Error al cerrar la sesión: " + e.getMessage());
        }
        super.stop();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
