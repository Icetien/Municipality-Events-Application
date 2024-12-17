package EventManager;

import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultListModel;
import java.util.List;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.TableCellRenderer; 
import java.awt.*;
import java.awt.event.ActionEvent; 
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 *
 * @author DWIGHT
 */
public class userWindow extends javax.swing.JFrame {
    
    private DefaultListModel<String> cityListModel;
    private DefaultTableModel eventTableModel;
    
    
    public userWindow() {
        initComponents();
        
    
        cityListModel = new DefaultListModel<>();
        cityList.setModel(cityListModel);
        
        
        eventTableModel = new DefaultTableModel(new String[]{"Event", "Time", "Date", "Place", "Actions"}, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        // Only allow the "Actions" column to be editable
        return column == 4;  // 4 corresponds to the "Actions" column index
    }
};

// Set the updated table model
eventTable.setModel(eventTableModel);

// Retain the "Actions" column renderer and editor as-is
TableColumn actionsColumn = eventTable.getColumn("Actions");
actionsColumn.setCellRenderer(new ButtonRenderer());
actionsColumn.setCellEditor(new ButtonEditor(eventTable));
        loadCities();
        // layout panel gere
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        eventTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
         
        actionsColumn.setCellRenderer(new ButtonRenderer());
        actionsColumn.setCellEditor(new ButtonEditor(eventTable));
        
        layeredPane.moveToBack(descriptionFrame);
        descriptionFrame.setVisible(false);
        
        
        suggestionPanel.setVisible(false);

        eventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = eventTable.rowAtPoint(e.getPoint());
                int selectedCityIndex = cityList.getSelectedIndex();
                int cityId = AdminFunction.getAllCities().get(selectedCityIndex).getId(); 
                String event = (String) eventTable.getValueAt(selectedRow, 0);
                String time = (String) eventTable.getValueAt(selectedRow, 1);
                String date = (String) eventTable.getValueAt(selectedRow, 2);
                String place = (String) eventTable.getValueAt(selectedRow, 3);
                int eventId = AdminFunction.getCityEvents(cityId).get(selectedRow).getId();
                String description = AdminFunction.getDescription(eventId);
                layeredPane.moveToFront(descriptionFrame);
                descriptionFrame.setVisible(true);
                Description.setText(description);
                eventNameLabel.setText(event);
                eventTimeLabel.setText(time);
                eventDateLabel.setText(date);
                eventPlaceLabel.setText(place);
                System.out.println(description);
            }
        });
        setVisible(true);
        searchField.addKeyListener(new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
        handleSearchInput(); // call method when user searched
    }
});
        
      
    }
    
    
   private void handleSearchInput() {
    String query = searchField.getText().trim();

    // clear suggestion
    suggestionPanel.removeAll();

    if (query.isEmpty()) {
        // hide suggestion if empty
        suggestionPanel.setVisible(false);
    } else {
        try {
            // search using adminfucntion
            List<String> results = AdminFunction.search(query);

            if (results.isEmpty()) {
                suggestionPanel.setVisible(false); // hides if no resulr
            } else {
                // clickable results
                for (String result : results) {
                    JLabel suggestion = new JLabel(result);
                    suggestion.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    suggestion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    
                    // click listener
                    suggestion.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            searchField.setText(result); // set click value
                            displayResults(result); // display
                            suggestionPanel.setVisible(false); // hide search
                        }
                    });

                    suggestionPanel.add(suggestion); // add suggestions
                }

                // reffresh
                suggestionPanel.revalidate();
                suggestionPanel.repaint();
                suggestionPanel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during search: " + e.getMessage()); //debug
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
                eventTableModel.addRow(new Object[]{event.event_name(), event.time(),event.date(),event.place(),"Pin"});
                AdminFunction.adjustRowHeights(eventTable);
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error: " + e.getMessage());
        }   
        
    }


public class ButtonRenderer extends JPanel implements TableCellRenderer {
    private JButton pinButton;
    

    public ButtonRenderer() {
        setLayout(new FlowLayout());
        pinButton = new JButton("Pin");
        
        add(pinButton);
        
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}


public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton pinButton;
    
    
    public ButtonEditor(JTable table) {
        panel = new JPanel(new FlowLayout());
        pinButton = new JButton("Pin");
        
       
        pinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                
                int selectedCityIndex = cityList.getSelectedIndex();
                int cityId = AdminFunction.getAllCities().get(selectedCityIndex).getId(); 
                int userId = AdminFunction.getUserID();
                
                int eventId = AdminFunction.getCityEvents(cityId).get(row).getId();
                AdminFunction.pinEvent(userId,eventId);
                if (AdminFunction.pinCheck == 1){
                    JOptionPane.showMessageDialog(jPanel5, "Pinned successfully!");
                }else if (AdminFunction.pinCheck == 2){
                    JOptionPane.showMessageDialog(null, "Event already pinned!");
                }
                
            }
        });

        

        panel.add(pinButton);
        
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
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
        popupMenu = new javax.swing.JPopupMenu();
        ItemNotifications = new javax.swing.JMenuItem();
        ItemProfile = new javax.swing.JMenuItem();
        ItemLogout = new javax.swing.JMenuItem();
        layeredPane = new javax.swing.JLayeredPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        suggestionPanel = new javax.swing.JPanel();
        btnMenu = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        cityList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        descriptionFrame = new javax.swing.JInternalFrame();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        eventNameLabel = new javax.swing.JLabel();
        eventDateLabel = new javax.swing.JLabel();
        eventPlaceLabel = new javax.swing.JLabel();
        eventTimeLabel = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Description = new javax.swing.JTextArea();

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

        ItemNotifications.setText("NOTIFICATIONS");
        ItemNotifications.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemNotificationsActionPerformed(evt);
            }
        });
        popupMenu.add(ItemNotifications);

        ItemProfile.setText("PROFILE");
        ItemProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemProfileActionPerformed(evt);
            }
        });
        popupMenu.add(ItemProfile);

        ItemLogout.setText("LOGOUT");
        ItemLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemLogoutActionPerformed(evt);
            }
        });
        popupMenu.add(ItemLogout);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        layeredPane.setPreferredSize(new java.awt.Dimension(800, 500));

        jPanel5.setBackground(new java.awt.Color(153, 153, 255));
        jPanel5.setMinimumSize(new java.awt.Dimension(800, 500));
        jPanel5.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel5.setLayout(null);

        jPanel1.setBackground(new java.awt.Color(102, 51, 255));

        searchField.setText("Search");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchFieldFocusLost(evt);
            }
        });
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        suggestionPanel.setLayout(new javax.swing.OverlayLayout(suggestionPanel));

        btnMenu.setBackground(new java.awt.Color(102, 51, 255));
        btnMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/menu (2).png"))); // NOI18N
        btnMenu.setBorder(null);
        btnMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon1/misor.png"))); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(suggestionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(suggestionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel1);
        jPanel1.setBounds(0, 0, 800, 110);

        jPanel2.setBackground(new java.awt.Color(153, 153, 255));

        jPanel6.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 114, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        cityList.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        cityList.setForeground(new java.awt.Color(0, 51, 51));
        cityList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        cityList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                cityListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(cityList);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(434, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel9);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Municipalities");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );

        jPanel5.add(jPanel2);
        jPanel2.setBounds(10, 120, 240, 380);

        jPanel4.setBackground(new java.awt.Color(153, 153, 255));
        jPanel4.setLayout(null);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName(""); // NOI18N
        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
        });
        jScrollPane2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                addEventPerformed(evt);
            }
        });
        jScrollPane2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane2KeyPressed(evt);
            }
        });
        jScrollPane2.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                jScrollPane2VetoableChange(evt);
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
        eventTable.setCellSelectionEnabled(true);
        eventTable.setFocusTraversalPolicyProvider(true);
        eventTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        eventTable.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                eventTableComponentAdded(evt);
            }
        });
        eventTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eventTableMouseClicked(evt);
            }
        });
        eventTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                eventTablePropertyChange(evt);
            }
        });
        eventTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                eventTableKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(eventTable);

        jPanel4.add(jScrollPane2);
        jScrollPane2.setBounds(6, 60, 538, 320);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Events");
        jPanel4.add(jLabel2);
        jLabel2.setBounds(6, 11, 538, 35);

        jPanel5.add(jPanel4);
        jPanel4.setBounds(250, 120, 550, 380);

        descriptionFrame.setClosable(true);
        descriptionFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        descriptionFrame.setPreferredSize(new java.awt.Dimension(650, 370));
        descriptionFrame.setVisible(true);
        descriptionFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                descriptionFrameInternalFrameClosing(evt);
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
        descriptionFrame.getContentPane().setLayout(new javax.swing.OverlayLayout(descriptionFrame.getContentPane()));

        Description.setColumns(20);
        Description.setRows(5);
        jScrollPane5.setViewportView(Description);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(eventPlaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eventNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 286, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(eventDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(eventTimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jScrollPane5)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eventDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eventPlaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        descriptionFrame.getContentPane().add(jPanel12);

        layeredPane.setLayer(jPanel5, javax.swing.JLayeredPane.DEFAULT_LAYER);
        layeredPane.setLayer(descriptionFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layeredPaneLayout = new javax.swing.GroupLayout(layeredPane);
        layeredPane.setLayout(layeredPaneLayout);
        layeredPaneLayout.setHorizontalGroup(
            layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layeredPaneLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layeredPaneLayout.createSequentialGroup()
                    .addGap(81, 81, 81)
                    .addComponent(descriptionFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(81, Short.MAX_VALUE)))
        );
        layeredPaneLayout.setVerticalGroup(
            layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layeredPaneLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layeredPaneLayout.createSequentialGroup()
                    .addGap(71, 71, 71)
                    .addComponent(descriptionFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(71, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        searchField.addKeyListener(new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
        handleSearchInput(); // Call the method whenever the user types in the search field
    }
});

    }//GEN-LAST:event_searchFieldActionPerformed

    private void ItemProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemProfileActionPerformed

        Profile profileWindow = new Profile();
        profileWindow.setLocationRelativeTo(null); // Center the window on the screen
        profileWindow.setVisible(true);
        dispose();// Show the Login window
    }//GEN-LAST:event_ItemProfileActionPerformed

    private void ItemLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "SEE YAH!!");
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setLocationRelativeTo(null);
            loginWindow.setVisible(true);
            this.dispose(); // 
        }
    }//GEN-LAST:event_ItemLogoutActionPerformed

    private void btnMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuActionPerformed
                            
        popupMenu.show(btnMenu, (btnMenu.getWidth() - popupMenu.getPreferredSize().width) / 2 - 19, btnMenu.getHeight() + 5);


    }//GEN-LAST:event_btnMenuActionPerformed

    private void jScrollPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseClicked

    }//GEN-LAST:event_jScrollPane2MouseClicked

    private void jScrollPane2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane2KeyPressed
        
    }//GEN-LAST:event_jScrollPane2KeyPressed

    private void eventTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eventTableMouseClicked

        
    }//GEN-LAST:event_eventTableMouseClicked

    private void eventTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_eventTablePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_eventTablePropertyChange

    private void eventTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_eventTableKeyPressed
        
    }//GEN-LAST:event_eventTableKeyPressed

    private void jScrollPane2VetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_jScrollPane2VetoableChange
      
    }//GEN-LAST:event_jScrollPane2VetoableChange

    private void eventTableComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_eventTableComponentAdded

        
    }//GEN-LAST:event_eventTableComponentAdded

    private void descriptionFrameInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_descriptionFrameInternalFrameClosing
        descriptionFrame.setVisible(false);
        layeredPane.moveToBack(descriptionFrame);
    }//GEN-LAST:event_descriptionFrameInternalFrameClosing

    private void ItemNotificationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemNotificationsActionPerformed
         NotificationFrame notificationFrame = new NotificationFrame(this);
                notificationFrame.toFront();
                notificationFrame.requestFocusInWindow();
                notificationFrame.setVisible(true);
                
    }//GEN-LAST:event_ItemNotificationsActionPerformed

    private void searchFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFieldFocusGained
        searchField.setText("");
    }//GEN-LAST:event_searchFieldFocusGained

    private void searchFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFieldFocusLost
        searchField.setText("Search");
    }//GEN-LAST:event_searchFieldFocusLost

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        if (AdminFunction.counter == 1 && AdminFunction.totalCounts != 0) {
        NotificationCountDialog countDialog = new NotificationCountDialog(this);
                countDialog.setVisible(true);
                AdminFunction.counter += 1;
                System.out.println(AdminFunction.counter);
        }
        
    }//GEN-LAST:event_formWindowOpened

    /**

    
    
    public static void main(String args[]) {
      
    }**/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Description;
    private javax.swing.JMenuItem ItemLogout;
    private javax.swing.JMenuItem ItemNotifications;
    private javax.swing.JMenuItem ItemProfile;
    private javax.swing.JButton btnMenu;
    private javax.swing.JList<String> cityList;
    private javax.swing.JInternalFrame descriptionFrame;
    private javax.swing.JLabel eventDateLabel;
    private javax.swing.JLabel eventNameLabel;
    private javax.swing.JLabel eventPlaceLabel;
    private javax.swing.JTable eventTable;
    private javax.swing.JLabel eventTimeLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLayeredPane layeredPane;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel suggestionPanel;
    // End of variables declaration//GEN-END:variables
}
