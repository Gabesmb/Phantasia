package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.RouteManager;
import database.MySQLConnection;

public class CadastroController{    

    @FXML
    TextField textUsername;
    
    @FXML
    PasswordField passFieldSenha;
    
    @FXML
    TextField textEmail;
    
    @FXML
    TextField textNome;    
    
    public void onLinkLoginAction(ActionEvent e){
        RouteManager.get().setScene("Login");
    }
    
    public void onButtonCadastroAction(ActionEvent e){
        String username = textUsername.getText();
        String senha = passFieldSenha.getText();
        String email = textEmail.getText();
        String nome = textNome.getText();
        
        MySQLConnection database = RouteManager.get().getDatabase();
        
        ResultSet usernameCount = database.query("select count(*) from Cliente where username_cliente = " + "'" + username + "'");
        ResultSet password;
        
        int count = 0;
        try {
            count = usernameCount.getInt("count(*)");
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        if(count > 0 || username.isBlank() || senha.isBlank()){
            showCadastroFailAlert();
        } else{
            if(email.isBlank()){
                email = "null";
            }
            if(nome.isBlank()){
                nome = "null";
            }
            //email = (email.isBlank())?"null":email;
            
            
            
            database.insert("Cliente", "username_cliente, senha_cliente, e_mail, nome_cliente", 
                    21, "'" + username + "', '" + senha + "', '" + email + "', '" + nome + "'");
            
            RouteManager.get().setScene("Login");
            
            showCadastroSuccessAlert();
            
        }
        
    }
    
    private void showCadastroFailAlert(){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Falha no cadastro");
            alert.setHeaderText("Houve uma falha no cadastro");
            alert.setContentText("Já existe um usuário com esse username ou as credenciais inseridas são inválidas!");
            alert.showAndWait();        
    }
    
    private void showCadastroSuccessAlert(){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Cadastro realizado com sucesso!");
            alert.setContentText("");
            alert.showAndWait();                
    }
}
