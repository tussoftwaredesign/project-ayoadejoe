package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import interfaces.TopicsInterface;
import logic.DatabaseManager;
import logic.Topic;
import javax.swing.JTextPane;

public class MainFrame extends JFrame implements ActionListener{

	private JPanel contentPane;
	private static int resourcesLoaded = 0;
	private static DatabaseManager dataManager;
	private JPanel subtopicPanel = new JPanel();
	private int rows =1, rowsub=2;
	private JPanel subsubTopicPanel = new JPanel();
	private String subHeader = "Sub-Topics", subsubHeader = "Sections";
	private TitledBorder subTitleText = new TitledBorder(null, subHeader, TitledBorder.LEADING, TitledBorder.TOP, null, null);
	private TitledBorder subsubTitleText = new TitledBorder(null, subsubHeader, TitledBorder.LEADING, TitledBorder.TOP, null, null);
	private JTextPane textPane = new JTextPane();
	
	public static void main(String[] args) {

			TopicsInterface topicsInterface  = new TopicsInterface() {
				@Override
				public void onComplete(boolean done) {
					System.out.println("Done: "+done);
					++resourcesLoaded;
					
					if(resourcesLoaded >=3)	//all resources loaded
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								MainFrame frame = new MainFrame();
								frame.setVisible(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});	
				}
			};
			
		try {	
			dataManager = new DatabaseManager(topicsInterface);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "One or more"
					+ " Database file is missing!");		
			e.printStackTrace();
		} catch (IllegalArgumentException e) {	//if the database structure has issues, this would be thrown but program would run anyway
			JOptionPane.showMessageDialog(null, e.getMessage());		
			e.printStackTrace();
		}
	}
	
	ArrayList<JButton> dataTypeButtons = new ArrayList<>();


	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 923, 470);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setPreferredSize(new Dimension(200, 130));
		contentPane.add(splitPane, BorderLayout.SOUTH);
		
		JPanel panelButtons = new JPanel();
		panelButtons.setBackground(new Color(255, 255, 240));
		panelButtons.setBorder(new TitledBorder(null, "Topics", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(panelButtons);
		FlowLayout fl_panelButtons = new FlowLayout(FlowLayout.LEFT, 5, 5);
		panelButtons.setLayout(fl_panelButtons);
		
		JButton btnDatatype = new JButton("Data Types");
		btnDatatype.setBackground(new Color(255, 248, 220));
		btnDatatype.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnDatatype.addActionListener(this);
		btnDatatype.setActionCommand("Data Types");
		btnDatatype.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnDatatype);
		
		JButton btnControl = new JButton("Control");
		btnControl.setBackground(new Color(255, 248, 220));
		btnControl.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnControl.addActionListener(this);
		btnControl.setActionCommand("Control");
		btnControl.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnControl);
		
		
		JButton btnStrings = new JButton("Strings");
		btnStrings.setActionCommand("Strings");
		btnStrings.setBackground(new Color(255, 248, 220));
		btnStrings.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnStrings.addActionListener(this);
		btnStrings.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnStrings);
		
		JButton btnStrgBuilder = new JButton("StringBuilders");
		btnStrgBuilder.setActionCommand("StringBuilders");
		btnStrgBuilder.setBackground(new Color(255, 248, 220));
		btnStrgBuilder.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnStrgBuilder.addActionListener(this);
		btnStrgBuilder.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnStrgBuilder);
		
		JButton btnArrayList = new JButton("ArrayLists");
		btnArrayList.setActionCommand("ArrayLists");
		btnArrayList.setBackground(new Color(255, 248, 220));
		btnArrayList.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnArrayList.addActionListener(this);
		btnArrayList.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnArrayList);
		
		JButton btnClasses = new JButton("Classes");
		btnClasses.setActionCommand("Classes");
		btnClasses.setBackground(new Color(255, 248, 220));
		btnClasses.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnClasses.addActionListener(this);
		btnClasses.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnClasses);
		
		JButton btnMethods = new JButton("Method");
		btnMethods.setActionCommand("Method");
		btnMethods.setBackground(new Color(255, 248, 220));
		btnMethods.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnMethods.addActionListener(this);
		btnMethods.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnMethods);
		
		JButton btnInheritance = new JButton("Inheritance");
		btnInheritance.setActionCommand("Inheritance");
		btnInheritance.setBackground(new Color(255, 248, 220));
		btnInheritance.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnInheritance.addActionListener(this);
		btnInheritance.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnInheritance);
		
		JScrollPane scrollSubs = new JScrollPane();
		subtopicPanel.setBackground(new Color(255, 255, 240));
		subtopicPanel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), subTitleText));
		scrollSubs.setViewportView(subtopicPanel);
		subtopicPanel.setPreferredSize(new Dimension(250, 100));
		//subtopicPanel.setLayout(new BoxLayout(subtopicPanel, BoxLayout.Y_AXIS));
		subtopicPanel.setMinimumSize(new Dimension(500, 100));
		 
		JPanel notes = new JPanel();
		notes.setLayout(new FlowLayout());
        notes.add(new JLabel("Notes Panel"));
        notes.setMinimumSize(new Dimension(50, 100));
        
		splitPane.setLeftComponent(scrollPane);
		splitPane.setRightComponent(notes);
		
		splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(150); 
		
		JPanel centrePanel = new JPanel();
		centrePanel.setLayout(new BorderLayout(0, 0));
		
		//subsubTopicPanel.setLayout(new BoxLayout(subsubTopicPanel, BoxLayout.X_AXIS));
		//subsubTopicPanel.setMinimumSize(new Dimension(50, 400));
		
		JScrollPane subsubScroll = new JScrollPane();
		subsubTopicPanel.setPreferredSize(new Dimension(200, 50));
		subsubTopicPanel.setBorder(subsubTitleText);
		subsubScroll.setViewportView(subsubTopicPanel);
		subsubTopicPanel.setLayout(new GridLayout(rowsub, 0, 0, 0));
		//subsubScroll.setMinimumSize(new Dimension(50, 400));
		
		centrePanel.add(subsubScroll, BorderLayout.WEST);
		
		contentPane.add(centrePanel, BorderLayout.CENTER);
		
		JPanel detailPanel = new JPanel();
		centrePanel.add(detailPanel, BorderLayout.NORTH);
		textPane.setBorder(new TitledBorder(null, "Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		textPane.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
		textPane.setPreferredSize(new Dimension(50, 100));
		
		centrePanel.add(textPane, BorderLayout.SOUTH);
		contentPane.add(scrollSubs, BorderLayout.WEST);
		System.out.println("Working Now!");
		
		SwingUtilities.invokeLater(() -> {
            int leftPreferred = subtopicPanel.getPreferredSize().width;
            int rightPreferred = notes.getPreferredSize().width;
            int totalWidth = leftPreferred + rightPreferred;

            // Dynamically set the divider location
            splitPane.setDividerLocation((double) leftPreferred / totalWidth);
        });
	}


	
	@Override
	public void actionPerformed(ActionEvent action) {
	    // Clear the subtopic panel before populating it
	    subtopicPanel.removeAll();
	    
	    // Determine the main topic and process subtopics
	    String actionCommand = action.getActionCommand();
	    processMainTopic(actionCommand);

	    // Revalidate and repaint the subtopic panel
	   
	    subTitleText.setTitle(actionCommand);
	    subtopicPanel.setLayout(new GridLayout(rows, 0, 0, 0));
	    subtopicPanel.revalidate();
	    subtopicPanel.repaint();
	    
	}
	
	private void processMainTopic(String mainTopic) {
	    // Fetch the topic data
	    Topic topic = dataManager.getSubTopics(mainTopic);
	    
	    if(topic != null) {
	    	textPane.setText(topic.getContent());
	    if (topic.getSubtopics().size() > 0) {
	        List<Topic> subtopics = topic.getSubtopics();
	        rows = subtopics.size();
	        for (Topic subtopic : subtopics) {
	        	System.out.println(subtopic.getTitle());
		        JButton subtopicButton = createSubtopicButton(subtopic.getTitle());
		        subtopicButton.setActionCommand(subtopic.getTitle());
		        
		        subtopicButton.addActionListener((e)-> {
		        	System.out.println(e.getActionCommand()+" clicked");
		        	subsubTitleText.setTitle(e.getActionCommand());
		        	processSubTopics(e.getActionCommand(), subtopic);
		        });
		        subtopicPanel.add(subtopicButton);
	        	
	        }
	    }
	    }
	}
	
	
	private void processSubTopics(String actionCommand, Topic subtopic) {
		subsubTopicPanel.removeAll();
		textPane.setText(subtopic.getContent());
        List<Topic> subsubTopicArray = subtopic.getSubtopics();
        if(subsubTopicArray.size()>0) {
        	rowsub = subsubTopicArray.size();
        	subsubTopicPanel.setLayout(new GridLayout(rowsub, 0, 0, 0));
        	for(Topic subsub:subsubTopicArray) {
	        	System.out.println(subsub.getTitle());
        		JButton subsubButton = new JButton(subsub.getTitle());
        		subsubButton.addActionListener((e)-> {
		        	System.out.println(e.getActionCommand()+" clicked");
		        	textPane.setText(subsub.getContent());
		        });
        		subsubTopicPanel.add(subsubButton);
        	}
        }
        
        subsubTopicPanel.revalidate();
	    subsubTopicPanel.repaint();
	}



	private JButton createSubtopicButton(String title) {
	    JButton button = new JButton(title);
	    button.setBackground(new Color(255, 248, 220));
	    button.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
	    button.setFont(new Font("Cambria", Font.BOLD, 18));
	    return button;
	}


}
