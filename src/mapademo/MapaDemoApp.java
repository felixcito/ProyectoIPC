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

        // --- TRUCO PARA PRUEBAS: Login automático ---
        // Pon aquí un usuario y contraseña que sepas que ya existen en tu base de datos
        //upv.ipc.sportlib.SportActivityApp.getInstance().login("jgarcia", "passPER21!"); 
        // --------------------------------------------

        // Cambiamos el archivo que se carga al arrancar
        Parent root = FXMLLoader.load(getClass().getResource("LoginVisita.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Running la Safor - Mapa (Modo Pruebas)");
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
