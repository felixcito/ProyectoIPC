/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmlapplication;



import java.net.URL;

import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.converter.LocalDateStringConverter;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.YEARS;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;


public class FXMLRegisterController implements Initializable {

    @FXML
    private TextField emailField;
    private BooleanProperty validEmail;
    private ChangeListener<String> listenerEmail;
    @FXML
    private Label emailError;
    @FXML
    private PasswordField passwordField;
    private BooleanProperty validPassword;
    private ChangeListener<String> listenerPassword;
    @FXML
    private Label passwordError;
    @FXML
    private PasswordField password2Field;
    private BooleanProperty confirmPasswords;
    private ChangeListener<String> listenerConfirmPassword;
    @FXML
    private Label passwordConfirmError;
    @FXML
    private DatePicker dateField;
    private BooleanProperty validDate;
    private ChangeListener<String>  listenerDate;
    @FXML
    private Label dateError;
    @FXML
    private Button bAccept;
    @FXML
    private Button bCancel;
    
    private void showError(boolean isValid, Node field, Node errorMessage){
        errorMessage.setVisible(!isValid);
        field.setStyle(((isValid) ? "" : "-fx-background-color: #FCE5E0"));
    }

    //=========================================================
    // you must initialize here all related with the object 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validEmail = new SimpleBooleanProperty();
        validEmail.setValue(Boolean.FALSE);
        validPassword = new SimpleBooleanProperty();
        validPassword.setValue(Boolean.FALSE);
        confirmPasswords = new SimpleBooleanProperty();
        confirmPasswords.setValue(Boolean.FALSE);
        validDate = new SimpleBooleanProperty();
        validDate.setValue(Boolean.FALSE);
        
        BooleanBinding validFields = Bindings.and(validEmail, validPassword)
                 .and(confirmPasswords)
                  .and(validDate);
        bAccept.disableProperty().bind(Bindings.not(validFields));
        bCancel.setOnAction( (event)->{
            bCancel.getScene().getWindow().hide();
        });
        
        LocalDateStringConverter localDateStringConverter = new LocalDateStringConverter() {
            @Override
            public LocalDate fromString(String value) {
                try {
                    return super.fromString(value);
                } catch (Exception e) {
                    System.out.println("Exception in fromString");
                    return LocalDate.now();
                }
            }
            @Override
            public String toString(LocalDate value) {
                return super.toString(value);
                }
        };
        dateField.setConverter(localDateStringConverter);
        
        emailField.focusedProperty().addListener((observable, oldValue, newValue) ->{
            if(!newValue){
                checkEmail();
                if(!validEmail.get()){
                    if(listenerEmail == null){
                        listenerEmail = (a, b, c) -> checkEmail();
                        emailField.textProperty().addListener(listenerEmail);
                    }
                }
            }
        });
        
        passwordField.focusedProperty().addListener((observable, oldValue,newValue) ->{
            if(!newValue){
                checkPassword();
                if(!validPassword.get()){
                    if(listenerPassword == null){
                        listenerPassword = (a, b, c) -> checkPassword();
                        passwordField.textProperty().addListener(listenerPassword);
                    }
                }
            }
        });
        
        password2Field.focusedProperty().addListener((observable, oldValue, newValue) ->{
            if(!newValue){
                checkPasswordsMatch();
                if(!confirmPasswords.get()){
                    if(listenerConfirmPassword == null){
                        listenerConfirmPassword = (a, b, c) -> checkPasswordsMatch();
                        password2Field.textProperty().addListener(listenerConfirmPassword);
                    }
                }
            }
        });
        dateField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Cuando el usuario sale del campo (pierde el foco)
                checkDate();
                if (!validDate.get()) {
                    if (listenerDate == null) {
                        listenerDate = (a, b, c) -> checkDate();
                        dateField.getEditor().textProperty().addListener(listenerDate);
                    }
                }
            }
        });
    }
    private void checkEmail(){
        String email = emailField.getText();
        boolean isValid = email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        validEmail.set(isValid);
        showError(isValid, emailField,emailError);
    }
    
    private void checkPassword(){
        String password = passwordField.getText();
        boolean isValid = password.matches("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,15}$");
        validPassword.set(isValid);
        showError(isValid, passwordField, passwordError);    
    }

    private void checkPasswordsMatch(){
        boolean match = passwordField.getText().equals(password2Field.getText());
        confirmPasswords.set(match);
        showError(match, password2Field, passwordConfirmError);
    }
    
    private void checkDate(){
        LocalDate value = dateField.getValue();
        boolean isValid = value.isBefore(LocalDate.now().minus(16, YEARS));
        validDate.set(isValid);
        showError(isValid, dateField, dateError);
    }

    @FXML
    private void handleBAcceptOnAction(ActionEvent event) {
        emailField.clear();
        passwordField.clear();
        password2Field.clear();
        dateField.setValue(null);
        
        validEmail.setValue(Boolean.FALSE);
        validPassword.setValue(Boolean.FALSE);
        confirmPasswords.setValue(Boolean.FALSE);
        validDate.setValue(Boolean.FALSE);
    }

}