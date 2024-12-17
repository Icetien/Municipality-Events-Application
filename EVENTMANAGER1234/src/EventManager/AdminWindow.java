package EventManager;

import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultListModel;
import java.util.List;



public class AdminWindow extends javax.swing.JFrame {
    
    private DefaultListModel<String> cityListModel;
    private DefaultTableModel eventTableModel;
        
    public AdminWindow() {
        initComponents();

        cityListModel = new DefaultListModel<>();
        cityList.setModel(cityListModel);
        
        eventTableModel = new DefaultTableModel(new String[]{"Event", "Time", "Date", "Place"}, 0);
        eventTable.setModel(eventTableModel);
        
        
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        

        loadCities();
        suggestionPanel.setVisible(false);
        suggestionScroll.setVisible(false);
        
        searchField.addKeyListener(new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
        handleSearchInput(); // Call the method whenever the user types in the search field
    }
});
        
    }
   private void handleSearchInput() {
    String query = searchField.getText().trim();

    // Clear previous suggestions
    suggestionPanel.removeAll();

    if (query.isEmpty()) {
        // Hide suggestions if the query is empty
        
        suggestionPanel.setVisible(false);
    } else {
        try {
            // Perform the search using AdminFunction
            List<String> results = AdminFunction.search(query);

            if (results.isEmpty()) {
                suggestionPanel.setVisible(false); // No results, hide panel
                
            } else {
                // Display each result as a clickable label
                for (String result : results) {
                    JLabel suggestion = new JLabel(result);
                    suggestion.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    suggestion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    
                    // Add a click listener to each suggestion
                    suggestion.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            searchField.setText(result); // Set the clicked value in the search field
                            displayResults(result); // Display results for the selected suggestion
                            suggestionPanel.setVisible(false); // Hide suggestion panel
                            
                        }
                    });

                    suggestionPanel.add(suggestion); // Add suggestion to the panel
                }

                // Refresh and show the suggestion panel
                suggestionPanel.revalidate();
                suggestionPanel.repaint();
                suggestionPanel.setVisible(true);
                suggestionPanel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during search: " + e.getMessage());
        }
    }
}

   private void displayResults(String result) {
    if (result.contains("(in ")) {
        // Extract the city name from the result (assumes "EventName (in CityName)" format)
        String cityName = result.substring(result.indexOf("(in ") + 4, result.length() - 1);
        updateCityAndEvents(cityName);
    } else {
        // Assume the result is a city name
        updateCityAndEvents(result);
        
    }
}

private void updateCityAndEvents(String cityName) {
    for (int i = 0; i < cityListModel.getSize(); i++) {
        if (cityListModel.get(i).equalsIgnoreCase(cityName)) {
            cityList.setSelectedIndex(i); // Select city in city list
            break;
        }
    }
    loadEvents(); // Load events for the selected city
}


    
    private void loadCities() {
        cityListModel.clear();
        try{
        List<City> cities = AdminFunction.getAllCities();
        for(City city : cities){
            System.out.println("City Loaded: " + city.getName());
            cityListModel.addElement(city.getName());
            
        }
        loadEvents(); 
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error" + e.getMessage());
        
        }
    }
    
    private void loadEvents() {
        eventTableModel.setRowCount(0);
        
        try {
            int selectedCity = cityList.getSelectedIndex();
            if(selectedCity == -1)return;
        
            int cityId = AdminFunction.getAllCities().get(selectedCity).getId();
            List<Event> events = AdminFunction.getCityEvents(cityId);
            
            for(Event event : events) {
                System.out.println("Event loaded: " + event.event_name());
                eventTableModel.addRow(new Object[]{event.event_name(), event.time(),event.date(),event.place(), event.description()});
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error: " + e.getMessage());
        }   
        
    }
    
    private void addCityAction() {
        String cityName = JOptionPane.showInputDialog(this, "Enter city name:");
        if (cityName != null && !cityName.trim().isEmpty()) {
            AdminFunction.addCity(cityName);
            
            loadCities();
        }
    }
    
   private void addEventAction() {
        int selectedCityIndex = cityList.getSelectedIndex();
        if (selectedCityIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a city first!");
            return;
        }
        
        String eventName = eventNameField.getText();
        String eventTime = eventTimeField.getText();
        String eventDate = eventDateField.getText();
        String eventPlace = eventPlaceField.getText();
        String eventDescription = eventDescriptionField.getText();
        if (eventName != null && eventTime != null && eventDate != null && eventPlace != null && eventDescription != null &&
            !eventName.trim().isEmpty() && !eventTime.trim().isEmpty() && !eventDate.trim().isEmpty() && !eventPlace.trim().isEmpty() && !eventDescription.trim().isEmpty()) {
            int cityId = AdminFunction.getAllCities().get(selectedCityIndex).getId();
            AdminFunction.addEvent(cityId, eventName, eventTime, eventDate,eventPlace, eventDescription);
            
        }
        loadCities();

   }
   
   private void deleteCityAction(){
            
    int selectedCityIndex = cityList.getSelectedIndex();
    if (selectedCityIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a city first!");
            return;}
    
       int cityId = AdminFunction.getAllCities().get(selectedCityIndex).getId();
       boolean success = AdminFunction.deleteCity(cityId);
        if (success) {
            // Remove from the table model
            cityListModel.removeElement(selectedCityIndex);
            JOptionPane.showMessageDialog(this, "Event deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete the event. Please try again.");
        }
        }
       
   private void deleteEventAction() {
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
        String eventName = (String) eventTableModel.getValueAt(selectedRow, 0);
        String eventTime = (String) eventTableModel.getValueAt(selectedRow, 1);
        String eventDate = (String) eventTableModel.getValueAt(selectedRow, 2);
        String eventPlace = (String) eventTableModel.getValueAt(selectedRow, 3);
        

        // Get the city ID (ensure a city is selected)
        int selectedCityIndex = cityList.getSelectedIndex();
        if (selectedCityIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a city first!");
            return;
        }
        int cityId = AdminFunction.getAllCities().get(selectedCityIndex).getId();

        // Call the AdminFunction to delete the event from the database
        boolean success = AdminFunction.deleteEvent(cityId, eventName, eventTime, eventDate, eventPlace);

        if (success) {
            // Remove from the table model
            eventTableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Event deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete the event. Please try again.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error during deletion: " + e.getMessage());
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

        jPanel8 = new javax.swing.JPanel();
        menuBar1 = new java.awt.MenuBar();
        popupMenu = new javax.swing.JPopupMenu();
        menuItemDashboard = new javax.swing.JMenuItem();
        menuItemLogout = new javax.swing.JMenuItem();
        jButton2 = new javax.swing.JButton();
        layeredPane = new javax.swing.JLayeredPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        btnMenu = new javax.swing.JButton();
        suggestionScroll = new javax.swing.JScrollPane();
        suggestionPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        addCity = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        addCity1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        cityList = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();
        addEvent = new javax.swing.JButton();
        deleteEvent = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        addFrame = new javax.swing.JInternalFrame();
        jPanel7 = new javax.swing.JPanel();
        addPanel = new javax.swing.JPanel();
        eventNameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        eventTimeField = new javax.swing.JTextField();
        eventDateField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        eventPlaceField = new javax.swing.JTextField();
        eventDescriptionField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        menuItemDashboard.setText("Dashboard");
        menuItemDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDashboardActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDashboard);

        menuItemLogout.setText("Logout");
        menuItemLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLogoutActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemLogout);

        jButton2.setText("jButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));

        layeredPane.setPreferredSize(new java.awt.Dimension(800, 500));

        jPanel5.setBackground(new java.awt.Color(102, 0, 255));
        jPanel5.setMinimumSize(new java.awt.Dimension(800, 500));
        jPanel5.setPreferredSize(new java.awt.Dimension(800, 500));

        jPanel1.setBackground(new java.awt.Color(102, 0, 255));

        searchField.setText("Search");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchFieldFocusLost(evt);
            }
        });
        searchField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchFieldMouseClicked(evt);
            }
        });
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        btnMenu.setBackground(new java.awt.Color(102, 0, 255));
        btnMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/menu (2).png"))); // NOI18N
        btnMenu.setBorder(null);
        btnMenu.setPreferredSize(new java.awt.Dimension(30, 30));
        btnMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuActionPerformed(evt);
            }
        });

        jLayeredPane1.setLayer(btnMenu, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );

        suggestionPanel.setLayout(new javax.swing.OverlayLayout(suggestionPanel));
        suggestionScroll.setViewportView(suggestionPanel);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/misor.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(130, 130, 130)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .addComponent(suggestionScroll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(suggestionScroll))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(null);

        addCity.setBackground(java.awt.SystemColor.inactiveCaptionBorder);
        addCity.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addCity.setForeground(new java.awt.Color(255, 255, 255));
        addCity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/addbtn.png"))); // NOI18N
        addCity.setBorder(null);
        addCity.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addCity.setOpaque(true);
        addCity.setPreferredSize(new java.awt.Dimension(40, 40));
        addCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCityActionPerformed(evt);
            }
        });
        jPanel6.add(addCity);
        addCity.setBounds(143, 6, 40, 35);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setText("Municipalities");
        jPanel6.add(jLabel1);
        jLabel1.setBounds(6, 6, 120, 35);

        addCity1.setBackground(java.awt.SystemColor.inactiveCaptionBorder);
        addCity1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addCity1.setForeground(new java.awt.Color(255, 255, 255));
        addCity1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/removebtn.png"))); // NOI18N
        addCity1.setBorder(null);
        addCity1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addCity1.setOpaque(true);
        addCity1.setPreferredSize(new java.awt.Dimension(40, 40));
        addCity1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCity1ActionPerformed(evt);
            }
        });
        jPanel6.add(addCity1);
        addCity1.setBounds(189, 6, 40, 35);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        cityList.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        cityList.setForeground(new java.awt.Color(0, 51, 51));
        cityList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        cityList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                cityListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(cityList);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(102, 102, 255));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName(""); // NOI18N
        jScrollPane2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                addEventPerformed(evt);
            }
        });

        eventTable.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        eventTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(eventTable);

        addEvent.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addEvent.setForeground(new java.awt.Color(0, 102, 102));
        addEvent.setText("Add");
        addEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEventActionPerformed(evt);
            }
        });

        deleteEvent.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        deleteEvent.setForeground(new java.awt.Color(0, 102, 102));
        deleteEvent.setText("Del");
        deleteEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEventActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Events");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(668, 668, 668))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        addFrame.setClosable(true);
        addFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addFrame.setVisible(true);
        addFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                addFrameInternalFrameClosing(evt);
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
        addFrame.getContentPane().setLayout(new javax.swing.OverlayLayout(addFrame.getContentPane()));

        jPanel7.setLayout(new javax.swing.OverlayLayout(jPanel7));

        eventNameField.setColumns(2);
        eventNameField.setText("Enter Event Name");
        eventNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eventNameFieldFocusGained(evt);
            }
        });

        jLabel3.setText("Event Name:");

        jLabel4.setText("Event Time:");

        eventTimeField.setText("Enter Event Time");
        eventTimeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eventTimeFieldFocusGained(evt);
            }
        });

        eventDateField.setText("Enter Event Date");
        eventDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eventDateFieldFocusGained(evt);
            }
        });

        jLabel5.setText("Event Date (YYYY-MM-DD)");

        jLabel6.setText("Event Place");

        eventPlaceField.setText("Enter Event Place");
        eventPlaceField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eventPlaceFieldFocusGained(evt);
            }
        });

        eventDescriptionField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        eventDescriptionField.setText("Enter Event Description");
        eventDescriptionField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eventDescriptionFieldFocusGained(evt);
            }
        });

        jLabel7.setText("Event Description");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("Add Event");

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addPanelLayout = new javax.swing.GroupLayout(addPanel);
        addPanel.setLayout(addPanelLayout);
        addPanelLayout.setHorizontalGroup(
            addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addPanelLayout.createSequentialGroup()
                        .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(eventNameField)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                            .addComponent(eventDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(62, 62, 62)
                        .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventPlaceField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eventDescriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        addPanelLayout.setVerticalGroup(
            addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eventTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eventDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(addPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eventPlaceField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eventDescriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.add(addPanel);

        addFrame.getContentPane().add(jPanel7);

        layeredPane.setLayer(jPanel5, javax.swing.JLayeredPane.DEFAULT_LAYER);
        layeredPane.setLayer(addFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layeredPaneLayout = new javax.swing.GroupLayout(layeredPane);
        layeredPane.setLayout(layeredPaneLayout);
        layeredPaneLayout.setHorizontalGroup(
            layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layeredPaneLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layeredPaneLayout.createSequentialGroup()
                    .addGap(200, 200, 200)
                    .addComponent(addFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(200, Short.MAX_VALUE)))
        );
        layeredPaneLayout.setVerticalGroup(
            layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layeredPaneLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layeredPaneLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(addFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(20, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDashboardActionPerformed
        AdminDashboard dashboard = new AdminDashboard();
        dashboard.setLocationRelativeTo(null);
        dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menuItemDashboardActionPerformed

    private void deleteEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEventActionPerformed
        deleteEvent.addActionListener(e -> deleteEventAction());
    }//GEN-LAST:event_deleteEventActionPerformed

    private void addEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEventActionPerformed
        
        addFrame.setVisible(true);
        layeredPane.moveToFront(addFrame);
        eventDateField.setText("Enter Event Date");
        eventTimeField.setText("Enter Event Time");
        eventDescriptionField.setText("Enter Event Description");
        eventPlaceField.setText("Enter Event Place");
        eventNameField.setText("Enter Event Name");
        
    }//GEN-LAST:event_addEventActionPerformed

    private void addEventPerformed(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_addEventPerformed

    }//GEN-LAST:event_addEventPerformed

    private void cityListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_cityListValueChanged
        cityList.addListSelectionListener(e ->{
            String selectedCity = cityList.getSelectedValue();
            if(selectedCity != null){
                loadEvents();
            }
        });
    }//GEN-LAST:event_cityListValueChanged

    private void addCity1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCity1ActionPerformed
        deleteCityAction();
        loadCities();
    }//GEN-LAST:event_addCity1ActionPerformed

    private void addCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCityActionPerformed
        addCity.addActionListener(e ->addCityAction());
        cityList.addListSelectionListener(e ->{
            if(!e.getValueIsAdjusting()){
                loadEvents();
            }
        });
    }//GEN-LAST:event_addCityActionPerformed

    private void btnMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuActionPerformed
        popupMenu.show(btnMenu, popupMenu.getWidth(), btnMenu.getHeight());
    }//GEN-LAST:event_btnMenuActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                handleSearchInput(); // Call the method whenever the user types in the search field
            }

        });

    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMouseClicked
        searchField.setText(null);
    }//GEN-LAST:event_searchFieldMouseClicked

    private void searchFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFieldFocusLost
        searchField.setText("Search");
    }//GEN-LAST:event_searchFieldFocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addEventAction();
        layeredPane.moveToBack(addFrame);
        addFrame.setVisible(false);
        
        eventDateField.setText("Enter Event Date");
        eventTimeField.setText("Enter Event Time");
        eventDescriptionField.setText("Enter Event Description");
        eventPlaceField.setText("Enter Event Place");
        eventNameField.setText("Enter Event Name");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void addFrameInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_addFrameInternalFrameClosing
        addFrame.setVisible(false);
        layeredPane.moveToBack(addFrame);
    }//GEN-LAST:event_addFrameInternalFrameClosing

    private void menuItemLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "SEE YAH!!");
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setLocationRelativeTo(null);
            loginWindow.setVisible(true);
            this.dispose(); // 
        }
    }//GEN-LAST:event_menuItemLogoutActionPerformed

    private void eventNameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eventNameFieldFocusGained
        eventNameField.setText("");
    }//GEN-LAST:event_eventNameFieldFocusGained

    private void eventTimeFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eventTimeFieldFocusGained
        eventTimeField.setText("");
    }//GEN-LAST:event_eventTimeFieldFocusGained

    private void eventPlaceFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eventPlaceFieldFocusGained
        eventPlaceField.setText("");
    }//GEN-LAST:event_eventPlaceFieldFocusGained

    private void eventDescriptionFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eventDescriptionFieldFocusGained
        eventDescriptionField.setText("");
    }//GEN-LAST:event_eventDescriptionFieldFocusGained

    private void eventDateFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eventDateFieldFocusGained
        eventDateField.setText("");
    }//GEN-LAST:event_eventDateFieldFocusGained

    /**

    
    
    public static void main(String args[]) {
      
    }**/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCity;
    private javax.swing.JButton addCity1;
    private javax.swing.JButton addEvent;
    private javax.swing.JInternalFrame addFrame;
    private javax.swing.JPanel addPanel;
    private javax.swing.JButton btnMenu;
    private javax.swing.JList<String> cityList;
    private javax.swing.JButton deleteEvent;
    private javax.swing.JTextField eventDateField;
    private javax.swing.JTextField eventDescriptionField;
    private javax.swing.JTextField eventNameField;
    private javax.swing.JTextField eventPlaceField;
    private javax.swing.JTable eventTable;
    private javax.swing.JTextField eventTimeField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLayeredPane layeredPane;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JMenuItem menuItemDashboard;
    private javax.swing.JMenuItem menuItemLogout;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel suggestionPanel;
    private javax.swing.JScrollPane suggestionScroll;
    // End of variables declaration//GEN-END:variables
}
