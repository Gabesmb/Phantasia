package controllers;

import database.MySQLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.RouteManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.Admin;

public class BuscaAdminController implements Initializable {

    @FXML
    private Button buttonPesquisa;

    @FXML
    private Label labelUsuario;

    @FXML
    private TextField textPesquisa;    
    
    @FXML
    private VBox vboxLista;     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        labelUsuario.setText(RouteManager.get().getLoggedUser().getUsername());
        MySQLConnection database = RouteManager.get().getDatabase();        
        
        vboxLista.getChildren().clear();
        FXMLLoader paneLoader = new FXMLLoader();
        paneLoader.setLocation(getClass().getResource("/views/ListaFantasias.fxml"));
        
        
        ScrollPane lista;
        
        try (ResultSet results = database.query("SELECT * FROM Fantasia")) {
            lista = paneLoader.load();
            ListaFantasiasController listaController = paneLoader.getController();
            vboxLista.getChildren().add(lista);
            if (results != null) {
                listaController.setFantasias(results, RouteManager.get().getLoggedUser() instanceof Admin);
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(BuscaAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
    public void loadFantasias(){
        MySQLConnection database = RouteManager.get().getDatabase();
        labelUsuario.setText(RouteManager.get().getLoggedUser().getUsername());
        
        /*ResultSet results = database.
                query("SELECT * FROM Fantasia WHERE nome_fantasia LIKE '%" 
                        + textPesquisa.getText() + "%'");*/
        
        vboxLista.getChildren().clear();
        FXMLLoader paneLoader = new FXMLLoader();
        paneLoader.setLocation(getClass().getResource("/views/ListaFantasias.fxml"));
                
        ScrollPane lista;
        
        try (ResultSet results = database.query("SELECT * FROM Fantasia WHERE nome_fantasia LIKE '%" 
                        + textPesquisa.getText() + "%'")) {
            lista = paneLoader.load();
            ListaFantasiasController listaController = paneLoader.getController();
            vboxLista.getChildren().add(lista);
            if (results != null) {
                listaController.setFantasias(results, RouteManager.get().getLoggedUser() instanceof Admin);
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(BuscaAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }   
    
    @FXML
    public void logout(ActionEvent event) {
        RouteManager.get().logout();
        RouteManager.get().setScene("Login");
    }    
    
    @FXML
    public void adicionar(ActionEvent event) {
        RouteManager.get().setScene("Adicionar");
    }    
}
