package controlador;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;

import modelo.Persona;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;

public class VistaListaControlador implements Initializable {
    //=========================================================
    @FXML
    private ListView<Persona> personasListView;
    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField apellidosTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button borrarButton;

    //=========================================================
    // DEBEN conincidir los tipo del ListView y de la lista observable
    private ObservableList<Persona> datos = null; // Coleccion vinculada a la vista.

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // en el código de inicialización del controlador
        ArrayList<Persona> misdatos = new ArrayList<Persona>();
        //misdatos.add(new Persona("Pepe", "García"));
        //misdatos.add(new Persona("María", "Pérez"));
        
        //=======================================================
        // creamos la lista observable mediante un metodo de FXCollections
        datos = FXCollections.observableArrayList(misdatos);
        
        personasListView.setItems(datos);
        personasListView.setCellFactory(c -> new PersonListCell());
        
        //=======================================================
        //=======================================================
        // vinculamos la lista observables de personas con el ListView

        
        //=======================================================
        //=======================================================
        // Hay que modificar CellFactory para mostrar el objeto Persona
     
        
        //=======================================================
        // inhabilitar botones al arrancar.
        addButton.setDisable(true);
        // si no hay selección el boton Borrar esta disabled
        borrarButton.disableProperty().bind(Bindings.equal(personasListView.getSelectionModel().selectedIndexProperty(), -1));

        // oyente de foco para el textfield.
        nombreTextField.focusedProperty().addListener((a, b, c) -> {
            // TODO Auto-generated method stub
            if (nombreTextField.isFocused()) {
                addButton.setDisable(false);
                personasListView.getSelectionModel().select(-1);
            }
        });

        // oyente de foco para el listView
        personasListView.focusedProperty().addListener((a, b, c) -> {
            if (personasListView.isFocused()) {
                addButton.setDisable(true);
            }
        });
        
    }

    @FXML
    void addAccion(ActionEvent event) {
        // añade a la colección si los campos no son vacíos y no contienen únicamente blancos
        if ((!nombreTextField.getText().isEmpty())
                && (nombreTextField.getText().trim().length() != 0)
                && (!apellidosTextField.getText().isEmpty())
                && (apellidosTextField.getText().trim().length() != 0)) {
            //============================================
            // añadimos a la lista
            Persona p = new Persona(nombreTextField.getText().trim(), apellidosTextField.getText().trim());
            datos.add(p);
            
            //============================================
            // vaciamos los campos despues de añadir a la lista
            nombreTextField.clear();
            apellidosTextField.clear();
            //cambio del foco al textfield inicial
            nombreTextField.requestFocus();

        }
    }

    @FXML
    void borrarAccion(ActionEvent event) {
        //================================================
        // borramos de la lista
        datos.remove(personasListView.getSelectionModel().getSelectedIndex());
        
        //================================================
    }
    class PersonListCell extends ListCell<Persona>{
        @Override
        protected void updateItem(Persona item, boolean empty){
            super.updateItem(item, empty);
            if(item == null || empty) setText(null);
            else setText(item.getNombre() + ", " + item.getApellidos() );
        }
    }

}
