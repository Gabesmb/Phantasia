package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.RouteManager;
import model.Fantasia;

public class DescricaoFantasiaController implements Initializable {

    @FXML
    private ImageView imageImagem;

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelUsuario;

    @FXML
    private Label labelNome;    
    
    @FXML
    private VBox vboxCategorias;    
    
    private Fantasia fantasia;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    public void voltar(){
        RouteManager.get().setScene("Busca");
    }
    
    @FXML
    public void alugar(){
        if(fantasia.isDisponivel()){
            RouteManager.get().prepareScene("Aluguel");
            AluguelController controller = RouteManager.get().getPreparedLoader().getController();
            controller.setData(fantasia);
            RouteManager.get().setPreparedScene();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Fantasia indisponível!");
            alert.setHeaderText("Não há fantasias desse modelo para aluguel.");
            alert.setContentText("");
            alert.showAndWait();            
        }
    }
    
    public void setValues(Fantasia fantasia){
        this.fantasia = fantasia;
        imageImagem.setImage(
                new Image(getClass().getResourceAsStream(
                        fantasia.getImageSource())));
        labelUsuario.setText(RouteManager.get().getLoggedUser().getUsername());
        setDisponivel(fantasia.isDisponivel());
        labelNome.setText(fantasia.getNome());
    }
    
    private void setDisponivel(boolean disponivel){
        if(disponivel){
            labelStatus.setText("Disponivel");
            labelStatus.setTextFill(Color.web("0x22FF22"));
        } else {
            labelStatus.setText("Alugada");
            labelStatus.setTextFill(Color.web("0xFF2222"));            
        }
    }
    
    private void setCategorias(String nomeFantasia){
        List<String> categorias = new ArrayList<>();
        var results = RouteManager.get().getDatabase().query(
                "SELECT Categoria.nome_categoria "
                        + "FROM Categoria, Fantasia, Fantasia_Categoria "
                        + "WHERE Categoria.id_categoria = Fantasia_Categoria.id_categoria "
                        + "AND Fantasia_Categoria.id_fantasia = Fantasia.id_fantasia "
                        + "AND Fantasia.nome_fantasia = '" + nomeFantasia + "';");
        try {
            if(results != null){            
                do{
                    Label labelCategoria = new Label(results.getString("nome_categoria"));
                    vboxCategorias.getChildren().add(labelCategoria); 
                } while(results.next());
            }
        } catch (SQLException ex) {
            Logger.getLogger(DescricaoFantasiaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void logout(ActionEvent event) {
        RouteManager.get().logout();
        RouteManager.get().setScene("Login");
    }
    
}
