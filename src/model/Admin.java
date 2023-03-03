package model;

public class Admin extends User{
    
    protected boolean allow_insert;
    protected boolean allow_update;
    protected boolean allow_delete;
    
    public Admin(String username, boolean allow_insert, boolean allow_update,
            boolean allow_delete) {
        super(username);
        this.allow_insert = allow_insert;
        this.allow_update = allow_update;
        this.allow_delete = allow_delete;
    }
    
    public boolean getInsertPermission(){
        return allow_insert;
    }
    
    public boolean getUpdatePermission(){
        return allow_update;
    }
    
    public boolean getDeletePermission(){
        return allow_delete;
    }
    
}
