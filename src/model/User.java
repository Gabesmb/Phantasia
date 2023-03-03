package model;

public abstract class User {
    protected String username;
    
    public User(String username){
        this.username = username;
    }
    
    public String getUsername(){
        return this.username;
    }
}
