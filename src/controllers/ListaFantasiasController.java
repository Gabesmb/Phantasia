package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import main.RouteManager;
import model.Fantasia;

public class ListaFantasiasController implements Initializable {

    @FXML
    private ScrollPane paneLista;
    
    @FXML
    private VBox vboxLista;    

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    
    
    public void setFantasias(ResultSet set){
        try {
            do {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/ItemFantasia.fxml"));
                
                HBox box = loader.load();
                
                ItemFantasiaController controller = loader.getController();
                //System.out.println(controller == null);
                
                
                controller.setData(fillFantasia(set));
                vboxLista.getChildren().add(box);
                        
            } while (set.next());
        } catch (SQLException | IOException ex) {
            Logger.getLogger(ListaFantasiasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Fantasia fillFantasia(ResultSet set){
        Fantasia fantasia = null;
        try {
            String nome = set.getString("nome_fantasia");
            String imageSource = "/img/" + nome + ".png";
            boolean disponivel = RouteManager.get().getDatabase()
                    .getQuantidadeDisponivel(nome) > 0;
            fantasia = new Fantasia(nome, imageSource, disponivel);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fantasia;
    }
    
}
