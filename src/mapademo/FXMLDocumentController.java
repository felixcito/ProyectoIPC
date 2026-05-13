/*
 * ============================================================
 *  PROYECTO EJEMPLO – IPC 2026
 *  Asignatura: Interfaces Persona-Computador
 *  Universitat Politècnica de València
 * ============================================================
 *
 *  DESCRIPCIÓN GENERAL
 *  -------------------
 *  Este controlador gestiona la vista principal de la aplicación
 *  de puntos de interés (POI) sobre un mapa.
 *
 * Funcionalidades implementadas:
 * 1. Carga y visualización de una imagen de mapa dinámica según la ruta.
 * 2. Zoom interactivo mediante un Slider.
 * 3. Importación de ficheros GPX.
 * 4. Añadir anotaciones personalizadas en el mapa con clic derecho.
 * 5. Listado de Actividades en un ListView con CellFactory personalizada.
 * 6. Dibujado del trazado y centrado animado al seleccionar una ruta.
 *
 * PATRÓN UTILIZADO: MVC (Model-View-Controller)
 * - Modelo : Clases Activity y Annotation (Librería IPC2026)
 * ============================================================
 */
package mapademo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.Annotation;
import upv.ipc.sportlib.AnnotationType;
import upv.ipc.sportlib.GeoPoint;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.TrackPoint;

/**
 * Controlador principal de la aplicación de gestión de actividades deportivas.
 *
 * La anotación @FXML conecta automáticamente los campos de esta clase
 * con los elementos declarados en el fichero FXML mediante su atributo fx:id.
 *
 * Implementa {@link Initializable} para poder ejecutar código de
 * inicialización una vez que el FXML ha sido cargado completamente.
 */
public class FXMLDocumentController implements Initializable {

    // =========================================================
    //  ESTRUCTURA DE NODOS PARA ZOOM
    // =========================================================
    //
    //  El zoom se consigue escalando un Group (zoomGroup).
    //  Escalar un Group NO desplaza los nodos que contiene,
    //  lo que evita el "salto" visual al hacer zoom.
    //
    //  Jerarquía de nodos:
    //
    //  ScrollPane (map_scrollpane)
    //   └─ contentGroup          ← Group raíz dentro del ScrollPane
    //       └─ zoomGroup         ← se escala para el zoom
    //           └─ mapPane       ← Pane con la imagen y los POIs
    //               ├─ ImageView ← imagen del mapa
    //               ├─ Text      ← etiquetas de POIs
    //               └─ Circle    ← anotaciones circulares
    //
    // =========================================================

    /** Group que se escala para aplicar el zoom. */
    private Group zoomGroup;

    /**
     * Pane que actúa como lienzo del mapa.
     * Contiene la imagen de fondo y todos los elementos superpuestos
     * (textos, círculos, etc.). Sus dimensiones coinciden con las de
     * la imagen cargada.
     */
    private Pane mapPane;
    private Activity actividadActual; // Guarda la ruta que has importado
    private MapProjection proj;       // Necesaria para convertir píxeles a GPS
    

    
    /** Menú contextual reutilizable para el clic derecho sobre el mapa. */
    private ContextMenu mapContextMenu;

 
    // =========================================================
    //  ELEMENTOS FXML  (inyectados automáticamente por el cargador)
    // =========================================================

    /** Lista lateral que muestra todos los POIs añadidos al mapa. */
    @FXML
    private ListView<Activity> map_listview;

    /** ScrollPane que envuelve el mapa y permite desplazarlo. */
    @FXML
    private ScrollPane map_scrollpane;
    
    /** Grafica de la altitud y distancia de la actividad */
    @FXML
    private LineChart<Number, Number> chartDesnivel;

    /**
     * Slider de zoom.
     * Rango: [0.5 – 1.5]. Valor inicial: 1.0 (sin zoom).
     * Cada cambio de valor llama al método zoom().
     */
    @FXML
    private Slider zoom_slider;
    
    // Este es el puntito que se moverá por el mapa
    private Circle punteroMapa = new Circle(6, Color.MAGENTA);

    /**
     * Botón de pin visible sobre el mapa.
     * Se desplaza hasta la posición del POI seleccionado en la lista.
     */
    private MenuButton map_pin;

    // FIX 5 — Eliminadas las variables sin uso:
    //   · 'mousePosistion' (errata + duplicado de mousePosition)
    //   · 'pin_info'       (inyectada pero nunca actualizada)

    /** Etiqueta en la barra de estado que muestra las coordenadas del ratón. */
    @FXML
    private Label mousePosition;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label labelDistancia;
    @FXML
    private Label labelDuracion;
    @FXML
    private Label labelDesnivel;
    @FXML
    private Label labelVelocidadMedia;
    @FXML
    private Label labelRitmoMedio;
    @FXML
    private Label labelAltitudMinima;
    @FXML
    private Label labelAltitudMaxima;
    @FXML
    private Label labelDesnivelNegativo;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private NumberAxis xAxis;
 

    // =========================================================
    //  MANEJADORES DE ZOOM
    // =========================================================

    /**
     * Aumenta el zoom en 0.1 unidades al pulsar el botón "+".
     *
     * @param event evento de acción del botón
     */
    @FXML
    void zoomIn(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + 0.1);
    }

    /**
     * Reduce el zoom en 0.1 unidades al pulsar el botón "–".
     *
     * @param event evento de acción del botón
     */
    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal - 0.1);
    }

    /**
     * Aplica el factor de escala al {@code zoomGroup}.
     *
     * Este método es invocado automáticamente cada vez que cambia el
     * valor del slider, gracias al listener registrado en {@link #initialize}.
     *
     * Truco: guardamos y restauramos los valores de scroll para que el
     * contenido visible no salte al cambiar la escala.
     *
     * @param scaleValue nuevo factor de escala (p. ej. 1.2 → 120 %)
     */
    private void zoom(double scaleValue) {
        // Guardamos la posición del scroll antes de escalar
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        // Aplicamos el zoom escalando el Group en ambos ejes
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        // Restauramos la posición del scroll para que el centro visual
        // permanezca estable durante el zoom
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    // =========================================================
    //  SELECCIÓN EN EL LISTVIEW → CENTRADO EN EL MAPA
    // =========================================================

    /**
     * Se ejecuta cuando el usuario hace clic en un elemento del ListView.
     *
     * Objetivo: centrar el ScrollPane sobre la posición del POI seleccionado
     * con una animación suave de 500 ms, y mover el pin al punto.
     *
     * Cálculo del scroll
     * ------------------
     * El ScrollPane expresa su posición como valores normalizados [0, 1]:
     *   · hValue = 0 → extremo izquierdo
     *   · hValue = 1 → extremo derecho
     *
     * Para centrar el POI necesitamos:
     *
     *   scrollH = (poiX_escalado - viewportAncho / 2)
     *             ─────────────────────────────────────
     *             (mapaAncho_escalado - viewportAncho)
     *
     * Aplicamos clamp para no salir del rango [0, 1].
     *
     * @param event evento de ratón sobre el ListView
     */
    @FXML
    void listClicked(MouseEvent event) {
        
        // 1. Obtenemos la Actividad (no el Poi) seleccionada
        Activity seleccionada = map_listview.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        // 2. CARGA DE DATOS        
        this.actividadActual = seleccionada; 
        dibujarRuta(seleccionada); // Este método ya pinta la línea y prepara el 'proj'

        // Actualizamos las etiquetas de texto con los números de esta ruta 
        labelDistancia.setText("Distancia: " + String.format("%.2f", seleccionada.getTotalDistance() / 1000.0) + " km");
        labelDuracion.setText("Duración: " + seleccionada.getDuration().toMinutes() + " min");
        labelVelocidadMedia.setText("Velocidad media: " + String.format("%.2f", seleccionada.getAverageSpeed()) + " km/h");
        labelRitmoMedio.setText("Ritmo medio: " + seleccionada.getAveragePace() + " min/km");
        labelDesnivel.setText("Desnivel+: " + seleccionada.getElevationGain() + " m");
        labelDesnivelNegativo.setText("Desnivel-: " + seleccionada.getElevationLoss() + " m");
        labelAltitudMinima.setText("Altitud mín: " + seleccionada.getMinElevation() + " m");
        labelAltitudMaxima.setText("Altitud máx: " + seleccionada.getMaxElevation() + " m");

        // Dibujamos las anotaciones que ya estaban guardadas en la base de datos 
        for (Annotation nota : seleccionada.getAnnotations()) {
            Point2D p = proj.project(nota.getGeoPoints().get(0));
            // 1. Creamos el texto primero
            Text t = new Text(nota.getText());

            // 2. Le asignamos las coordenadas X e Y
            t.setX(p.getX());
            t.setY(p.getY());

            // 3. Le ponemos el estilo (esta línea ya la tenías)
            t.setStyle("-fx-fill: " + nota.getColor() + "; -fx-font-weight: bold;");
            mapPane.getChildren().add(t);
        }

        // 3. ANIMACIÓN Y CENTRADO (Lo que tenía tu código original)
        // Usamos el punto de inicio de la ruta para centrar el mapa
        Point2D inicioEscalado = proj.project(seleccionada.getStartPoint());
        double poiX = inicioEscalado.getX() * zoomGroup.getScaleX();
        double poiY = inicioEscalado.getY() * zoomGroup.getScaleY();

        double mapWidth  = mapPane.getWidth()  * zoomGroup.getScaleX();
        double mapHeight = mapPane.getHeight() * zoomGroup.getScaleY();
        double viewW = map_scrollpane.getViewportBounds().getWidth();
        double viewH = map_scrollpane.getViewportBounds().getHeight();

        double scrollH = Math.max(0, Math.min(1, (poiX - viewW / 2) / (mapWidth  - viewW)));
        double scrollV = Math.max(0, Math.min(1, (poiY - viewH / 2) / (mapHeight - viewH)));

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), 
            new KeyValue(map_scrollpane.hvalueProperty(), scrollH),
            new KeyValue(map_scrollpane.vvalueProperty(), scrollV)
        ));
        timeline.play(); 
        
        /** actualiza la grafica de la actividad */
        actualizarGrafica(seleccionada);
    }

    // =========================================================
    //  CONSTRUCCIÓN DEL MAPA
    // =========================================================

    /**
     * Carga una imagen y construye la jerarquía de nodos del mapa.
     *
     * Este método puede llamarse varias veces (p. ej. al cambiar el mapa),
     * ya que sustituye completamente el contenido del ScrollPane.
     *
     * @param imgFile fichero de imagen a cargar como fondo del mapa
     */
    private void buildMap(File imgFile) {
        // Comprobación defensiva: si el fichero no existe mostramos un aviso
        if (!imgFile.exists()) {
            map_scrollpane.setContent(
                new Label("Imagen no encontrada: " + imgFile.getPath()));
            return;
        }

        // Cargamos la imagen y obtenemos sus dimensiones reales en píxeles
        Image img = new Image(imgFile.toURI().toString());
        double W = img.getWidth();
        double H = img.getHeight();

        // ── mapPane: lienzo del mapa ───────────────────────────────────
        // Usamos un Pane (y no un Group) para poder posicionar los nodos
        // hijos con coordenadas absolutas (setLayoutX / setLayoutY).
        mapPane = new Pane();
        mapPane.setPrefSize(W, H); // tamaño preferido = tamaño de la imagen
        mapPane.setMinSize(W, H);  // impedimos que el layout lo encoja
        mapPane.setMaxSize(W, H);  // impedimos que el layout lo agrande

        // Añadimos la imagen como fondo del Pane
        ImageView iv = new ImageView(img);
        iv.setFitWidth(W);
        iv.setFitHeight(H);
        mapPane.getChildren().add(iv);

       // ── Manejador de clics sobre el mapa ──────────────────────────
        // Gestionamos el clic derecho (menú contextual) para añadir anotaciones.
        
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // Clic derecho → mostrar menú contextual para añadir tus Anotaciones
                onMapRightClick(e.getX(), e.getY());
            } 
            // Hemos eliminado el 'else if' antiguo porque ya no usamos los POIs
        });

        // ── Jerarquía de Groups para el zoom ──────────────────────────
        // contentGroup es el nodo raíz que recibe el ScrollPane.
        // zoomGroup es el que se escala; anidar un Group dentro de otro
        // evita que el ScrollPane reajuste su contenido durante el escalado.
        zoomGroup = new Group();
        Group contentGroup = new Group();
        zoomGroup.getChildren().add(mapPane);
        contentGroup.getChildren().add(zoomGroup);

        // Aplicamos el zoom actual (valor actual del slider)
        double zoom = zoom_slider.getValue();
        zoomGroup.setScaleX(zoom);
        zoomGroup.setScaleY(zoom);

        // Asignamos el contentGroup como contenido del ScrollPane
        map_scrollpane.setContent(contentGroup);

    }

    // =========================================================
    //  MENÚ CONTEXTUAL (clic derecho sobre el mapa)
    // =========================================================

    /**
     * Muestra el menú contextual reutilizable en la posición del clic.
     *
     * Las acciones de los MenuItem se actualizan con las coordenadas
     * del clic actual antes de mostrar el menú.
     *
     * @param x coordenada X del clic en el sistema local del mapPane
     * @param y coordenada Y del clic en el sistema local del mapPane
     */
   private void onMapRightClick(double x, double y) {
        // cerramos el menú si ya estaba visible
        mapContextMenu.hide();

        // Actualizamos las acciones con las coordenadas actuales.
        final double clickX = x;
        final double clickY = y;
        
  
        mapContextMenu.getItems().get(0).setOnAction(e -> crearAnotacionReal(clickX, clickY));

        // Mostramos el menú en coordenadas de pantalla
        mapContextMenu.show(
            mapPane.getScene().getWindow(),
            mapPane.localToScreen(x, y).getX(),
            mapPane.localToScreen(x, y).getY()
        );
    }

    // =========================================================
    //  INICIALIZACIÓN DEL CONTROLADOR
    // =========================================================

    /**
     * Método llamado automáticamente por el FXMLLoader tras inyectar
     * todos los elementos {@code @FXML}.
     *
     * Aquí configuramos:
     *  - El slider de zoom y su listener.
     *  - El ContextMenu reutilizable (FIX 6).
     *  - La CellFactory del ListView (FIX 4).
     *  - La carga del mapa inicial.
     *
     * @param url  URL del documento FXML (no usado aquí)
     * @param rb   paquete de recursos de internacionalización (no usado aquí)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ── Configuración del slider de zoom ──────────────────────────
        zoom_slider.setMin(0.5);   // zoom mínimo: 50 %
        zoom_slider.setMax(1.5);   // zoom máximo: 150 %
        zoom_slider.setValue(1.0); // valor inicial: 100 %

        // Listener que invoca zoom() cada vez que el slider cambia de valor.
        // Usamos una expresión lambda en lugar de una clase anónima por brevedad.
        zoom_slider.valueProperty().addListener(
            (observable, oldVal, newVal) -> zoom((Double) newVal)
        );

        // Los items se crean aquí sin acción; las acciones se asignan
        // en onMapRightClick() con las coordenadas correctas de cada clic.
        MenuItem miText   = new MenuItem("📝 Añadir texto");
        MenuItem miCircle = new MenuItem("⭕ Añadir círculo");
        mapContextMenu = new ContextMenu(miText, miCircle);

       
        map_listview.setCellFactory(listView -> new ListCell<Activity>() {
            @Override
            protected void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);
                if (empty || activity == null) {
                    setText(null);
                } else {
                    // Muestra el nombre de la ruta, o "Actividad sin nombre" si no tiene
                    setText(activity.getName() != null ? activity.getName() : "Actividad " + activity.getId());
                }
            }
        });

        // ── Carga de actividades al arrancar la pantalla ───────────────
        // ¡Las ponemos aquí fuera para que solo se ejecute UNA VEZ!
        List<Activity> misActividades = SportActivityApp.getInstance().getUserActivities();
        map_listview.getItems().addAll(misActividades);

        // ── Carga del mapa inicial ─────────────────────────────────────
        // El fichero se busca relativo al directorio de trabajo del proyecto.
        buildMap(new File("maps/upv.jpg"));
    }

    // =========================================================
    //  INDICADOR DE POSICIÓN DEL RATÓN
    // =========================================================

    /**
     * Actualiza la etiqueta {@code mousePosition} con las coordenadas
     * actuales del ratón, tanto en el sistema de la escena como en el
     * sistema local del nodo sobre el que se mueve.
     *
     * Útil para depuración y para que los alumnos comprendan la diferencia
     * entre coordenadas de escena y coordenadas locales.
     *
     * @param event evento de movimiento del ratón
     */
    @FXML
    private void showPosition(MouseEvent event) {
        mousePosition.setText(
            "sceneX: " + (int) event.getSceneX() +
            ", sceneY: " + (int) event.getSceneY() + "\n" +
            "         X: " + (int) event.getX() +
            ",          Y: " + (int) event.getY()
        );
    }

    // =========================================================
    //  DIÁLOGO "ACERCA DE"
    // =========================================================

    /**
     * Muestra un diálogo informativo con datos de la asignatura.
     *
     * Nota: accedemos al Stage del diálogo para poder personalizar
     * su icono, ya que Alert no expone directamente esa propiedad.
     *
     * @param event evento de acción del menú
     */
    @FXML
    private void about(ActionEvent event) {
        Alert mensaje = new Alert(Alert.AlertType.INFORMATION);

        // Personalizamos el icono de la ventana del diálogo
        Stage dialogStage = (Stage) mensaje.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(
            new Image(getClass().getResourceAsStream("/resources/logo.png"))
        );

        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("IPC - 2026");
        mensaje.showAndWait(); // Bloquea hasta que el usuario cierra el diálogo
    }

    
    // =========================================================
    //  CAMBIAR EL MAPA (selector de fichero)
    // =========================================================

    /**
     * Abre un selector de fichero para que el usuario elija una imagen
     * diferente como mapa y reconstruye toda la vista.
     *
     * FIX 3: se comprueba que imgFile no sea null antes de usarlo,
     * evitando NullPointerException cuando el usuario cierra el FileChooser
     * sin seleccionar ningún fichero.
     *
     * @param event evento de acción del menú
     * @throws IOException si hay un problema al obtener la ruta canónica
     */
    @FXML
    private void cambiarMapa(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(".")); // Empezamos en el directorio del proyecto

        File imgFile = fc.showOpenDialog(zoom_slider.getScene().getWindow());

        // FIX 3: showOpenDialog() devuelve null si el usuario cancela la selección
        if (imgFile != null) {
            System.out.println("Mapa seleccionado: " + imgFile.getCanonicalPath());
            buildMap(imgFile); // Reconstruimos la vista con la nueva imagen
            map_listview.getItems().clear(); // Borramos los datos del mapa anterior
        }
    }

    
    // =========================================================
    //  Importar GPX
    // =========================================================
    
    @FXML
    private void importarGPX(ActionEvent event) {
        // 1. Abrimos el buscador de archivos
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo GPX");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheros GPX", "*.gpx"));

        // Obtenemos la ventana actual para mostrar el diálogo
        File file = fc.showOpenDialog(zoom_slider.getScene().getWindow());

        if (file != null) {
            try {
                // 2. Importamos la actividad usando la librería
                SportActivityApp app = SportActivityApp.getInstance();
                Activity actividad = app.importActivity(file); // La librería procesa el GPX y lo guarda 
                this.actividadActual = actividad;
                this.proj = new MapProjection(actividad.getSuggestedMap(), mapPane.getWidth(), mapPane.getHeight()); 

                // 3. Mostramos las estadísticas en los Labels [cite: 201]
                labelDistancia.setText("Distancia: " + String.format("%.2f", actividad.getTotalDistance() / 1000.0) + " km");
                labelDuracion.setText("Duración: " + actividad.getDuration().toMinutes() + " min");
                labelDesnivel.setText("Desnivel+: " + actividad.getElevationGain() + " m");
                labelVelocidadMedia.setText("Velocidad media: " + String.format("%.2f", actividad.getAverageSpeed()) + " km/h");
                labelRitmoMedio.setText("Ritmo medio: " + actividad.getAveragePace() + " min/km");
                labelDesnivelNegativo.setText("Desnivel-: " + actividad.getElevationLoss() + " m");
                labelAltitudMinima.setText("Altitud mín: " + actividad.getMinElevation() + " m");
                labelAltitudMaxima.setText("Altitud máx: " + actividad.getMaxElevation() + " m");

                // 4. Dibujar ruta en el mapa
                dibujarRuta(actividad);
                
                // 5. Actualiza la grafica de la actividad
                actualizarGrafica(actividad);
                
            
            } catch (Exception e) {
                System.err.println("Error al cerrar sesión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // =========================================================
    //  Dibujar ruta
    // =========================================================
    
    private void dibujarRuta(Activity activity) {
        // 1. Obtenemos el mapa sugerido por la librería para esta ruta
        MapRegion region = activity.getSuggestedMap();
        if (region != null) {
            // Cargamos la imagen del mapa correcto (ej: pirineos.jpg o calderona.jpg)
            File imgFile = new File(region.getImagePath());
            buildMap(imgFile); // Usamos el método que ya venía en tu proyecto base
        }

        // 2. Creamos el objeto matemático para convertir coordenadas 
        this.proj = new MapProjection(region, mapPane.getWidth(), mapPane.getHeight());
        
        // 3 y 4. Recorremos los puntos de dos en dos para crear los pequeños tramos
        List<TrackPoint> puntosRuta = activity.getTrackPoints();
        for (int i = 0; i < puntosRuta.size() - 1; i++) {
            TrackPoint tp1 = puntosRuta.get(i);
            TrackPoint tp2 = puntosRuta.get(i + 1);

            Point2D p1 = proj.project(tp1);
            Point2D p2 = proj.project(tp2);

            // Calculamos la velocidad de este tramo en concreto en km/h
            double velocidad = tp1.speedTo(tp2);

            // Mapeamos la velocidad a un color (0 = Rojo, 120 = Verde)
            // Asumimos un tope de 20 km/h para el color verde máximo
            double tono = Math.max(0, Math.min(120, (velocidad / 20.0) * 120));
            Color colorTramo = Color.hsb(tono, 1.0, 0.8);

            // Creamos la línea para este pequeño tramo
            javafx.scene.shape.Line segmento = new javafx.scene.shape.Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            segmento.setStroke(colorTramo);
            segmento.setStrokeWidth(4.0);

            // 5. Añadimos el tramo al mapa
            mapPane.getChildren().add(segmento);
        }

        // 6. Añadimos círculo verde al inicio y rojo al final 
        Point2D pInicio = proj.project(activity.getStartPoint());
        Circle circleInicio = new Circle(pInicio.getX(), pInicio.getY(), 6, Color.GREEN);

        Point2D pFin = proj.project(activity.getEndPoint());
        Circle circleFin = new Circle(pFin.getX(), pFin.getY(), 6, Color.RED);

        mapPane.getChildren().addAll(circleInicio, circleFin);
    }
    
    // =========================================================
    //  Crear Anotacion
    // =========================================================
    
    private void crearAnotacionReal(double x, double y) {
        if (actividadActual == null) return; // Si no hay ruta cargada, no hacemos nada

        // 1. Creamos el diálogo (ventanita) para pedir datos
        Dialog<Annotation> dialog = new Dialog<>();
        dialog.setTitle("Nueva Anotación");
        dialog.setHeaderText("Añade una nota al mapa");

        // 2. Creamos los controles: un campo de texto y un selector de color
        TextField textField = new TextField();
        textField.setPromptText("Escribe aquí tu nota...");
        ColorPicker colorPicker = new ColorPicker(Color.RED);

        VBox vbox = new VBox(10, new Label("Texto de la anotación:"), textField, new Label("Color:"), colorPicker);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // 3. Cuando el usuario pulsa OK, construimos la anotación real
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                GeoPoint gp = proj.unproject(x, y); 

                // Convertimos el Color de JavaFX a formato Hexadecimal (#RRGGBB) que pide la librería
                String hexColor = String.format("#%02X%02X%02X",
                    (int)(colorPicker.getValue().getRed() * 255),
                    (int)(colorPicker.getValue().getGreen() * 255),
                    (int)(colorPicker.getValue().getBlue() * 255));

                // Creamos la anotación con los datos del usuario 
                return new Annotation(AnnotationType.TEXT, textField.getText(), hexColor, 2.0, List.of(gp));
            }
            return null;
        });

        // 4. Mostramos la ventana y si hay resultado lo guardamos y dibujamos
        Optional<Annotation> result = dialog.showAndWait();
        result.ifPresent(nota -> {
            SportActivityApp.getInstance().addAnnotation(actividadActual, nota); 

            Text t = new Text(x, y, nota.getText());
            t.setFill(colorPicker.getValue()); // Le ponemos el color que eligió
            t.setStyle("-fx-font-weight: bold;");
            mapPane.getChildren().add(t);
        });
    }
    
    // =========================================================
    //  Cerrar sesion
    // =========================================================
    
    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            // 1. Guardar y cerrar sesión en la librería del proyecto
            upv.ipc.sportlib.SportActivityApp.getInstance().logout();
            System.out.println("Sesión guardada en la base de datos.");

            // 2. Preparar el cargador para la vista de Login
            // Nota: Asegúrate de que el nombre coincide con tu archivo (LoginVisita.fxml) 
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginVisita.fxml"));
            Parent root = loader.load();

            // 3. Obtener la ventana actual (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 4. Cambiar la escena y mostrar el Login
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Running la Safor - Login");
            stage.show();

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // =========================================================
    //  Historial
    // =========================================================
    
    @FXML
    private void verHistorial(ActionEvent event) {
        try {
            // Cargamos el nuevo archivo FXML que has diseñado
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HistorialSesiones.fxml"));
            Parent root = loader.load();

            // Creamos una nueva ventana (Stage)
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Session History");

            // Esto hace que el usuario no pueda tocar la ventana principal hasta cerrar esta
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); 
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de historial: " + e.getMessage());
        }
    }

   @FXML
    private void mostrarTotales(ActionEvent event) {
            double distanciaTotal = 0;
            double ascensoTotal = 0;
            double descensoTotal = 0;
            long segundosTotales = 0;

            // Obtenemos todas las actividades de la base de datos
            List<Activity> actividades = SportActivityApp.getInstance().getUserActivities();

            // Sumamos todo
            for (Activity a : actividades) {
                distanciaTotal += a.getTotalDistance();
                ascensoTotal += a.getElevationGain();
                descensoTotal += a.getElevationLoss();
                segundosTotales += a.getDuration().getSeconds();
            }

            // Convertimos los segundos a horas y minutos para que se lea mejor
            long horas = segundosTotales / 3600;
            long minutos = (segundosTotales % 3600) / 60;

            // Mostramos un mensaje emergente (Alert) con los totales 
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Acumulado de actividades");
            alert.setHeaderText("Totales del usuario");
            alert.setContentText(
                "Distancia total: " + String.format("%.2f", distanciaTotal / 1000.0) + " km\n" +
                "Tiempo total: " + horas + "h " + minutos + "m\n" +
                "Ascenso total: " + ascensoTotal + " m\n" +
                "Descenso total: " + descensoTotal + " m"
            );
            alert.showAndWait();
        }
    
    private void actualizarGrafica(Activity activity) {
            // 1. Limpiamos la gráfica por si había otra ruta antes
            chartDesnivel.getData().clear();
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Perfil de elevación");

            double distanciaAcumulada = 0;
            List<TrackPoint> puntos = activity.getTrackPoints();

            // 2. Recorremos los puntos para calcular distancia y altitud [cite: 217, 271]
            for (int i = 0; i < puntos.size(); i++) {
                TrackPoint actual = puntos.get(i);

                if (i > 0) {
                    TrackPoint anterior = puntos.get(i - 1);
                    // Calculamos la distancia entre el punto anterior y el actual
                    distanciaAcumulada += actual.distanceTo(anterior);
                }

                // Añadimos el punto: (Distancia en km, Altitud en metros)
                series.getData().add(new XYChart.Data<>(distanciaAcumulada / 1000.0, actual.getElevation()));
            }

            // 3. Metemos los datos en la gráfica
            chartDesnivel.getData().add(series);

            // 4. INTERACCIÓN GRÁFICA -> MAPA
            // Activamos los símbolos para poder detectarlos con el ratón
            chartDesnivel.setCreateSymbols(true); 

            for (int i = 0; i < series.getData().size(); i++) {
                XYChart.Data<Number, Number> data = series.getData().get(i);
                final TrackPoint tpAsociado = puntos.get(i); // Guardamos qué punto del GPS es

                javafx.scene.Node node = data.getNode();
                if (node != null) {
                    // Hacemos el punto transparente para que se vea como una línea limpia
                    node.setStyle("-fx-background-color: transparent, transparent;");

                    // Cuando el ratón ENTRA al punto de la gráfica
                    node.setOnMouseEntered(e -> {
                        Point2D px = proj.project(tpAsociado); // Calculamos su posición en el mapa
                        punteroMapa.setCenterX(px.getX());
                        punteroMapa.setCenterY(px.getY());
                        if (!mapPane.getChildren().contains(punteroMapa)) {
                            mapPane.getChildren().add(punteroMapa);
                        }
                    });

                    // Cuando el ratón SALE del punto de la gráfica
                    node.setOnMouseExited(e -> {
                        mapPane.getChildren().remove(punteroMapa);
                    });
                }
            }
    }
    @FXML
    private void abrirVentanaAnadirMapa(ActionEvent event) {
        // 1. Creamos el diálogo
        Dialog<MapRegion> dialog = new Dialog<>();
        dialog.setTitle("Añadir Nuevo Mapa");
        dialog.setHeaderText("Introduce los datos del script generar_mapas_hd.py");

        // 2. Creamos los campos de texto
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la región (ej: Montanejos)");
        TextField txtLatMin = new TextField();
        txtLatMin.setPromptText("Latitud Mínima");
        TextField txtLatMax = new TextField();
        txtLatMax.setPromptText("Latitud Máxima");
        TextField txtLonMin = new TextField();
        txtLonMin.setPromptText("Longitud Mínima");
        TextField txtLonMax = new TextField();
        txtLonMax.setPromptText("Longitud Máxima");

        Button btnSeleccionarImagen = new Button("Seleccionar Imagen JPG");
        final File[] archivoSeleccionado = new File[1];

        btnSeleccionarImagen.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg"));
            archivoSeleccionado[0] = fc.showOpenDialog(null);
            if (archivoSeleccionado[0] != null) {
                btnSeleccionarImagen.setText(archivoSeleccionado[0].getName());
            }
        });

        // 3. Organizamos los campos en la ventana
        VBox content = new VBox(10, 
            new Label("Nombre:"), txtNombre,
            btnSeleccionarImagen,
            new Label("Coordenadas (Bounding Box):"),
            txtLatMin, txtLatMax, txtLonMin, txtLonMax
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // 4. Lógica al pulsar OK
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK && archivoSeleccionado[0] != null) {
                try {
                    // Llamamos a la librería para registrar el mapa 
                    return SportActivityApp.getInstance().addMapRegion(
                        txtNombre.getText(),
                        archivoSeleccionado[0],
                        Double.parseDouble(txtLatMin.getText()),
                        Double.parseDouble(txtLatMax.getText()),
                        Double.parseDouble(txtLonMin.getText()),
                        Double.parseDouble(txtLonMax.getText())
                    );
                } catch (NumberFormatException e) {
                    mostrarError("Las coordenadas deben ser números decimales.");
                }
            }
            return null;
        });

        Optional<MapRegion> result = dialog.showAndWait();
        result.ifPresent(region -> {
            System.out.println("Mapa añadido correctamente: " + region.getName());
        });
    }
    private void mostrarError(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
