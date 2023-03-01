package phantasia;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import phantasia.database.MySQLConnection;

public class RouteManager{
    
    private static RouteManager routeManager = null;
    
    private String loggedUser = null;
    private boolean allowInsert = false;
    private boolean allowUpdate = false;
    private boolean allowDelete = false;
    
    private MySQLConnection database;
    private Stage stage;
    
    public RouteManager(MySQLConnection database, Stage stage){
        this.database = database;
        this.stage = stage;
    }
    
    public MySQLConnection getDatabase(){
        return this.database;
    }
    
    public void setScene(String scene){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/phantasia/fxml/"
                    + scene + ".fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            Logger.getLogger(RouteManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void init(MySQLConnection database, Stage stage){
        if(routeManager != null){
            return;
        }
        routeManager = new RouteManager(database, stage);
    }
    
    public static RouteManager get(){
        return routeManager;
    }
    
    public void logClient(String user){
        loggedUser = user;
        allowInsert = false;
        allowUpdate = false;
        allowDelete = false;
    }
    
    public void logAdmin(String user){
        loggedUser = user;
        try {
            
            allowInsert = database.query("SELECT allow_insert FROM DBAdmin WHERE username_admin = '" + user + "'").
                    getBoolean("allow_insert");
            allowUpdate = database.query("SELECT allow_update FROM DBAdmin WHERE username_admin = '" + user + "'").
                    getBoolean("allow_update");
            allowDelete = database.query("SELECT allow_delete FROM DBAdmin WHERE username_admin = '" + user + "'").
                    getBoolean("allow_delete");            
        } catch (SQLException ex) {
            Logger.getLogger(RouteManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logout(){
        loggedUser = null;
    }
}
