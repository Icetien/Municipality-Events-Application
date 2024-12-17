package EventManager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class NotificationFrame extends JFrame {
    private JTextArea notificationArea;

    public NotificationFrame(JFrame parent) {
        setTitle("Notifications");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        add(new JScrollPane(notificationArea), BorderLayout.CENTER);

        loadNotifications();
        setLocationRelativeTo(parent); // Center the NotificationFrame on the screen
    }

    private void loadNotifications() {
        notificationArea.setText(""); // Clear the text area first
        int[] daysArray = {7, 3, 1, 0};
        for (int days : daysArray) {
            List<Notifications> notifications = AdminFunction.getNotificationsForDays(days);
            for (Notifications notification : notifications) {
                String message = getMessageForDays(days);
                notificationArea.append("Reminder: " + notification + " - " + message + "\n");
            }
        }}
    private String getMessageForDays(int days) {
        switch (days) {
            case 7:
                return "Just 7 days left. Don't forget to prepare.";
            case 3:
                return "Only 3 days left. Get ready!";
            case 1:
                return "It's happening tomorrow. Make sure you're prepared!";
            case 0:
                return "It's happening today. Don't miss it!";
            default:
                return "";
        }
    }  
}
