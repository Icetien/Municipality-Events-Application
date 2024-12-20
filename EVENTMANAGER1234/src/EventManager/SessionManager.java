/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EventManager;

/**
 *
 * @author DWIGHT
 */
public class SessionManager {

    private static boolean loggedIn = false;
    private static String username = "";
    private static String role = "";
    private static String sessionId = "";

    public static void setLoggedIn(boolean loggedIn) {
        SessionManager.loggedIn = loggedIn;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setUsername(String username) {
        SessionManager.username = username;
    }

    public static String getUsername() {
        return username;
    }

    public static void setRole(String role) {
        SessionManager.role = role;
    }

    public static String getRole() {
        return role;
    }

    public static void setSessionId(String sessionId) {
        SessionManager.sessionId = sessionId;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static void logout() {
        loggedIn = false;
        username = "";
        role = "";
        sessionId = "";
    }

}
