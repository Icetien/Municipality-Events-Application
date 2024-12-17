/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EventManager;

/**
 *
 * @author einst
 */
public class Notifications {
    private String cityName;
    private String eventName;
    
    public Notifications(String cityName, String eventName) {
        this.cityName = cityName;
        this.eventName = eventName;
    }
    
    public String cityName(){
        return cityName;
    }
    public String eventName(){
        return eventName;
    }
    
    @Override
    public String toString() {
        return "Event: " + eventName + ", City: " + cityName;
    }
}
