
package EventManager;
/**
 *
 * @author DWIGHT
 */
public class Event {
    private int id;
    private String event_name;
    private String time;
    private String date;
    private String place;
    private String description;
    
    public Event(int id,String event_name, String time, String date, String place, String description) {
        this.id = id;
        this.event_name = event_name;
        this.time = time;
        this.date = date;
        this.place = place;
        this.description = description;
    }
    
    public int getId(){
        return id;
    }
    public String event_name(){
        return event_name;
    }
    public String time(){
        return time;
    }
    public String date() {
        return date;
    }
    public String place() {
        return place;
    }
    public String description() {
        return description;
    }
}
