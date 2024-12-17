/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EventManager;



public class User {
    private String username;
    private int id;
    private String nickname;
    
    public User(int id, String username,String nickname) {

        this.username = username;
        this.id = id;
        this.nickname = nickname;
    }
    

    
    
    public String getUsername() {
        return username;
    }
    public int getId() {
        return id;
    }
    public String getNickname(){
        return nickname;
    }
}
