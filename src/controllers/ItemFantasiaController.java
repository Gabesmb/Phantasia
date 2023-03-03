package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import main.RouteManager;
import model.Fantasia;

public class ItemFantasiaController implements Initializable {

    @FXML
    private ImageView imgFantasia;

    @FXML
    private Label labelDisponivel;

    @FXML
    private Label labelNome;
    
    private Fantasia fantasia;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setData(Fantasia fantasia){
        this.fantasia = fantasia;
        //System.out.println(fantasia.getImageSource());
        imgFantasia.setImage(
                new Image(getClass().getResourceAsStream(
                        fantasia.getImageSource())));
        labelNome.setText(fantasia.getNome());
        setDisponivel(fantasia.isDisponivel());
    }
    
    private void setDisponivel(boolean disponivel){
        if(disponivel){
            labelDisponivel.setText("Disponivel");
            labelDisponivel.setTextFill(Color.web("0x22FF22"));
        } else {
            labelDisponivel.setText("Alugada");
            labelDisponivel.setTextFill(Color.web("0xFF2222"));            
        }
    }
    
    @FXML
    public void visualizar(ActionEvent event) {
        RouteManager.get().prepareScene("DescricaoFantasia");
        DescricaoFantasiaController controller = 
                RouteManager.get().getPreparedLoader().getController();
        controller.setValues(fantasia);
        RouteManager.get().setPreparedScene();
    }    
}
