/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;



import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Persona;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * FXML Controller class
 *
 * @author jsoler
 */
public class VistaTablaController implements Initializable {

    private ObservableList<Persona> datos = null; // Colecci�n vinculada a la vista.

    @FXML
    private Button addButton;
    @FXML
    private Button modificarButton;
    @FXML
    private Button borrarButton;
    @FXML
    private TableColumn<Persona, String> nombreColumn;
     @FXML
    private TableColumn<Persona, String> imagenPathColumn;
    @FXML
    private TableColumn<Persona, String> apellidosColumn; //tipo objeto fila, tipo objeto columna
    @FXML
    private TableView<Persona> personasTableV; //tipo objetos muestra el tableview

    private void inicializaModelo() {
        ArrayList<Persona> misdatos = new ArrayList<Persona>();
        misdatos.add(new Persona("Pepe", "García", "/resources/images/Lloroso.png"));
        misdatos.add(new Persona("María", "Pérez", "/resources/images/Sonriente.png"));
        datos = FXCollections.observableList(misdatos);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        inicializaModelo();
        //asigna lista observable a tableview
        personasTableV.setItems(datos);
        
        // Si hay error, se detecta antes de ejecutar el programa
        //Definimos que mostrar en cada columna
        //Para la columna de nombres, la función accede al valor del objeto y retorna el método NombreProperty().
        nombreColumn.setCellValueFactory(personaFila ->new SimpleStringProperty(personaFila.getValue().getNombre()));
        //método setCellValueFactory establece una conexión entre la columna de la tabla y una propiedad específica del objeto
        apellidosColumn.setCellValueFactory(personaFila -> new SimpleStringProperty(personaFila.getValue().getApellidos()));
        imagenPathColumn.setCellValueFactory(personaFila -> new SimpleStringProperty(personaFila.getValue().getImagenPath()));
        imagenPathColumn.setCellFactory(c -> new ImagenTabCell());
        
        
    }
    private boolean mostrarVentanaEdicion(Persona p) throws IOException {
        //Cargamos el archivo FXML de la ventana de edición
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPersona_1.fxml"));
        //FXMLLoader se encarga de convertir el XML en objetos Java
        Stage stage = loader.load();

        // Obtener el controlador de la ventana secundaria
        //vital para poder pasarle datos a la ventana antes de que se muestre
        VistaPersonaController_1 controller = loader.getController();
        controller.setPersona(p); // Le pasamos la persona a editar

        
        stage.setTitle("Editar Persona");
        //Modality.APPLICATION_MODAL impide que el usuario interactúe con la ventana principal
        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
        //El programa se queda "esperando" en esta línea hasta que la ventana secundaria se oculta
        stage.showAndWait(); // Espera a que el usuario cierre esta ventana

        return controller.isOKPressed();
    }
    
    
    @FXML
    private void modificarPersona(ActionEvent event) throws IOException {
        //Obtenemos la persona seleccionada de la tabla
        Persona seleccionada = personasTableV.getSelectionModel().getSelectedItem();
        //Llamamos al método de la imagen
        if (seleccionada != null) {
            boolean aceptarPulsado = mostrarVentanaEdicion(seleccionada);
            if (aceptarPulsado) {
                //Si aceptó, refrescamos la tabla para que se vean los cambios
                personasTableV.refresh(); // Refresca la tabla para ver los cambios
            }
        }
    }
    @FXML
    private void addPersona(ActionEvent event) throws IOException {
        // 1. Creamos una persona nueva con datos vacíos y una imagen por defecto
        Persona nueva = new Persona("", "", "/resources/images/Pregunta.png");

        // 2. Usamos el método que ya tienes para abrir la ventana
        // Pasamos la persona vacía para que el usuario rellene los datos
        boolean aceptarPulsado = mostrarVentanaEdicion(nueva);

        // 3. Si el usuario pulsó 'Aceptar', la añadimos a la lista observable
        if (aceptarPulsado) {
            datos.add(nueva);
            // Al añadir a la ObservableList, la tabla se actualiza automáticamente
        }
    }
    @FXML
    private void borrarPersona(ActionEvent event) {
        // 1. Miramos qué fila está seleccionada en la tabla
        int indiceSeleccionado = personasTableV.getSelectionModel().getSelectedIndex();

        // 2. Si hay algo seleccionado (índice diferente a -1)
        if (indiceSeleccionado >= 0) {
            // Borramos de la lista de datos
            datos.remove(indiceSeleccionado);
        } else {
            // Opcional: podrías mostrar una alerta aquí
            System.out.println("Por favor, selecciona una persona para borrar.");
        }
    }

    
    class ImagenTabCell extends TableCell<Persona, String> {
        private ImageView view = new ImageView();
        private Image imagen;
        @Override
        protected void updateItem(String t, boolean bln) {
            super.updateItem(t, bln); 
            if (t == null || bln) {
                setText(null);
                setGraphic(null);
            } else {
                imagen = new Image(t, 25, 25, true, true);
                view.setImage(imagen);
                setGraphic(view);
              }
        }
    }
}
