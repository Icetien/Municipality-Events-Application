package EventManager;

import java.awt.Component;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.time.LocalDate;


public class AdminFunction {  
   private static final String URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/eventdb.db";
   
   
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    //Initialize the database if tables are missing
    public static void initDatabase(){
        String createCities = """
                CREATE TABLE IF NOT EXISTS cities (
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              name TEXT NOT NULL UNIQUE
                              );
                              """;
        
        String createEvents = """
                CREATE TABLE IF NOT EXISTS events (
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              city_id INTEGER NOT NULL,
                              name TEXT NOT NULL,
                              time TEXT NOT NULL,
                              date TEXT NOT NULL,
                              place TEXT NOT NULL,
                              description TEXT NOT NULL,
                              FOREIGN KEY (city_id) REFERENCES cities (id) ON DELETE CASCADE,
                              CONSTRAINT unique_event UNIQUE (city_id, name, place, date)
                              );
                             
                              """;
        String createPinned = """
                              CREATE TABLE IF NOT EXISTS pinned(
                              eventID INTEGER NOT NULL,
                              userID INTEGER NOT NULL,
                              FOREIGN KEY (eventID) REFERENCES events (id) ON DELETE CASCADE,
                              FOREIGN KEY (userID) REFERENCES users (id) ON DELETE CASCADE,
                              CONSTRAINT unique_event UNIQUE (eventID,userID));
                              """;
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            
            stmt.execute(createCities);
            stmt.execute(createEvents);
            stmt.execute(createPinned);
            System.out.println("DATABASE INITIALIZED SUCCESSFULLY: ");
            
        } catch(SQLException e){
            System.err.println("DATABASE INITIALIZATION FAILED: ");
        }
                
        
    
    }
    
    public static void accountDatabase() {
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:eventdb.db");
         Statement stmt = connection.createStatement()) {

        // Create the 'users' table if it doesn't exist
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL CHECK (role IN ('admin', 'user')),
                nickname TEXT,
                contact TEXT,          
                profile_pic BLOB);
        """;
        
        stmt.execute(createTableQuery);

        // Check if admin account exists
        String checkAdminQuery = "SELECT COUNT(*) AS count FROM users WHERE username = 'admin'";
        ResultSet rs = stmt.executeQuery(checkAdminQuery);
        if (rs.next() && rs.getInt("count") > 0) {
            System.out.println("Admin account already exists!");
            return; // Exit the method if admin account exists
        }

        // Insert admin account
        String adminPassword = "admin123"; // Default password
        String hashedPassword = hashPassword(adminPassword);

        String insertAdminQuery = """
            INSERT INTO users (username, password, role) 
            VALUES ('admin', ?, 'admin');
        """;
        PreparedStatement pstmt = connection.prepareStatement(insertAdminQuery);
        pstmt.setString(1, hashedPassword);
        pstmt.executeUpdate();

        System.out.println("Admin account initialized successfully!");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database initialization failed: " + e.getMessage());
    }
}
    
    public static void adminLogin(String username, String password) {
    try {
        String role = validateUserRole(username, password);
        if (role == null) {
            System.out.println("Invalid login. Check username and password.");
        } else if ("admin".equals(role)) {
            System.out.println("Admin login successful!");
        } else {
            System.out.println("Access denied. Not an admin.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    
    public static String hashPassword(String password) {
    try {
        // Generate a random salt
        byte[] salt = generateSalt();
        
        // Convert the salt to a string for storage
        String saltString = Base64.getEncoder().encodeToString(salt);

        // Hash the password using PBKDF2
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // 128-bit hash
        byte[] hashedPassword = skf.generateSecret(spec).getEncoded();

        // Convert the hashed password to a string
        String hashedPasswordString = Base64.getEncoder().encodeToString(hashedPassword);

        // Return the combined salt and hashed password
        return saltString + ":" + hashedPasswordString;
    } catch (Exception e) {
        throw new RuntimeException("Error hashing password: " + e.getMessage());
    }
}

// Step 2.2: Validate a password during login
    public static boolean validatePassword(String password, String storedHash) throws Exception {
    String hashedPassword = hashPassword(password); // Hash the entered password
    return hashedPassword.equals(storedHash); // Compare it with the stored hash
}
    
    //ADD CITY IN DATABASE
    public static boolean addCity(String cityName) {
        String sql = "INSERT INTO cities (name) VALUES (?);";
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cityName);
            pstmt.executeUpdate();
            return true;
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Error Adding City: ");
            return false;
        }
    }
    
    public static List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        
        try(Connection conn = AdminFunction.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM cities;")) {
            while (rs.next()) {
                City city = new City(rs.getInt("id"), rs.getString("name"));
                cities.add(city);
                
            }
    
        } catch(SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Error Retreiving database: "+ e.getMessage());
            
        }
        return cities;
    }
    
    
    public static int getCityId(String cityName) {
            try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM cities WHERE name = ? ;")){
                
                pstmt.setString(1, cityName);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch(SQLException e) {
                System.err.println("Error getting City ID: " + e.getMessage());
            }
            return -1;
}   
    
    public static String currentUsername;
    public static String currentPassword;
    public static int counter;
    public static int totalCounts;
    public static int totalCountsNow;
        
    
    
    
    
    public static void saveLoginInfo(int log,String username){
    
        String sql = "UPDATE users SET logged = ? where username = ?";
        
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
        pstmt.setInt(1, log);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
}
catch(SQLException e) {
                System.err.println("Error in updating log info: " + e.getMessage());
            }

        
        }
        
            
            
    
    
    public static boolean addEvent(int cityId, String eventName, String time, String date, String place, String description) {
        String sql = "INSERT INTO events (city_id, name, time, date, place, description) VALUES (?, ?, ?, ?, ?, ?);";
        
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cityId);
            pstmt.setString(2, eventName);
            pstmt.setString(3, time);
            pstmt.setString(4, date);
            pstmt.setString(5, place);
            pstmt.setString(6, description);
            pstmt.executeUpdate();
            return true;
        
        } catch (SQLException e) {
            System.err.println("Error adding event: " + e.getMessage());
        }
        return false;
    }
    public static int getUserID(){
    String sql = "SELECT id FROM users WHERE username = ? ;";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)){
                
                pstmt.setString(1, currentUsername);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch(SQLException e) {
                System.err.println("Error getting User ID: " + e.getMessage());
            }
        return -1;
    }
   public static String getDescription(int eventID){
    String sql = "SELECT description FROM events WHERE id = ? ";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)){
                
                pstmt.setInt(1, eventID);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getString("description");
                }
            } catch(SQLException e) {
                System.err.println("Error getting Description: " + e.getMessage());
            }
        return null;
    }
    
     public static boolean pinnedCheck(int userID, int eventID) {
        String checkSql = "SELECT COUNT(*) FROM pinned WHERE userID = ? AND eventID = ?";
        
        
        try(Connection conn = getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userID);
            checkStmt.setInt(2, eventID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
                
            }
            return true;
        
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }
 public static int pinCheck;
 
    public static boolean pinEvent(int userID, int eventID) {
        String checkSql = "SELECT COUNT(*) FROM pinned WHERE userID = ? AND eventID = ?";
        String sql = "INSERT INTO pinned (userID,eventID) VALUES (?, ?)";
        
        try(Connection conn = getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            checkStmt.setInt(1, userID);
            checkStmt.setInt(2, eventID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                pstmt.setInt(1, userID);
                pstmt.setInt(2, eventID);
                pstmt.executeUpdate();
                System.out.println("Event pinned successfully.");
                
                pinCheck = 1;
                } else {
                pinCheck = 2;
                System.out.println("This event is already pinned by the user.");
                
            }
            return true;
        
        } catch (SQLException e) {
            System.err.println("Error adding event: " + e.getMessage());
        }
        return false;
    }
    //Get events for a city
    public static List<Event> getCityEvents(int  cityId){
        List<Event> events = new ArrayList<>();
        
        try (Connection conn = AdminFunction.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, time, date, place, description FROM events WHERE city_id = ?"))
        {
            pstmt.setInt(1, cityId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {                
                Event event = new Event(
                 rs.getInt("id"), rs.getString("name"), rs.getString("time"), rs.getString("date"), rs.getString("place"), rs.getString("description"));
                events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Error Retreiving events: "+ e.getMessage());
        }
        return events;
    }
    
    public static List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        
        try (Connection conn = AdminFunction.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, username, nickname FROM users;"))
        {
           
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {                
                User user = new User(
                    rs.getInt("id"),rs.getString("username"),rs.getString("nickname"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Error Retreiving events: "+ e.getMessage());
        }
        return users;
    }
  public static void adjustRowHeights(JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();

            for (int column = 0; column < table.getColumnCount(); column++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(cellRenderer, row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }

            table.setRowHeight(row, rowHeight);
        }
    }
   
   public static List<Notifications> getNotifications(String date){
        List<Notifications> notifications = new ArrayList<>();
        
        try (Connection conn = AdminFunction.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT e.name AS eventName, c.name AS cityName FROM pinned p JOIN events e ON p.EventID = e.id JOIN cities c ON c.id = e.city_id WHERE e.date = ? AND p.userID = ? ORDER BY e.date ASC;"))
        {
            
            int userId = getUserID();
            pstmt.setString(1,date);
            pstmt.setInt(2,userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {                
                Notifications notification = new Notifications(
                    rs.getString("cityName"), rs.getString("eventName"));
                notifications.add(notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Error Retreiving events: "+ e.getMessage());
        }
        return notifications;
    }
    public static List<Notifications> getNotificationsForDays(int days) {
        List<Notifications> notifications = new ArrayList<>();
        String targetDate = LocalDate.now().plusDays(days).toString();
        int userId = AdminFunction.getUserID();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT e.name AS eventName, c.name AS cityName FROM pinned p JOIN events e ON p.EventID = e.id JOIN cities c ON c.id = e.city_id WHERE p.userID = ? AND e.date = ?")) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, targetDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Notifications notification = new Notifications(
                        rs.getString("cityName"), rs.getString("eventName"));
                notifications.add(notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Error retrieving events: " + e.getMessage());
        }
        return notifications;
    }
   
    public static List<Event> getAllPinned(){
        List<Event> pinnedEvents = new ArrayList<>();
        
        try (Connection conn = AdminFunction.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT e.id, e.name, e.time, e.date, e.place, e.description FROM pinned p JOIN events e ON p.EventID = e.id JOIN cities c ON c.id = e.city_id WHERE p.userID = ?;"))
        {
            
            int userId = getUserID();
            pstmt.setInt(1,userId );

            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {                
                Event event = new Event(
                    rs.getInt("id"), rs.getString("name"), rs.getString("time"), rs.getString("date"), rs.getString("place"), rs.getString("description"));
                pinnedEvents.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Error Retreiving events: "+ e.getMessage());
        }
        return pinnedEvents;
    }
    
    public static List<String> search(String query) {
    List<String> results = new ArrayList<>();

    // Example implementation: search through cities and events
    for (City city : getAllCities()) {
        if (city.getName().toLowerCase().contains(query.toLowerCase())) {
            results.add(city.getName());
        }

        for (Event event : getCityEvents(city.getId())) {
            if (event.event_name().toLowerCase().contains(query.toLowerCase())) {
                results.add(event.event_name() + " (in " + city.getName() + ")");
            }
        }
    }

    return results;
}
public static boolean deleteEvent(int cityId, String eventName, String eventTime, String eventDate, String eventPlace) {
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:eventdb.db")) {
        String query = "DELETE FROM events WHERE city_id = ? AND name = ? AND time = ? AND date = ? AND place = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, cityId);
            pstmt.setString(2, eventName);
            pstmt.setString(3, eventTime);
            pstmt.setString(4, eventDate);
            pstmt.setString(5, eventPlace);
            

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public static boolean deleteCity(int id) {
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:eventdb.db")) {
        String query = "DELETE FROM cities WHERE id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            

            int affectedRows = pstmt.executeUpdate();
            return true;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    public static boolean resetPassword(String username, String newUsername, String password) {
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:eventdb.db")) {
        String query = "UPDATE users SET password = ?, username = ? WHERE  username = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, hashPassword(password));
            pstmt.setString(2, newUsername);
            pstmt.setString(3, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    public static boolean editProfile(String username, String name, String caption) {
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:eventdb.db")) {
        String query = "UPDATE users SET name = ?, caption = ? WHERE  username = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, caption);
            pstmt.setString(3, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    
     public static boolean deletePinnedEvent(int userID,String name, String time,String date, String place) {
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:eventdb.db")) {
        String query =  "DELETE FROM pinned WHERE eventID = (SELECT e.id FROM events e JOIN pinned p ON e.id = p.eventID WHERE e.name = ? AND e.place = ? AND e.time = ? AND e.date = ? AND p.userID = ?)";
;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(5, userID);
            pstmt.setString(1, name);
            pstmt.setString(3, time);
            pstmt.setString(4, date);
            pstmt.setString(2, place);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public static boolean registerUser(String username, String password) {
    try (Connection connection = AdminFunction.getConnection()) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, AdminFunction.hashPassword(password)); // Hash the password
        pstmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
    
   public static String validateUserRole(String username, String password) {
    try (Connection conn = getConnection()) {
        String query = "SELECT password, role FROM users WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            String storedHash = rs.getString("password");
            String role = rs.getString("role");

            // Verify the entered password with the stored hash
            if (verifyPassword(password, storedHash)) {
                return role; // Return the role if the login is valid
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null; // Return null if login is invalid
}
    
    public static boolean verifyPassword(String enteredPassword, String storedHash) {
    try {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid stored hash format");
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(enteredPassword.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] enteredHash = factory.generateSecret(spec).getEncoded();

        return Arrays.equals(expectedHash, enteredHash);
    } catch (Exception e) {
        throw new RuntimeException("Error verifying password: " + e.getMessage());
        }
    }



public static byte[] generateSalt() {
    try {
        // Generate a 16-byte (128-bit) salt - need more understanding
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error generating salt: " + e.getMessage());
    }
}

    public String getEventDescriptionByCityId(String eventName) {
        String description = "";
        String query = "SELECT description FROM events WHERE name = ?";

        try (Connection conn = AdminFunction.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, eventName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    description = rs.getString("description");
                } else {
                    description = "No description available for this event.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            description = "Error retrieving description from the database.";
        }

        return description;
    }
    
    
}


