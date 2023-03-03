package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.Fantasia;

public class AluguelController implements Initializable {

    @FXML
    private DatePicker dateFinal;

    @FXML
    private DatePicker dateInicio;

    @FXML
    private ImageView imageImagem;

    @FXML
    private Label labelPrecoDia;

    @FXML
    private Label labelUsuario;

    @FXML
    private Label labelValorTotal;

    private Fantasia fantasia;
    
    @FXML
    public void alugar(ActionEvent event) {

    }

    @FXML
    public void voltar(ActionEvent event) {

    }    
    
    public void setData(Fantasia fantasia){
        this.fantasia = fantasia;
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
