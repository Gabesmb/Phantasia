package phantasia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import phantasia.database.MySQLConnection;
/**
 *
 * @author gabri
 */
public class Main extends Application {
    
    private static MySQLConnection database;
    
    public static void main(String[] args) {
        String username = "root";
        String password = "Phantasia2.0";
        String db = "Phantasia";
        database = new MySQLConnection(username, password, db);             
        
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/phantasia/fxml/Login.fxml"));
        Scene sc = new Scene(root);
        stage.setScene(sc);
        
        RouteManager.init(database, stage);
        
        stage.show();
    }
    
}
