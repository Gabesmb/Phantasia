package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import database.MySQLConnection;
import model.Admin;
import model.Cliente;
import model.User;

public class RouteManager{
    
    private static RouteManager routeManager = null;
    
    private User loggedUser;
    
    private MySQLConnection database;
    private Stage stage;
    
    private Scene preparedScene = null;
    private FXMLLoader preparedLoader = null;
    
    public RouteManager(MySQLConnection database, Stage stage){
        this.database = database;
        this.stage = stage;
    }
    
    public MySQLConnection getDatabase(){
        return this.database;
    }
    
    public void prepareScene(String scene){
        try {
            preparedLoader = new FXMLLoader();
            preparedLoader.setLocation(getClass().getResource("/views/" + scene + ".fxml"));
            preparedScene = new Scene(preparedLoader.load());
        } catch (IOException ex) {
            Logger.getLogger(RouteManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public FXMLLoader getPreparedLoader(){
        return preparedLoader;
    }
    
    public Scene getPreparedScene(){
        return preparedScene;
    }
    
    public void setPreparedScene(){
        if(preparedScene != null){
            stage.setScene(preparedScene);
            preparedScene = null;
            preparedLoader = null;
        }
    }
    
    public void setScene(String scene){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/"
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
        loggedUser = new Cliente(user);
    }
    
    public void logAdmin(String user){
        boolean allowInsert, allowUpdate, allowDelete;
        try {
            
            allowInsert = database.query("SELECT allow_insert FROM DBAdmin WHERE username_admin = '" + user + "'").
                    getBoolean("allow_insert");
            allowUpdate = database.query("SELECT allow_update FROM DBAdmin WHERE username_admin = '" + user + "'").
                    getBoolean("allow_update");
            allowDelete = database.query("SELECT allow_delete FROM DBAdmin WHERE username_admin = '" + user + "'").
                    getBoolean("allow_delete");  
            loggedUser = new Admin(user, allowInsert, allowUpdate, allowDelete);
        } catch (SQLException ex) {
            Logger.getLogger(RouteManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logout(){
        loggedUser = null;
    }
    
    public User getLoggedUser(){
        return loggedUser;
    }
}
