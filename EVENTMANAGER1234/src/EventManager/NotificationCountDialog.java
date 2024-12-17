/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EventManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;

public class NotificationCountDialog extends JDialog {
    private int userId;

    public NotificationCountDialog(JFrame parent) {
        super(parent, "Notification Count", true);
        this.userId = AdminFunction.getUserID();

        setLayout(new BorderLayout());

        int count7Days = AdminFunction.getNotificationsForDays(7).size();
        int count3Days = AdminFunction.getNotificationsForDays(3).size();
        int count1Day = AdminFunction.getNotificationsForDays( 1).size();
        int count0Days = AdminFunction.getNotificationsForDays(0).size();
        AdminFunction.totalCounts = count7Days + count3Days + count1Day + count0Days;
        
        
        JLabel countLabel = new JLabel("You have " + AdminFunction.totalCounts + " upcoming notifications.");

        JButton showNotificationsButton = new JButton("Show All Notifications");
        showNotificationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NotificationFrame notificationFrame = new NotificationFrame(parent);
                notificationFrame.toFront();
                notificationFrame.requestFocusInWindow();
                notificationFrame.setVisible(true);
                dispose();
                
            }
        });
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(countLabel, BorderLayout.CENTER);
        panel.add(showNotificationsButton, BorderLayout.EAST);

        add(panel, BorderLayout.CENTER);
        setSize(400, 150);
        setLocationRelativeTo(parent);
    }
    
}


