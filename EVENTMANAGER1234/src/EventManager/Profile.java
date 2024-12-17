
package EventManager;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class Profile extends javax.swing.JFrame {
    private static final String DefaultProfile = System.getProperty("user.dir") + "/prof1.png";
    private DefaultTableModel pinnedEventTableModel;
    public Profile() {
        initComponents();
        
        pinnedEventTableModel = new DefaultTableModel(new String[]{"Event", "Time", "Date", "Place"}, 0) {
        @Override
    public boolean isCellEditable(int row, int column) {
        // Only allow the "Actions" column to be editable
        return column == 4;  // 4 corresponds to the "Actions" column index
    }
};    
        
        eventTable.setModel(pinnedEventTableModel);
        loadEvents();
        loadProfileDetails();
        editProfile.setVisible(false);
        editLoginInfo5.setVisible(false);
        
        Edit_Profile.setText("Edit Profile");
        Home.setText("Home");
        account.setText("Account");
        logout.setText("Logout");
        
    }
private void loadEvents() {
        pinnedEventTableModel.setRowCount(0);
        try{
        List<Event> events = AdminFunction.getAllPinned();
            System.out.println(events);
            for(Event event : events) {
                System.out.println("Event loaded: " + event.event_name());
                pinnedEventTableModel.addRow(new Object[]{event.event_name(), event.time(),event.date(),event.place()});
                AdminFunction.adjustRowHeights(eventTable);
                
        }
           
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error" + e.getMessage());
        
        }
    }

private void editProfileAction() {
        
        
        String Name = nicknameLabel1.getText();
        String Description = discriptionLabel1.getText();
        String Username = AdminFunction.currentUsername;
        
        if (Name != null && Description != null && 
            !Name.trim().isEmpty() && !Description.trim().isEmpty()) {
                AdminFunction.editProfile(Username, Name, Description);
        }else if (Name != null && Description != null && 
            !Name.trim().isEmpty() && !Description.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name and caption!");
        }else if(Name == null || Name.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter your Name!");
            
        }else if(Description == null || Description.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a caption!");
            
        }
}
private void changeInfoAction() {
        
        
        String newUsername = usernameField.getText();
        String Password = passwordField.getText();
        String Username = AdminFunction.currentUsername;
        
        if (newUsername != null && Password != null && 
            !newUsername.trim().isEmpty() && !Password.trim().isEmpty()) {
                AdminFunction.resetPassword(Username, newUsername, Password);
        }else if (newUsername == null && Password == null && 
            newUsername.trim().isEmpty() && Password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username or a password!");
        }else if(newUsername == null || newUsername.trim().isEmpty()){
                newUsername = AdminFunction.currentUsername;
                AdminFunction.resetPassword(Username, newUsername, Password);
            
        }else if(Password == null || Password.trim().isEmpty()){
                Password = AdminFunction.currentPassword;
                AdminFunction.resetPassword(Username, newUsername, Password);
            
        }



   }

    
    public void addProfile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Choose photo");
    int res = fileChooser.showOpenDialog(this);
    
    if (res == fileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] profPIC = new byte[(int) file.length()];
            fis.read(profPIC);
            
            // Update query with proper placeholders for nickname and contact
            try (Connection conn = AdminFunction.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET profile_pic = ? WHERE username = ?");
                pstmt.setBytes(1, profPIC);
                pstmt.setString(2, AdminFunction.currentUsername);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Profile updated successfully.");
                    loadProfileDetails();  // Reload profile picture after update
                } else {
                    System.out.println("Failed to update profile.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    
    public void addProfileDetails() {
        String nickname = nicknameLabel1.getText(); // Assuming this gets the nickname
        if (nickname == null || nickname.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nickname must not be empty!!");
        return; // Exit the method if the nickname is invalid
        }
        
    
            try (Connection conn = AdminFunction.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET nickname = ?, contact = ? WHERE username = ?");
                pstmt.setString(1, nicknameLabel1.getText());  // Assuming nicknameTextField holds the nickname
 
                pstmt.setString(2, discriptionLabel1.getText());   // Assuming contactTextField holds the contact info

                pstmt.setString(3, AdminFunction.currentUsername);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Profile updated successfully.");
                    loadProfileDetails();  // Reload profile picture after update
                    editProfile.dispose();
                } else {
                    System.out.println("Failed to update profile.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    
    public void loadProfileDetails() {
    try (Connection conn = AdminFunction.getConnection()) {
        // SQL query to retrieve nickname, contact, and profile_pic
        PreparedStatement pstmt = conn.prepareStatement("SELECT nickname, contact, profile_pic FROM users WHERE username = ?");
        pstmt.setString(1, AdminFunction.currentUsername);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            // Load nickname and contact
            String nickname = rs.getString("nickname");
            String contact = rs.getString("contact");
            

            nicknameLabel.setText(nickname != null ? nickname : AdminFunction.currentUsername);
            contactLabel.setText(contact != null ? contact : "Add Bio");;
            
            // Load and set the profile picture
            byte[] image = rs.getBytes("profile_pic");
            if (image != null) {
                ImageIcon profilePicIcon = new ImageIcon(image);
                picLabel.setIcon(profilePicIcon);  // Assuming picLabel is where you display the profile picture
            } else {
                ImageIcon defaultprof = new ImageIcon(DefaultProfile);
                picLabel.setIcon(defaultprof);
            }
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu2 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        Edit_Profile = new javax.swing.JMenuItem();
        account = new javax.swing.JMenuItem();
        Home = new javax.swing.JMenuItem();
        logout = new javax.swing.JMenuItem();
        jPanel10 = new javax.swing.JPanel();
        layeredPane = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        nicknameLabel = new javax.swing.JLabel();
        contactLabel = new javax.swing.JLabel();
        picLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        settings = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        editProfile = new javax.swing.JInternalFrame();
        jPanel11 = new javax.swing.JPanel();
        addPanel = new javax.swing.JPanel();
        nicknameLabel1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        discriptionLabel1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        picLabel1 = new javax.swing.JPanel();
        editLoginInfo5 = new javax.swing.JInternalFrame();
        jPanel18 = new javax.swing.JPanel();
        addPanel6 = new javax.swing.JPanel();
        usernameField = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        passwordField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();

        jMenuItem1.setText("Notifications");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        popupMenu2.add(jMenuItem1);

        Edit_Profile.setText("jMenuItem1");
        Edit_Profile.setName("Edit Profile"); // NOI18N
        Edit_Profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Edit_ProfileActionPerformed(evt);
            }
        });
        popupMenu2.add(Edit_Profile);
        Edit_Profile.getAccessibleContext().setAccessibleDescription("");

        account.setText("jMenuItem1");
        account.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountActionPerformed(evt);
            }
        });
        popupMenu2.add(account);

        Home.setText("jMenuItem1");
        Home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeActionPerformed(evt);
            }
        });
        popupMenu2.add(Home);

        logout.setText("jMenuItem1");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });
        popupMenu2.add(logout);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(800, 500));

        jPanel10.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel10.setLayout(null);

        layeredPane.setPreferredSize(new java.awt.Dimension(800, 500));

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel1.setLayout(null);

        jPanel3.setBackground(new java.awt.Color(102, 102, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(800, 300));

        eventTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(eventTable);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("PINNED EVENTS");

        jButton2.setBackground(new java.awt.Color(102, 102, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/removebtn.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        nicknameLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        nicknameLabel.setForeground(new java.awt.Color(255, 255, 255));
        nicknameLabel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                nicknameLabelPropertyChange(evt);
            }
        });

        contactLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        contactLabel.setForeground(new java.awt.Color(255, 255, 255));
        contactLabel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                contactLabelPropertyChange(evt);
            }
        });

        picLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        picLabel.setPreferredSize(new java.awt.Dimension(100, 100));
        picLabel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                picLabelPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(picLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contactLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nicknameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(nicknameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(contactLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(picLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addComponent(jLabel3))
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(0, 113, 800, 390);

        jPanel2.setBackground(new java.awt.Color(102, 0, 255));

        settings.setBackground(new java.awt.Color(102, 0, 255));
        settings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/Gear Icon.png"))); // NOI18N
        settings.setBorder(null);
        settings.setPreferredSize(new java.awt.Dimension(40, 40));
        settings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/misor.png"))); // NOI18N
        jLabel1.setText("jLabel1");

        jButton4.setBackground(new java.awt.Color(102, 0, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/Back icon.png"))); // NOI18N
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(293, 293, 293)
                .addComponent(settings, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jButton4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(settings, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 800, 120);

        jButton3.setBackground(new java.awt.Color(102, 102, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/Back icon.png"))); // NOI18N
        jButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(jButton3);
        jButton3.setBounds(0, 0, 32, 32);

        layeredPane.add(jPanel1);
        jPanel1.setBounds(0, 0, 800, 500);

        editProfile.setClosable(true);
        editProfile.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        editProfile.setPreferredSize(new java.awt.Dimension(450, 320));
        editProfile.setVisible(true);
        editProfile.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                editProfileInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        editProfile.getContentPane().setLayout(new javax.swing.OverlayLayout(editProfile.getContentPane()));

        jPanel11.setLayout(new javax.swing.OverlayLayout(jPanel11));

        addPanel.setPreferredSize(new java.awt.Dimension(450, 320));

        nicknameLabel1.setColumns(2);
        nicknameLabel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nicknameLabel1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Name");

        jLabel5.setText("Description");

        discriptionLabel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discriptionLabel1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setText("Edit Profile");

        jButton1.setText("Save");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        picLabel1.setBackground(new java.awt.Color(153, 255, 255));
        picLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                picLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout picLabel1Layout = new javax.swing.GroupLayout(picLabel1);
        picLabel1.setLayout(picLabel1Layout);
        picLabel1Layout.setHorizontalGroup(
            picLabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        picLabel1Layout.setVerticalGroup(
            picLabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout addPanelLayout = new javax.swing.GroupLayout(addPanel);
        addPanel.setLayout(addPanelLayout);
        addPanelLayout.setHorizontalGroup(
            addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(addPanelLayout.createSequentialGroup()
                        .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(addPanelLayout.createSequentialGroup()
                                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(nicknameLabel1)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                                .addGap(62, 62, 62)
                                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(discriptionLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(picLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        addPanelLayout.setVerticalGroup(
            addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(picLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nicknameLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(discriptionLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        jPanel11.add(addPanel);

        editProfile.getContentPane().add(jPanel11);

        layeredPane.add(editProfile);
        editProfile.setBounds(175, 90, 450, 320);

        editLoginInfo5.setClosable(true);
        editLoginInfo5.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        editLoginInfo5.setPreferredSize(new java.awt.Dimension(410, 200));
        editLoginInfo5.setVisible(true);
        editLoginInfo5.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                editLoginInfo5InternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        editLoginInfo5.getContentPane().setLayout(new javax.swing.OverlayLayout(editLoginInfo5.getContentPane()));

        jPanel18.setLayout(new javax.swing.OverlayLayout(jPanel18));

        addPanel6.setPreferredSize(new java.awt.Dimension(410, 200));

        usernameField.setColumns(2);
        usernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameFieldActionPerformed(evt);
            }
        });

        jLabel22.setText("Username");

        jLabel23.setText("Password");

        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel24.setText("Change Login Information");

        jButton9.setText("Save");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addPanel6Layout = new javax.swing.GroupLayout(addPanel6);
        addPanel6.setLayout(addPanel6Layout);
        addPanel6Layout.setHorizontalGroup(
            addPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton9))
                    .addGroup(addPanel6Layout.createSequentialGroup()
                        .addGroup(addPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(addPanel6Layout.createSequentialGroup()
                                .addGroup(addPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(usernameField)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                                .addGap(62, 62, 62)
                                .addGroup(addPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        addPanel6Layout.setVerticalGroup(
            addPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel18.add(addPanel6);

        editLoginInfo5.getContentPane().add(jPanel18);

        layeredPane.setLayer(editLoginInfo5, javax.swing.JLayeredPane.PALETTE_LAYER);
        layeredPane.add(editLoginInfo5);
        editLoginInfo5.setBounds(195, 150, 410, 200);

        jPanel10.add(layeredPane);
        layeredPane.setBounds(0, 0, 800, 500);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void settingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsActionPerformed
        popupMenu2.show(settings, (settings.getWidth() - popupMenu2.getPreferredSize().width) / 2 - 19, settings.getHeight() + 5);

        System.out.println(settings.getWidth());
        System.out.println(settings.getX());
    }//GEN-LAST:event_settingsActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an event to delete!");
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Get event details from the table
            int userID = AdminFunction.getUserID();
            String eventName = (String) pinnedEventTableModel.getValueAt(selectedRow, 0);
            String eventTime = (String) pinnedEventTableModel.getValueAt(selectedRow, 1);
            String eventDate = (String) pinnedEventTableModel.getValueAt(selectedRow, 2);
            String eventPlace = (String) pinnedEventTableModel.getValueAt(selectedRow, 3);

            // Call the AdminFunction to delete the event from the database
            boolean success = AdminFunction.deletePinnedEvent(userID, eventName, eventTime, eventDate, eventPlace);

            if (success) {
                // Remove from the table model
                pinnedEventTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Event deleted successfully!");
                loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the event. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during deletion: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void editProfileInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_editProfileInternalFrameClosing
        editProfile.setVisible(false);
        layeredPane.moveToBack(editProfile);
    }//GEN-LAST:event_editProfileInternalFrameClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    addProfileDetails();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void nicknameLabel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nicknameLabel1ActionPerformed

    }//GEN-LAST:event_nicknameLabel1ActionPerformed

    private void Edit_ProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Edit_ProfileActionPerformed
        editProfile.setVisible(true);
        layeredPane.moveToFront(editProfile);
    }//GEN-LAST:event_Edit_ProfileActionPerformed

    private void accountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountActionPerformed
        editLoginInfo5.setVisible(true);
        layeredPane.moveToFront(editLoginInfo5);
    }//GEN-LAST:event_accountActionPerformed

    private void usernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameFieldActionPerformed
        
    }//GEN-LAST:event_usernameFieldActionPerformed

    private void editLoginInfo5InternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_editLoginInfo5InternalFrameClosing
        editLoginInfo5.setVisible(false);
        layeredPane.moveToBack(editLoginInfo5);
    }//GEN-LAST:event_editLoginInfo5InternalFrameClosing

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordFieldActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        changeInfoAction();
        editLoginInfo5.setVisible(false);
        layeredPane.moveToBack(editLoginInfo5);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void picLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_picLabel1MouseClicked
        addProfile();

    }//GEN-LAST:event_picLabel1MouseClicked

    private void discriptionLabel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discriptionLabel1ActionPerformed

    }//GEN-LAST:event_discriptionLabel1ActionPerformed

    private void contactLabelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_contactLabelPropertyChange

    }//GEN-LAST:event_contactLabelPropertyChange

    private void nicknameLabelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_nicknameLabelPropertyChange

    }//GEN-LAST:event_nicknameLabelPropertyChange

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        addProfileDetails();

    }//GEN-LAST:event_jButton1MouseClicked

    private void picLabelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_picLabelPropertyChange

    }//GEN-LAST:event_picLabelPropertyChange

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        addProfileDetails();
    }//GEN-LAST:event_jButton1KeyPressed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        // Clear session data
        SessionManager.logout();
        
        JOptionPane.showMessageDialog(this, "SEE YAH!!");
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setLocationRelativeTo(null);
        loginWindow.setVisible(true);
        this.dispose(); // Close current window
    }
    }//GEN-LAST:event_logoutActionPerformed

    private void HomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeActionPerformed
        userWindow userw = new userWindow();
        userw.setVisible(true);
        userw.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_HomeActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       userWindow userw = new userWindow();
        userw.setVisible(true);
        userw.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
         NotificationFrame notificationFrame = new NotificationFrame(this);
                notificationFrame.toFront();
                notificationFrame.requestFocusInWindow();
                notificationFrame.setVisible(true);
               
    }//GEN-LAST:event_jMenuItem1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Edit_Profile;
    private javax.swing.JMenuItem Home;
    private javax.swing.JMenuItem account;
    private javax.swing.JPanel addPanel;
    private javax.swing.JPanel addPanel6;
    private javax.swing.JLabel contactLabel;
    private javax.swing.JTextField discriptionLabel1;
    private javax.swing.JInternalFrame editLoginInfo5;
    private javax.swing.JInternalFrame editProfile;
    private javax.swing.JTable eventTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLayeredPane layeredPane;
    private javax.swing.JMenuItem logout;
    private javax.swing.JLabel nicknameLabel;
    private javax.swing.JTextField nicknameLabel1;
    private javax.swing.JTextField passwordField;
    private javax.swing.JLabel picLabel;
    private javax.swing.JPanel picLabel1;
    private javax.swing.JPopupMenu popupMenu2;
    private javax.swing.JButton settings;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
