package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import interfaces.TopicsInterface;
import logic.DatabaseManager;
import logic.Topic;
import java.awt.GridLayout;
import javax.swing.border.TitledBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.LineBorder;

public class MainFrame extends JFrame implements ActionListener{

	private JPanel contentPane;
	private static int resourcesLoaded = 0;
	private static DatabaseManager dataManager;
	private JPanel subtopicPanel = new JPanel();
	
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
		
		JSplitPane splitPane = new JSplitPane();
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
		btnStrings.setBackground(new Color(255, 248, 220));
		btnStrings.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnStrings.addActionListener(this);
		btnStrings.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnStrings);
		
		JButton btnStrgBuilder = new JButton("StringBuilders");
		btnStrgBuilder.setBackground(new Color(255, 248, 220));
		btnStrgBuilder.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnStrgBuilder.addActionListener(this);
		btnStrgBuilder.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnStrgBuilder);
		
		JButton btnArrayList = new JButton("ArrayLists");
		btnArrayList.setBackground(new Color(255, 248, 220));
		btnArrayList.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnArrayList.addActionListener(this);
		btnArrayList.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnArrayList);
		
		JButton btnClasses = new JButton("Classes");
		btnClasses.setBackground(new Color(255, 248, 220));
		btnClasses.setBorder(new LineBorder(new Color(255, 153, 51), 2, true));
		btnClasses.addActionListener(this);
		btnClasses.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnClasses);
		
		JScrollPane scrollSubs = new JScrollPane();
		subtopicPanel.setBackground(new Color(245, 255, 250));
		subtopicPanel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Operations", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0))));
		scrollSubs.setViewportView(subtopicPanel);
		subtopicPanel.setPreferredSize(new Dimension(250, 100));
		subtopicPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		splitPane.setLeftComponent(scrollPane);
		splitPane.setRightComponent(scrollSubs);
		
		splitPane.setDividerLocation(500);
		System.out.println("Working Now!");
	}


	
	@Override
	public void actionPerformed(ActionEvent action) {
		
		switch(action.getActionCommand()) {
			case "Data Types":
				subtopicPanel.removeAll();
				//get the topic
				Topic datatype = dataManager.getSubTopics(action.getActionCommand());
				//split into subtopics
				
				if(datatype.getSubtopics().size()>0) {
					List<Topic> subs = datatype.getSubtopics();
					for(Topic sub : subs) {
						JButton subtops = new JButton(sub.getTitle());
						subtops.setBackground(new Color(235, 253, 220));
						subtops.setBorder(new LineBorder(new Color(255, 253, 151), 2, true));
						subtopicPanel.add(subtops);
					}
					
					subtopicPanel.revalidate();
					subtopicPanel.repaint();
				}
				
				break;
				
			case "Control":
				subtopicPanel.removeAll();
				Topic control = dataManager.getSubTopics(action.getActionCommand());
				//split into subtopics
				if(control.getSubtopics().size()>0) {
					List<Topic> subs = control.getSubtopics();
					//System.out.println(subs);
					for (Topic sub : subs) {
				        // Skip sub-subtopics
				        if (sub.getSubtopics() != null && !sub.getSubtopics().isEmpty()) {
				        	JButton subtops = new JButton(sub.getTitle());
				        	subtops.setBackground(new Color(225, 253, 230));
							subtops.setBorder(new LineBorder(new Color(255, 253, 151), 2, true));
					        subtopicPanel.add(subtops);
					        System.out.println(sub.getTitle());
				        }
				    }
						
					subtopicPanel.revalidate();
					subtopicPanel.repaint();
				}
				
				break;
				
			default:
				return;
			
		}
		
	}


}
