package mapademo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.Duration;
import javafx.scene.control.TableCell;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox; // Importante para la caja vacía

public class HistorialSesionesController implements Initializable {

    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, LocalDateTime> colDate; 
    @FXML private TableColumn<Session, LocalDateTime> colTime; 
    @FXML private TableColumn<Session, Duration> colDuration;  
    @FXML private TableColumn<Session, Integer> colImported;
    @FXML private TableColumn<Session, Integer> colViewed;
    @FXML private TableColumn<Session, Integer> colAnnotations;
    
    @FXML private ComboBox<String> comboFiltro;
    
    // --- AQUÍ ESTÁ LA CORRECCIÓN CLAVE ---
    @FXML private VBox chartContainer; // La caja vacía que pusimos en Scene Builder
    private BarChart<String, Number> sesionesChart; // La gráfica (sin @FXML)
    // -------------------------------------

    @FXML private Label lblTotalImported;
    @FXML private Label lblTotalViewed;
    @FXML private Label lblTotalAnnotations;
    @FXML private Button btnClose;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // --- DIBUJAMOS LA GRÁFICA MÁGICAMENTE DESDE EL CÓDIGO ---
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        sesionesChart = new BarChart<>(xAxis, yAxis);
        
        sesionesChart.setPrefHeight(250.0);
        javafx.scene.layout.VBox.setVgrow(sesionesChart, javafx.scene.layout.Priority.ALWAYS);
        
        // Metemos la gráfica dentro de la caja vacía
        chartContainer.getChildren().add(sesionesChart);
        // ---------------------------------------------------------

        // Enlaces de columnas
        colDate.setCellValueFactory(new PropertyValueFactory<>("startTime"));    
        colTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));    
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colImported.setCellValueFactory(new PropertyValueFactory<>("importedActivities"));
        colViewed.setCellValueFactory(new PropertyValueFactory<>("viewedActivities"));
        colAnnotations.setCellValueFactory(new PropertyValueFactory<>("annotationsCreated"));

        // Formato Fecha
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colDate.setCellFactory(column -> new TableCell<Session, LocalDateTime>() {
            @Override protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatoFecha.format(item));
            }
        });

        // Formato Hora
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        colTime.setCellFactory(column -> new TableCell<Session, LocalDateTime>() {
            @Override protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatoHora.format(item));
            }
        });

        // Formato Duración
        colDuration.setCellFactory(column -> new TableCell<Session, Duration>() {
            @Override protected void updateItem(Duration item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); } 
                else {
                    long horas = item.toHours();
                    long minutos = item.toMinutesPart();
                    long segundos = item.toSecondsPart();
                    if (horas > 0) setText(horas + "h " + minutos + "m");
                    else if (minutos > 0) setText(minutos + "m " + segundos + "s");
                    else setText(segundos + "s");
                }
            }
        });

        // Configurar el ComboBox de filtros
        comboFiltro.getItems().addAll("All Time", "Last 7 Days", "Last 30 Days");
        comboFiltro.getSelectionModel().selectFirst();
        
        // Cuando el usuario cambia el filtro, llamamos a la función
        comboFiltro.setOnAction(e -> aplicarFiltro());

        // Cargar los datos por primera vez
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        User user = SportActivityApp.getInstance().getCurrentUser();
        if (user == null) return;

        List<Session> todasLasSesiones = user.getSessions();
        List<Session> sesionesFiltradas = new ArrayList<>();
        
        String filtroActual = comboFiltro.getValue();
        LocalDateTime ahora = LocalDateTime.now();

        // 1. FILTRAR DATOS
        for (Session s : todasLasSesiones) {
            if ("All Time".equals(filtroActual)) {
                sesionesFiltradas.add(s);
            } else if ("Last 7 Days".equals(filtroActual)) {
                if (s.getStartTime().isAfter(ahora.minusDays(7))) {
                    sesionesFiltradas.add(s);
                }
            } else if ("Last 30 Days".equals(filtroActual)) {
                if (s.getStartTime().isAfter(ahora.minusDays(30))) {
                    sesionesFiltradas.add(s);
                }
            }
        }

        // 2. ACTUALIZAR TABLA
        sessionTable.setItems(FXCollections.observableArrayList(sesionesFiltradas));

        // 3. ACTUALIZAR GRÁFICA Y TOTALES
        sesionesChart.getData().clear(); // Limpiamos gráfica anterior
        
        XYChart.Series<String, Number> seriesImported = new XYChart.Series<>();
        seriesImported.setName("Imported");
        XYChart.Series<String, Number> seriesViewed = new XYChart.Series<>();
        seriesViewed.setName("Viewed");
        XYChart.Series<String, Number> seriesAnnotations = new XYChart.Series<>();
        seriesAnnotations.setName("Annotations");

        int totalImp = 0, totalView = 0, totalAnnot = 0;
        int numSesion = 1;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM");

        for (Session s : sesionesFiltradas) {
            totalImp += s.getImportedActivities();
            totalView += s.getViewedActivities();
            totalAnnot += s.getAnnotationsCreated();

            String xLabel = "S" + numSesion + " (" + df.format(s.getStartTime()) + ")";
            seriesImported.getData().add(new XYChart.Data<>(xLabel, s.getImportedActivities()));
            seriesViewed.getData().add(new XYChart.Data<>(xLabel, s.getViewedActivities()));
            seriesAnnotations.getData().add(new XYChart.Data<>(xLabel, s.getAnnotationsCreated()));
            
            numSesion++;
        }

        sesionesChart.getData().addAll(seriesImported, seriesViewed, seriesAnnotations);

        lblTotalImported.setText("Total Imported: " + totalImp);
        lblTotalViewed.setText("Total Viewed: " + totalView);
        lblTotalAnnotations.setText("Total Annotations: " + totalAnnot);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}