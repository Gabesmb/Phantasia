package controllers;

import database.MySQLConnection;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import main.RouteManager;
import model.Fantasia;

public class ItemFantasiaAdminController implements Initializable {

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
        InputStream image = getClass().getResourceAsStream(
                        fantasia.getImageSource());
        if (image != null) {
            imgFantasia.setImage(new Image(image));
        }
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

    @FXML
    public void editar(ActionEvent event) {

    }    
    
    @FXML
    public void excluir(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar operação");
        alert.setHeaderText("Tem certeza que deseja excluir a fantasia?");
        alert.setContentText("");
        if(alert.showAndWait().get() == ButtonType.OK){
            try {
                MySQLConnection db = RouteManager.get().getDatabase();
                int idAdmin = db.query("select id_admin from DBAdmin "
                        + "where username_admin = '"
                        + RouteManager.get().getLoggedUser().getUsername() + "'")
                        .getInt("id_admin");
                db.delete("Fantasia", idAdmin, "nome_fantasia = '" + fantasia.getNome() + "'");
            } catch (SQLException ex) {
                Logger.getLogger(ItemFantasiaAdminController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setTitle("Operação realizada com sucesso!");
        alert1.setHeaderText("Os dados foram removidos do sistema.");
        alert1.setContentText("");
        alert1.show();
        RouteManager.get().setScene("BuscaAdmin");
    }
}
