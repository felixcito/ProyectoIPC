/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmlapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import static javafxmlapplication.Utils.*;

/**
 *
 * @author jsoler
 */
public class FXMLDocumentController implements Initializable {
    private Label labelMessage;
    @FXML
    private Circle circle1;
    @FXML
    private GridPane grid;
    double X_ini;
    double Y_ini;
    
    //=========================================================
    // event handler, fired when button is clicked or 
    //                      when the button has the focus and enter is pressed
    private void handleButtonAction(ActionEvent event) {
        labelMessage.setText("Hello, this is your first JavaFX project - IPC");
    }
    
    //=========================================================
    // you must initialize here all related with the object 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // TODO
    }    


    @FXML
    private void cicrcleClick(MouseEvent event) {
        double x = event.getSceneX();
        double y = event.getSceneY();
        
        GridPane.setColumnIndex(circle1, columnCalc(grid,x));
        GridPane.setRowIndex(circle1, rowCalc(grid, y));
    }

    @FXML
    private void circcleMove(MouseEvent event) {
        X_ini = event.getSceneX();
        Y_ini = event.getSceneY();
      
    }

    @FXML
    private void moveFlechas(KeyEvent event) {
        int row = grid.getRowIndex(circle1);
        int column = grid.getColumnIndex(circle1);
        if(event.getCode() == KeyCode.UP){
            
            GridPane.setRowIndex(circle1, rowNorm(grid, row-1));
        }
        else if(event.getCode() == KeyCode.DOWN){
            GridPane.setRowIndex(circle1, rowNorm(grid, row+1));
   
        }
        else if(event.getCode() == KeyCode.LEFT){
            GridPane.setColumnIndex(circle1, columnNorm(grid, column-1));
        }
        else if(event.getCode() == KeyCode.RIGHT){
            GridPane.setColumnIndex(circle1, columnNorm(grid, column+1));
        }
    }

    @FXML
    private void circleReleased(MouseEvent event) {
        circle1.setTranslateX(0);
        circle1.setTranslateY(0);
        //int newCol = columnCalc(grid, event.getSceneX());
        //int newRow = rowCalc(grid, event.getSceneY());
        GridPane.setColumnIndex(circle1, columnCalc(grid,event.getSceneX()));
        GridPane.setRowIndex(circle1, rowCalc(grid, event.getSceneY()));
        
        event.consume();
   
    }

    @FXML
    private void circleDragg(MouseEvent event) {
        circle1.setTranslateX(event.getSceneX() - X_ini);
        circle1.setTranslateY(event.getSceneY() - Y_ini);
    }
}
