package phantasia.fxml.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import phantasia.RouteManager;

public class CadastroController{    

    @FXML
    TextField textUsername;
    
    @FXML
    TextField textSenha;
    
    @FXML
    TextField textEmail;
    
    @FXML
    TextField textNome;    
    
    public void onLinkLoginAction(ActionEvent e){
        RouteManager.get().setScene("Login");
    }
    
    public void onButtonCadastroAction(ActionEvent e){
        System.out.println("Aqui!!!!!");        
    }
    
}
