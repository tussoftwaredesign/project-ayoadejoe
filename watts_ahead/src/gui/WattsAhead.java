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

import connections.ClientConnect;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;



public class WattsAhead extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
    private final JCheckBox chckbxNewCheckBox = new JCheckBox("Use Meteostat Data");
    private final JButton btnNewButton_1 = new JButton("Export CSV");
    private final JPanel panelDate = new JPanel();
    private final JTextField endpointText = new JTextField();
    private final JPanel panel = new JPanel();
    private final JButton btnFetchData = new JButton("Acquire Energy Data");
    
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedTime = Calendar.getInstance();
    private Calendar selectedEndDate = Calendar.getInstance();
    private Calendar selectedEndTime = Calendar.getInstance();
    
    private DataTable historicalData = new DataTable();
    private String startDateTime, endDateTime = null;
    private final JLabel lblNewLabel = new JLabel("Start Time & Date");
    private final JLabel lblEndtimeDate = new JLabel("End Time & Date");
    private final JPanel processPanel = new JPanel();
    private final JPanel cleaningPanel = new JPanel();
    private final JButton btnNewButton = new JButton("Clean Missing Instances");
    private final JButton btnNewButton_2 = new JButton("Clean Missing Features");
    private final JLabel lblNewLabel_1 = new JLabel("Connect to Server ");
    private final JPanel weatherPanel = new JPanel();
    private final JPanel ecoPanel = new JPanel();
    private final JTextField txtEnterMeteostatUrl = new JTextField();
    private final JTextField txtEnterApiKey = new JTextField();
    private final JDateChooser startDateChooser_1 = new JDateChooser();
    private final JDateChooser endDateChooser_1 = new JDateChooser();
    private final JLabel lblNewLabel_4 = new JLabel("Enter Start Date:");
    private final JLabel lblNewLabel_5 = new JLabel("Enter End Date:");
    private final JProgressBar weatherProgress = new JProgressBar();
    private final JButton btnNewButton_3 = new JButton("Acquire Weather Data");
    private final JLabel energyAvailable = new JLabel("No energy data ");
    private final JLabel lblNewLabel_7 = new JLabel("Economic Data (No Data)");
    private final JTextField txtEnterStatistaUrl = new JTextField();
    private final JTextField textField = new JTextField();
    private final JDateChooser startDateChooser_1_1 = new JDateChooser();
    private final JLabel lblNewLabel_4_1 = new JLabel("Enter Start Date:");
    private final JDateChooser endDateChooser_1_1 = new JDateChooser();
    private final JLabel lblNewLabel_5_1 = new JLabel("Enter End Date:");
    private final JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Use Economic Data");
    private final JButton btnNewButton_3_1 = new JButton("Acquire Economic Data");
    private final JProgressBar progressBar_2 = new JProgressBar();
    private final JLabel lblNewLabel_8 = new JLabel("Clean Energy Data - Preview -->");
    private final JTextArea textArea = new JTextArea();
    
    public WattsAhead() {
    	txtEnterApiKey.setHorizontalAlignment(SwingConstants.RIGHT);
    	txtEnterApiKey.setText("Enter API Key");
    	txtEnterApiKey.setColumns(10);
    	txtEnterMeteostatUrl.setHorizontalAlignment(SwingConstants.RIGHT);
    	txtEnterMeteostatUrl.setText("Enter Meteostat URL");
    	txtEnterMeteostatUrl.setColumns(10);
    	setTitle("FEATURE USER [<--->]");
    	
    	JToolBar toolBar = new JToolBar();
    	toolBar.setPreferredSize(new Dimension(13, 50));
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
        panel.setLayout(new GridLayout(1, 2, 0, 0));
        panel.add(btnFuse);
        toolBar.addSeparator();
    	panelFuse.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
    	
    	toolBar.add(panelFuse);
    	panelFuse.setLayout(new GridLayout(0, 1, 0, 0));
    	
    	panelFuse.add(panel_1);
    	panel_1.setLayout(new GridLayout(0, 1, 0, 0));
    	
    	panel_1.add(btnNewButton_1);
    	
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
    	processPanel.setBorder(new LineBorder(UIManager.getColor("Button.background"), 6));
    	
    	getContentPane().add(processPanel, BorderLayout.WEST);
    	processPanel.setLayout(new GridLayout(0, 1, 10, 10));
    	cleaningPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Energy Data", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
    	
    	processPanel.add(cleaningPanel);
    	GridBagLayout gbl_cleaningPanel = new GridBagLayout();
    	gbl_cleaningPanel.columnWidths = new int[]{95, 95, 79, 79, 0};
    	gbl_cleaningPanel.rowHeights = new int[]{26, 0, 0, 34, 27, 27, 0, 0, 0};
    	gbl_cleaningPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
    	gbl_cleaningPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
    	cleaningPanel.setLayout(gbl_cleaningPanel);
    	
    	GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    	gbc_lblNewLabel_1.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_lblNewLabel_1.gridwidth = 4;
    	gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
    	gbc_lblNewLabel_1.gridx = 0;
    	gbc_lblNewLabel_1.gridy = 0;
    	lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
    	cleaningPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
    	btnNewButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    		}
    	});
    	GridBagConstraints gbc_endpointText = new GridBagConstraints();
    	gbc_endpointText.fill = GridBagConstraints.HORIZONTAL;
    	gbc_endpointText.gridwidth = 4;
    	gbc_endpointText.insets = new Insets(0, 0, 5, 0);
    	gbc_endpointText.gridx = 0;
    	gbc_endpointText.gridy = 1;
    	cleaningPanel.add(endpointText, gbc_endpointText);
    	endpointText.setFont(new Font("Lucida Grande", Font.ITALIC, 12));
    	endpointText.setText("http://eucalyptus.iq-joy.com/joseph1/historical_power.php");
    	endpointText.setColumns(10);
    	GridBagConstraints gbc_btnFetchData = new GridBagConstraints();
    	gbc_btnFetchData.anchor = GridBagConstraints.EAST;
    	gbc_btnFetchData.gridwidth = 2;
    	gbc_btnFetchData.insets = new Insets(0, 0, 5, 5);
    	gbc_btnFetchData.gridx = 0;
    	gbc_btnFetchData.gridy = 2;
    	cleaningPanel.add(btnFetchData, gbc_btnFetchData);
    	
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
    	    outputFilePath = "Requested.csv";
    	    // Proceed with data fetch
    	    logTextArea.append("\nStarting Download...");
    	    
    	    // Start ClientConnect with a callback that triggers createTable()
    	    new ClientConnect(urlString, startDate, endDate, outputFilePath, logTextArea, energyAvailable, progressBar, () -> {
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
    	
    	GridBagConstraints gbc_energyAvailable = new GridBagConstraints();
    	gbc_energyAvailable.anchor = GridBagConstraints.WEST;
    	gbc_energyAvailable.gridwidth = 2;
    	gbc_energyAvailable.insets = new Insets(0, 0, 5, 0);
    	gbc_energyAvailable.gridx = 2;
    	gbc_energyAvailable.gridy = 2;
    	cleaningPanel.add(energyAvailable, gbc_energyAvailable);
    	
    	GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
    	gbc_lblNewLabel_8.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_lblNewLabel_8.gridwidth = 4;
    	gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 0);
    	gbc_lblNewLabel_8.gridx = 0;
    	gbc_lblNewLabel_8.gridy = 3;
    	cleaningPanel.add(lblNewLabel_8, gbc_lblNewLabel_8);
    	
    	GridBagConstraints gbc_textArea = new GridBagConstraints();
    	gbc_textArea.gridwidth = 4;
    	gbc_textArea.gridheight = 3;
    	gbc_textArea.insets = new Insets(0, 0, 5, 5);
    	gbc_textArea.fill = GridBagConstraints.BOTH;
    	gbc_textArea.gridx = 0;
    	gbc_textArea.gridy = 4;
    	cleaningPanel.add(textArea, gbc_textArea);
    	btnNewButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
    	
    	GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
    	gbc_btnNewButton.gridwidth = 2;
    	gbc_btnNewButton.fill = GridBagConstraints.BOTH;
    	gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
    	gbc_btnNewButton.gridx = 0;
    	gbc_btnNewButton.gridy = 7;
    	cleaningPanel.add(btnNewButton, gbc_btnNewButton);
    	
    	GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
    	gbc_btnNewButton_2.gridwidth = 2;
    	gbc_btnNewButton_2.fill = GridBagConstraints.BOTH;
    	gbc_btnNewButton_2.gridx = 2;
    	gbc_btnNewButton_2.gridy = 7;
    	cleaningPanel.add(btnNewButton_2, gbc_btnNewButton_2);
    	weatherPanel.setBorder(new TitledBorder(null, "Fill Weather (No Data)", TitledBorder.LEFT, TitledBorder.TOP, null, null));
    	
    	processPanel.add(weatherPanel);
    	GridBagLayout gbl_weatherPanel = new GridBagLayout();
    	gbl_weatherPanel.columnWidths = new int[]{145, 0, 0};
    	gbl_weatherPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 27, 0};
    	gbl_weatherPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    	gbl_weatherPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    	weatherPanel.setLayout(gbl_weatherPanel);
    	
    	GridBagConstraints gbc_txtEnterMeteostatUrl = new GridBagConstraints();
    	gbc_txtEnterMeteostatUrl.insets = new Insets(0, 0, 5, 0);
    	gbc_txtEnterMeteostatUrl.gridwidth = 2;
    	gbc_txtEnterMeteostatUrl.fill = GridBagConstraints.HORIZONTAL;
    	gbc_txtEnterMeteostatUrl.gridx = 0;
    	gbc_txtEnterMeteostatUrl.gridy = 0;
    	weatherPanel.add(txtEnterMeteostatUrl, gbc_txtEnterMeteostatUrl);
    	
    	GridBagConstraints gbc_txtEnterApiKey = new GridBagConstraints();
    	gbc_txtEnterApiKey.insets = new Insets(0, 0, 5, 0);
    	gbc_txtEnterApiKey.gridwidth = 2;
    	gbc_txtEnterApiKey.fill = GridBagConstraints.HORIZONTAL;
    	gbc_txtEnterApiKey.gridx = 0;
    	gbc_txtEnterApiKey.gridy = 1;
    	weatherPanel.add(txtEnterApiKey, gbc_txtEnterApiKey);
    	
    	GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
    	gbc_lblNewLabel_4.anchor = GridBagConstraints.NORTHWEST;
    	gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
    	gbc_lblNewLabel_4.gridx = 0;
    	gbc_lblNewLabel_4.gridy = 2;
    	weatherPanel.add(lblNewLabel_4, gbc_lblNewLabel_4);
    	
    	GridBagConstraints gbc_startDateChooser_1 = new GridBagConstraints();
    	gbc_startDateChooser_1.insets = new Insets(0, 0, 5, 0);
    	gbc_startDateChooser_1.gridwidth = 2;
    	gbc_startDateChooser_1.fill = GridBagConstraints.BOTH;
    	gbc_startDateChooser_1.gridx = 0;
    	gbc_startDateChooser_1.gridy = 3;
    	weatherPanel.add(startDateChooser_1, gbc_startDateChooser_1);
    	
    	GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
    	gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
    	gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
    	gbc_lblNewLabel_5.gridx = 0;
    	gbc_lblNewLabel_5.gridy = 4;
    	weatherPanel.add(lblNewLabel_5, gbc_lblNewLabel_5);
    	
    	GridBagConstraints gbc_endDateChooser_1 = new GridBagConstraints();
    	gbc_endDateChooser_1.insets = new Insets(0, 0, 5, 0);
    	gbc_endDateChooser_1.gridwidth = 2;
    	gbc_endDateChooser_1.fill = GridBagConstraints.BOTH;
    	gbc_endDateChooser_1.gridx = 0;
    	gbc_endDateChooser_1.gridy = 5;
    	weatherPanel.add(endDateChooser_1, gbc_endDateChooser_1);
    	GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
    	gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
    	gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
    	gbc_chckbxNewCheckBox.gridx = 0;
    	gbc_chckbxNewCheckBox.gridy = 6;
    	weatherPanel.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
    	
    	GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
    	gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 0);
    	gbc_btnNewButton_3.gridx = 1;
    	gbc_btnNewButton_3.gridy = 6;
    	weatherPanel.add(btnNewButton_3, gbc_btnNewButton_3);
    	
    	GridBagConstraints gbc_weatherProgress = new GridBagConstraints();
    	gbc_weatherProgress.gridwidth = 2;
    	gbc_weatherProgress.fill = GridBagConstraints.BOTH;
    	gbc_weatherProgress.gridx = 0;
    	gbc_weatherProgress.gridy = 7;
    	weatherPanel.add(weatherProgress, gbc_weatherProgress);
    	
    	processPanel.add(ecoPanel);
    	GridBagLayout gbl_ecoPanel = new GridBagLayout();
    	gbl_ecoPanel.columnWidths = new int[]{0, 0, 0};
    	gbl_ecoPanel.rowHeights = new int[]{35, 25, 25, 28, 25, 30, 28, 23, 24, 0};
    	gbl_ecoPanel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
    	gbl_ecoPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    	ecoPanel.setLayout(gbl_ecoPanel);
    	
    	GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
    	gbc_lblNewLabel_7.fill = GridBagConstraints.VERTICAL;
    	gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 0);
    	gbc_lblNewLabel_7.gridwidth = 2;
    	gbc_lblNewLabel_7.gridx = 0;
    	gbc_lblNewLabel_7.gridy = 0;
    	lblNewLabel_7.setFont(new Font("Tahoma", Font.BOLD, 13));
    	ecoPanel.add(lblNewLabel_7, gbc_lblNewLabel_7);
    	
    	GridBagConstraints gbc_txtEnterStatistaUrl = new GridBagConstraints();
    	gbc_txtEnterStatistaUrl.insets = new Insets(0, 0, 5, 0);
    	gbc_txtEnterStatistaUrl.gridwidth = 2;
    	gbc_txtEnterStatistaUrl.fill = GridBagConstraints.HORIZONTAL;
    	gbc_txtEnterStatistaUrl.gridx = 0;
    	gbc_txtEnterStatistaUrl.gridy = 1;
    	txtEnterStatistaUrl.setText("Enter Statista URL");
    	txtEnterStatistaUrl.setHorizontalAlignment(SwingConstants.RIGHT);
    	txtEnterStatistaUrl.setColumns(10);
    	ecoPanel.add(txtEnterStatistaUrl, gbc_txtEnterStatistaUrl);
    	
    	GridBagConstraints gbc_textField = new GridBagConstraints();
    	gbc_textField.insets = new Insets(0, 0, 5, 0);
    	gbc_textField.gridwidth = 2;
    	gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    	gbc_textField.gridx = 0;
    	gbc_textField.gridy = 2;
    	textField.setText("Enter API Key");
    	textField.setHorizontalAlignment(SwingConstants.RIGHT);
    	textField.setColumns(10);
    	ecoPanel.add(textField, gbc_textField);
    	
    	GridBagConstraints gbc_lblNewLabel_4_1 = new GridBagConstraints();
    	gbc_lblNewLabel_4_1.anchor = GridBagConstraints.WEST;
    	gbc_lblNewLabel_4_1.insets = new Insets(0, 0, 5, 5);
    	gbc_lblNewLabel_4_1.gridx = 0;
    	gbc_lblNewLabel_4_1.gridy = 3;
    	ecoPanel.add(lblNewLabel_4_1, gbc_lblNewLabel_4_1);
    	
    	GridBagConstraints gbc_startDateChooser_1_1 = new GridBagConstraints();
    	gbc_startDateChooser_1_1.insets = new Insets(0, 0, 5, 0);
    	gbc_startDateChooser_1_1.gridwidth = 2;
    	gbc_startDateChooser_1_1.fill = GridBagConstraints.BOTH;
    	gbc_startDateChooser_1_1.gridx = 0;
    	gbc_startDateChooser_1_1.gridy = 4;
    	ecoPanel.add(startDateChooser_1_1, gbc_startDateChooser_1_1);
    	
    	GridBagConstraints gbc_lblNewLabel_5_1 = new GridBagConstraints();
    	gbc_lblNewLabel_5_1.anchor = GridBagConstraints.WEST;
    	gbc_lblNewLabel_5_1.insets = new Insets(0, 0, 5, 5);
    	gbc_lblNewLabel_5_1.gridx = 0;
    	gbc_lblNewLabel_5_1.gridy = 5;
    	ecoPanel.add(lblNewLabel_5_1, gbc_lblNewLabel_5_1);
    	
    	GridBagConstraints gbc_endDateChooser_1_1 = new GridBagConstraints();
    	gbc_endDateChooser_1_1.insets = new Insets(0, 0, 5, 0);
    	gbc_endDateChooser_1_1.gridwidth = 2;
    	gbc_endDateChooser_1_1.fill = GridBagConstraints.BOTH;
    	gbc_endDateChooser_1_1.gridx = 0;
    	gbc_endDateChooser_1_1.gridy = 6;
    	ecoPanel.add(endDateChooser_1_1, gbc_endDateChooser_1_1);
    	
    	GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
    	gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
    	gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 5);
    	gbc_chckbxNewCheckBox_1.gridx = 0;
    	gbc_chckbxNewCheckBox_1.gridy = 7;
    	ecoPanel.add(chckbxNewCheckBox_1, gbc_chckbxNewCheckBox_1);
    	
    	GridBagConstraints gbc_btnNewButton_3_1 = new GridBagConstraints();
    	gbc_btnNewButton_3_1.insets = new Insets(0, 0, 5, 0);
    	gbc_btnNewButton_3_1.gridx = 1;
    	gbc_btnNewButton_3_1.gridy = 7;
    	ecoPanel.add(btnNewButton_3_1, gbc_btnNewButton_3_1);
    	
    	GridBagConstraints gbc_progressBar_2 = new GridBagConstraints();
    	gbc_progressBar_2.fill = GridBagConstraints.BOTH;
    	gbc_progressBar_2.gridwidth = 2;
    	gbc_progressBar_2.gridx = 0;
    	gbc_progressBar_2.gridy = 8;
    	ecoPanel.add(progressBar_2, gbc_progressBar_2);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	//setLocationRelativeTo(null);
    	setSize(1200, 900);
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
