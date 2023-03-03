package controllers;

import database.MySQLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.RouteManager;
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

    @FXML
    private Label labelNome;    
    
    private Fantasia fantasia;
    
    @FXML
    public void alugar(ActionEvent event) {
        if(dateInicio == null || dateFinal == null){
            showMissingDatesAlert();
        } else{
            try {
                LocalDate dataInicio = dateInicio.getValue();
                LocalDate dataFinal = dateFinal.getValue();
                int dateDifference = RouteManager.get().getDatabase()
                        .query("select datediff('" + dataFinal.toString() + "', '"
                                + dataInicio.toString() + "')").getInt(1) + 1;
                if(dateDifference < 1){
                    showInvalidDatesAlert();
                } else {
                    ButtonType confirmation = showConfirmationAlert();
                    if(confirmation == ButtonType.OK){
                        MySQLConnection db = RouteManager.get().getDatabase();
                        String nomeCliente = RouteManager.get().getLoggedUser().getUsername();                        
                        int idFantasia = db.query("select id_fantasia from Fantasia "
                                + "where nome_fantasia = '" + fantasia.getNome() + "'")
                                .getInt("id_fantasia");
                        int idCliente = db.query("select id_cliente from Cliente "
                                + "where username_cliente = '" + nomeCliente + "'")
                                .getInt("id_cliente");
                        db.update("insert into Aluguel "
                                        + "(id_fantasia, id_cliente, data_inicio, data_fim, status_aluguel) "
                                        + "values (" + idFantasia + ", " + idCliente 
                                        + ", '" + dataInicio.toString() + "'"
                                        + ", '" + dataFinal.toString() + "', 'ativo')");
                        showSuccessAlert();
                        RouteManager.get().setScene("Busca");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(AluguelController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        }
    }
    
    private void showMissingDatesAlert(){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Insira as datas!");
            alert.setHeaderText("Não é possível realizar um aluguel sem a inserção de datas válidas.");
            alert.setContentText("");
            alert.showAndWait();                    
    }
    
    private void showInvalidDatesAlert(){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datas inseridas inválidas!");
            alert.setHeaderText("Certifique-se que a data de início não é anterior à data atual, e que a data final não seja anterior à data inicial.");
            alert.setContentText("");
            alert.showAndWait();                    
    }

    private ButtonType showConfirmationAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirme as informações");
        alert.setHeaderText("Deseja confirmar a operação de aluguel?");
        alert.setContentText("");
        return alert.showAndWait().get();
    }
    
    private void showSuccessAlert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operação realizada com sucesso!");
        alert.setHeaderText("O seu aluguel foi confirmado e já está armazenado no sistema.");
        alert.setContentText("");    
        alert.showAndWait();
    }
    
    @FXML
    public void voltar(ActionEvent event) {
        RouteManager.get().prepareScene("DescricaoFantasia");
        DescricaoFantasiaController controller = 
                RouteManager.get().getPreparedLoader().getController();
        controller.setValues(fantasia);
        RouteManager.get().setPreparedScene();        
    }    
    
    @FXML
    public void logout(ActionEvent event) {
        RouteManager.get().logout();
        RouteManager.get().setScene("Login");
    }
    
    public void setData(Fantasia fantasia){
        try {
            this.fantasia = fantasia;
            imageImagem.setImage(
                    new Image(getClass().getResourceAsStream(
                            fantasia.getImageSource())));
            labelUsuario.setText(RouteManager.get().getLoggedUser().getUsername());
            labelNome.setText(fantasia.getNome());
            float precoDia = RouteManager.get().getDatabase()
                    .query("select valor_aluguel_dia from Fantasia "
                            + "where nome_fantasia = '" + fantasia.getNome() + "'")
                    .getFloat("valor_aluguel_dia");
            labelPrecoDia.setText("" + precoDia);
        } catch (SQLException ex) {
            Logger.getLogger(AluguelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void setValorTotal(ActionEvent e){
        LocalDate dataInicio = dateInicio.getValue();
        LocalDate dataFinal = dateFinal.getValue();
        
        
        if(dataInicio!= null && dataFinal != null && 
                !(dataInicio.isBefore(LocalDate.now()) ||
                dataFinal.isBefore(dataInicio))){
            try {
                float valor = RouteManager.get().getDatabase()
                    .query("select valor_aluguel_dia from Fantasia "
                            + "where nome_fantasia = '" + fantasia.getNome() + "'")
                        .getFloat("valor_aluguel_dia");
                int dateDifference = RouteManager.get().getDatabase()
                        .query("select datediff('" + dataFinal.toString() + "', '" 
                                + dataInicio.toString() + "')").getInt(1) + 1;
                labelValorTotal.setText("R$ " + valor * dateDifference);
            } catch (SQLException ex) {
                Logger.getLogger(AluguelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            labelValorTotal.setText("");
        }
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
