package mapademo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

public class HistorialSesionesController implements Initializable {

    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, String> colDate;
    @FXML private TableColumn<Session, String> colDuration;
    @FXML private TableColumn<Session, Integer> colImported;
    @FXML private TableColumn<Session, Integer> colViewed;
    @FXML private TableColumn<Session, Integer> colAnnotations;
    
    @FXML private Label lblTotalImported;
    @FXML private Label lblTotalViewed;
    @FXML private Label lblTotalAnnotations;
    @FXML private Button btnClose;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Enlazamos las columnas con los datos de la clase Session
        colDate.setCellValueFactory(new PropertyValueFactory<>("startTime"));    
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colImported.setCellValueFactory(new PropertyValueFactory<>("importedActivities"));
        colViewed.setCellValueFactory(new PropertyValueFactory<>("viewedActivities"));
        colAnnotations.setCellValueFactory(new PropertyValueFactory<>("annotationsCreated"));

        // 2. Obtenemos el usuario y sus sesiones desde la librería
        SportActivityApp app = SportActivityApp.getInstance();
        User user = app.getCurrentUser();
        
        if (user != null) {
            List<Session> sessions = user.getSessions();
            sessionTable.setItems(FXCollections.observableArrayList(sessions));

            // 3. Calculamos los totales acumulados
            int totalImp = 0, totalView = 0, totalAnnot = 0;
            for (Session s : sessions) {
                totalImp += s.getImportedActivities();
                totalView += s.getViewedActivities();
                totalAnnot += s.getAnnotationsCreated();
            }

            // 4. Mostramos los totales en los Labels que pusiste en el HBox
            lblTotalImported.setText("Total Imported: " + totalImp);
            lblTotalViewed.setText("Total Viewed: " + totalView);
            lblTotalAnnotations.setText("Total Annotations: " + totalAnnot);
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        // Cerramos la ventana actual
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}