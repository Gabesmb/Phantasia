/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import database.MySQLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import main.RouteManager;

/**
 * FXML Controller class
 *
 * @author evert
 */
public class AdicionarController implements Initializable {

    @FXML
    private Label labelUsuario;

    @FXML
    private RadioMenuItem menuItemG;

    @FXML
    private RadioMenuItem menuItemGG;

    @FXML
    private RadioMenuItem menuItemM;

    @FXML
    private RadioMenuItem menuItemP;

    @FXML
    private RadioMenuItem menuItemPP;

    @FXML
    private RadioMenuItem menuItemXG;

    @FXML
    private TextField textAluguel;
    
    @FXML
    private TextField textNome;

    @FXML
    private TextField textQuantidade;

    @FXML
    public void logout(ActionEvent event) {
        RouteManager.get().logout();
        RouteManager.get().setScene("Login");
    }
    
    @FXML
    public void adicionar(ActionEvent event) {        
        String nome = textNome.getText();
        int quantidade = Integer.parseInt(textQuantidade.getText());
        String tamanho;
        if(menuItemPP.isSelected()){
            tamanho = menuItemPP.getText();
        } else if(menuItemP.isSelected()){
            tamanho = menuItemP.getText();
        } else if(menuItemM.isSelected()){
            tamanho = menuItemM.getText();
        } else if(menuItemG.isSelected()){
            tamanho = menuItemG.getText();
        } else if(menuItemGG.isSelected()){
            tamanho = menuItemGG.getText();
        } else {
            tamanho = menuItemXG.getText();
        }
        float valor = Float.parseFloat(textAluguel.getText());
        
/*        System.out.println(nome);
        System.out.println(quantidade);
        System.out.println(tamanho);
        System.out.println(valor);*/

        if(tamanho.isBlank() || textQuantidade.getText().isBlank() 
                || textAluguel.getText().isBlank() || quantidade <= 0 
                || valor <= 0){
            showInvalidDataAlert();
        } else{
            try {
                MySQLConnection db = RouteManager.get().getDatabase();
                int idAdmin = db.query("select id_admin from DBAdmin "
                        + "where username_admin = '"
                        + RouteManager.get().getLoggedUser().getUsername() + "'")
                        .getInt("id_admin");
                db.insert("Fantasia", "nome_fantasia, quantidade, tamanho, valor_aluguel_dia, id_admin",
                        idAdmin, "'" + nome + "', " + quantidade + ", "
                                + "'" + tamanho + "', " + valor + ", " + idAdmin);
                showSuccessAlert();
            } catch (SQLException ex) {
                Logger.getLogger(AdicionarController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void showInvalidDataAlert() {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dados inválidos");
            alert.setHeaderText("Insira dados válidos no formulário.");
            alert.setContentText("");
            alert.showAndWait();        
    }
    
    private void showSuccessAlert(){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso!");
            alert.setHeaderText("Dados inseridos no sistema.");
            alert.setContentText("");
            alert.showAndWait();                
    }
    
    @FXML
    public void voltar(){
        RouteManager.get().setScene("BuscaAdmin");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        labelUsuario.setText(RouteManager.get().getLoggedUser().getUsername());
        textQuantidade.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        textQuantidade.textProperty().addListener((obs,oldv,newv) -> {
            try {
                textQuantidade.getTextFormatter().getValueConverter().fromString(newv);
                // no exception above means valid
                textQuantidade.setBorder(null);
            } catch (NumberFormatException e) {
                textQuantidade.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            }
        });
        textAluguel.setTextFormatter(new TextFormatter<>(new FloatStringConverter()));
        textAluguel.textProperty().addListener((obs,oldv,newv) -> {
            try {
                textAluguel.getTextFormatter().getValueConverter().fromString(newv);
                // no exception above means valid
                textAluguel.setBorder(null);
            } catch (NumberFormatException e) {
                textAluguel.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            }
        });        
        
    }    
    
}
