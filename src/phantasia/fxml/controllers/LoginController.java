package phantasia.fxml.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import phantasia.RouteManager;
import phantasia.database.MySQLConnection;

public class LoginController{
    
    @FXML
    TextField textUsername;
    
    @FXML
    TextField textSenha;
        
    public void onLinkCadastroAction(ActionEvent e){
        RouteManager.get().setScene("Cadastro");
    }
    
    public void onButtonLoginAction(ActionEvent e){
        String username = textUsername.getText();
        String senha = textSenha.getText();
        
        //System.out.println("select count(*) from DBAdmin where username_admin = " + "'" + username + "'");
        
        MySQLConnection database = RouteManager.get().getDatabase();
        
        ResultSet usernameCount = database.query("select count(*) from DBAdmin where username_admin = " + "'" + username + "'");
        ResultSet password;
        
        int count = 0;
        try {
            count = usernameCount.getInt("count(*)");
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(count);
        
        if(count != 0){
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
                
            }
        } else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Falha no login");
            alert.setHeaderText("Houve uma falha na autenticação.");
            alert.setContentText("As informações inseridas não correspondem com nenhuma entrada.");
            alert.showAndWait();
            
        }
        
    }
    
}
