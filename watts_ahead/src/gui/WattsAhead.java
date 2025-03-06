package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import com.toedter.calendar.JDateChooser;

import watts.ClientConnect;



public class WattsAhead extends JFrame{

	private String urlString = null; //"http://eucalyptus.iq-joy.com/joseph1/historical_power.php";
    private String startDate = "2024-10-01 00:00:00";
    private String endDate = "2024-11-30 23:59:59"; // Fixed date issue
    private String outputFilePath = null; // Local file path
    
    private JDateChooser startDateChooser = new JDateChooser();
    private JDateChooser endDateChooser = new JDateChooser();
    
    private JProgressBar progressBar = new JProgressBar();

    private final JPanel progressPanel = new JPanel();
    private final JTextArea logTextArea = new JTextArea();
    private final JSplitPane splitPane = new JSplitPane();
    private final JPanel panelFuse = new JPanel();
    private final JButton btnFuse = new JButton("<<FUSE FEATURES>>");
    private final JPanel panel_1 = new JPanel();
    private final JCheckBox chckbxNewCheckBox = new JCheckBox("Meteostat");
    private final JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Government");
    private final JButton btnNewButton_1 = new JButton("Export CSV");
    private final JLabel lblNewLabel_2 = new JLabel(" | ");
    private final JPanel panelDate = new JPanel();
    private final JTextField endpointText = new JTextField();
    private final JPanel panel = new JPanel();
    private final JButton btnFetchData = new JButton("FETCH DATA>>");
    
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedTime = Calendar.getInstance();
    private Calendar selectedEndDate = Calendar.getInstance();
    private Calendar selectedEndTime = Calendar.getInstance();
    
    private DataTable historicalData = new DataTable();
    private ClientConnect clientConnect;
    private String startDateTime, endDateTime = null;
    private final JLabel lblNewLabel = new JLabel("Start Time & Date");
    private final JLabel lblEndtimeDate = new JLabel("End Time & Date");
    
    public WattsAhead() {
    	setTitle("FEATURE USER [<--->]");
    	endpointText.setFont(new Font("Lucida Grande", Font.ITALIC, 12));
    	endpointText.setText("http://eucalyptus.iq-joy.com/joseph1/historical_power.php");
    	endpointText.setColumns(10);
    	
    	JToolBar toolBar = new JToolBar();
    	getContentPane().add(toolBar, BorderLayout.NORTH);
    	panelDate.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
    	
    	toolBar.add(panelDate);
    	panelDate.setLayout(new GridLayout(0, 2, 0, 0));
    	
    	
    	panelDate.setPreferredSize(new Dimension(700, 10));
    	
    	startDateChooser.setDate(new Date()); // Set default date to today

    	// Create JSpinner for Time Selection
        SpinnerDateModel timeModel = new SpinnerDateModel();
        
        panelDate.add(startDateChooser);
        
        JSpinner timeSpinner = new JSpinner(timeModel);
        startDateChooser.add(timeSpinner, BorderLayout.WEST);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss"); // Time format
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date()); // Default to current time
        lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        startDateChooser.add(lblNewLabel, BorderLayout.SOUTH);
        
        
        endDateChooser.setDate(new Date());
        panelDate.add(endDateChooser);
     // Create JSpinner for Time Selection
        SpinnerDateModel timeEndModel = new SpinnerDateModel();
        
        JSpinner timeEndSpinner = new JSpinner(timeEndModel);
        endDateChooser.add(timeEndSpinner, BorderLayout.WEST);
        JSpinner.DateEditor timeEndEditor = new JSpinner.DateEditor(timeEndSpinner, "HH:mm:ss"); // Time format
        timeEndSpinner.setEditor(timeEndEditor);
        timeEndSpinner.setValue(new Date()); // Default to current time
        lblEndtimeDate.setHorizontalAlignment(SwingConstants.CENTER);
        lblEndtimeDate.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
        
        endDateChooser.add(lblEndtimeDate, BorderLayout.SOUTH);
       
        
        toolBar.addSeparator();
        
        toolBar.add(panel);
        panel.setLayout(new GridLayout(2, 2, 0, 0));
        
        btnFetchData.addActionListener(e -> {
            // 1. Merge Date from JDateChooser and Time from JSpinner
            // StartDate
            selectedDate.setTime(startDateChooser.getDate());
            selectedTime.setTime((Date) timeSpinner.getValue());
            selectedDate.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY));
            selectedDate.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE));
            selectedDate.set(Calendar.SECOND, selectedTime.get(Calendar.SECOND));

            // EndDate
            selectedEndDate.setTime(endDateChooser.getDate());
            selectedEndTime.setTime((Date) timeEndSpinner.getValue());
            selectedEndDate.set(Calendar.HOUR_OF_DAY, selectedEndTime.get(Calendar.HOUR_OF_DAY));
            selectedEndDate.set(Calendar.MINUTE, selectedEndTime.get(Calendar.MINUTE));
            selectedEndDate.set(Calendar.SECOND, selectedEndTime.get(Calendar.SECOND));

            // Validate date-time selection
            if (!validateDateTimeSelection(selectedDate, selectedEndDate)) {
                return; // Stop if validation fails
            }
            
            urlString = endpointText.getText().trim();
            // Validate URL
            
            if (urlString != null && !isValidURL(urlString)) {
                JOptionPane.showMessageDialog(this, "Invalid URL! Please enter a valid URL starting with http:// or https://", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop execution if invalid
            }

            // Format the selected date-time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startDateTime = sdf.format(selectedDate.getTime());
            endDateTime = sdf.format(selectedEndDate.getTime());

            System.out.println("Start Date: " + startDateTime);
            System.out.println("End Date: " + endDateTime);

            logTextArea.setText("Start Date Selected: " + startDateTime + "\nEnd Date Selected: " + endDateTime);
            outputFilePath = startDateTime+"_"+endDateTime+".csv";
            // Proceed with data fetch
            logTextArea.append("\nStarting Download...");
            
            // Start ClientConnect with a callback that triggers createTable()
            new ClientConnect(urlString, startDate, endDate, outputFilePath, logTextArea, progressBar, () -> {
                // This runs only after the download is complete
                SwingUtilities.invokeLater(() -> {
                	logTextArea.append("\nLoading Data into Table...");
                    DataTable csvPanel = new DataTable();
                    csvPanel.createTable(outputFilePath);
                    splitPane.setLeftComponent(csvPanel); // Update UI
                });
            }).execute();
        });

        
        btnFetchData.setHorizontalAlignment(SwingConstants.RIGHT);
        btnFetchData.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        panel.add(btnFetchData);
        panel.add(btnFuse);
        toolBar.addSeparator();
    	panelFuse.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
    	
    	toolBar.add(panelFuse);
    	panelFuse.setLayout(new BorderLayout(0, 0));
    	
    	panelFuse.add(panel_1, BorderLayout.SOUTH);
    	
    	panel_1.add(btnNewButton_1);
    	
    	panel_1.add(lblNewLabel_2);
    	
    	panel_1.add(chckbxNewCheckBox);
    	
    	panel_1.add(chckbxNewCheckBox_1);
    	
    	panelFuse.add(endpointText, BorderLayout.NORTH);
    	
    	getContentPane().add(progressPanel, BorderLayout.SOUTH);
    	progressPanel.setLayout(new BorderLayout(0, 0));
    	progressPanel.setPreferredSize(new Dimension(200, 100));
    	progressBar.setStringPainted(true);
    	progressPanel.add(progressBar, BorderLayout.NORTH);
    	
    	JScrollPane scrollLog = new JScrollPane();
    	scrollLog.setViewportView(logTextArea);
    	progressPanel.add(scrollLog, BorderLayout.CENTER);
    	splitPane.setLeftComponent(historicalData);
    	JPanel pan = new JPanel();
    	pan.setPreferredSize(new Dimension(300, 200));
    	splitPane.setRightComponent(pan);
    	splitPane.setDividerSize(2);
    	
    	getContentPane().add(splitPane, BorderLayout.CENTER);
    	splitPane.setDividerLocation(750);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLocationRelativeTo(this.getParent());
    	setSize(1200, 750);
    	setVisible(true);
        
    }
    
    private boolean validateDateTimeSelection(Calendar selectedDate, Calendar selectedEndDate) {
        if (selectedDate == null || selectedEndDate == null) {
            JOptionPane.showMessageDialog(this, "Both start and end date-time must be selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Normalize both dates by removing milliseconds
        selectedDate.set(Calendar.MILLISECOND, 0);
        selectedEndDate.set(Calendar.MILLISECOND, 0);

        long startMillis = selectedDate.getTimeInMillis();
        long endMillis = selectedEndDate.getTimeInMillis();

        if (endMillis == startMillis) { // Directly compare timestamps
            JOptionPane.showMessageDialog(this, "Start and End date-time cannot be the same!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (endMillis < startMillis) { // Ensure end is strictly after start
            JOptionPane.showMessageDialog(this, "End date-time must be after start date-time!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true; // Validation passed
    }


  

    private boolean isValidURL(String url) {
        String urlRegex = "^(https?|ftp)://[\\w.-]+(?:\\.[\\w.-]+)+[/#?]?.*$";
        Pattern pattern = Pattern.compile(urlRegex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    

    public static void main(String[] args) {
    	SwingUtilities.invokeLater(()->{
    		new WattsAhead(); // Instantiate class to execute request
    	});
        
    }
}
