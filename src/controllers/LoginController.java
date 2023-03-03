package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.RouteManager;
import database.MySQLConnection;

public class LoginController{
    
    @FXML
    TextField textUsername;
    
    @FXML
    PasswordField passFieldSenha;
        
    public void onLinkCadastroAction(ActionEvent e){
        RouteManager.get().setScene("Cadastro");
    }
    
    public void onButtonLoginAction(ActionEvent e){
        String username = textUsername.getText();
        String senha = passFieldSenha.getText();
        
        //System.out.println("select count(*) from DBAdmin where username_admin = " + "'" + username + "'");
        
        if(username.isBlank()){
            showLoginFailAlert();
            return;
        } 
        
        MySQLConnection database = RouteManager.get().getDatabase();
        
        String loginSource;
        ResultSet adminCountSet = database.query("select count(*) from DBAdmin where username_admin = " + "'" + username + "'");
        ResultSet clienteCountSet = database.query("select count(*) from Cliente where username_cliente = " + "'" + username + "'");
        ResultSet password;
        
        
        
        int adminCount = 0, clienteCount = 0;
        try {
            adminCount = adminCountSet.getInt("count(*)");
            clienteCount = clienteCountSet.getInt("count(*)");
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(adminCount);
        System.out.println(clienteCount);
        
        if(adminCount > 0){
            loginSource = "DBAdmin";
        } else if (clienteCount > 0){
            loginSource = "Cliente";
        } else {
            loginSource = "null";
        }
        
        if(loginSource.equals("Cliente")){
            password = database.query("select senha_cliente from Cliente where username_cliente = " + "'" + username + "'");
            String passwordString = "";
            try {
                passwordString = password.getString("senha_cliente");
            } catch (SQLException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println(passwordString);
            
            if(senha.equals(passwordString)){
                //TODO (AugustNinelie): trocar para a tela de busca de fantasia.
                RouteManager.get().logClient(username);
                RouteManager.get().setScene("Busca");
            } else {
                showLoginFailAlert();
            }
        } else if (loginSource.equals("DBAdmin")){
            password = database.query("select senha_admin from DBAdmin where username_admin = " + "'" + username + "'");
            String passwordString = "";
            try {
                passwordString = password.getString("senha_admin");
            } catch (SQLException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println(passwordString);
            
            if(senha.equals(passwordString)){
                //TODO (AugustNinelie): trocar para a tela de busca de fantasia.
                RouteManager.get().logAdmin(username);
                RouteManager.get().setScene("Busca");
            } else {
                showLoginFailAlert();
            }
            
        } else {
            showLoginFailAlert();
        }
        
    }
    
    private void showLoginFailAlert(){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Falha no login");
            alert.setHeaderText("Houve uma falha na autenticação.");
            alert.setContentText("As informações inseridas não correspondem com nenhuma entrada.");
            alert.showAndWait();
    }
    
}
