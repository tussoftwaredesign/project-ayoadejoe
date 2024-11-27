package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import interfaces.TopicsInterface;
import logic.DataBoy;

public class ManeFrame extends JFrame implements ActionListener{

	private JPanel contentPane;
	private static int resourcesLoaded = 0;
	private static DataBoy dataBoy;
	
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
								ManeFrame frame = new ManeFrame();
								frame.setVisible(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});	
				}
			};
			
		try {	
			dataBoy = new DataBoy(topicsInterface);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "One or more"
					+ " Database file is missing. So program cannot launch!");		
			e.printStackTrace();
		}
	}
	
	ArrayList<JButton> dataTypeButtons = new ArrayList<>();


	public ManeFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 923, 470);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setPreferredSize(new Dimension(200, 110));
		contentPane.add(splitPane, BorderLayout.SOUTH);
		
		JPanel panelButtons = new JPanel();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(panelButtons);
		FlowLayout fl_panelButtons = new FlowLayout(FlowLayout.LEFT, 5, 5);
		panelButtons.setLayout(fl_panelButtons);
		
		JButton btnDatatype = new JButton("Data Types");
		btnDatatype.addActionListener(this);
		btnDatatype.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnDatatype);
		
		JButton btnControl = new JButton("Control");
		btnControl.addActionListener(this);
		btnControl.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnControl);
		
		
		JButton btnStrings = new JButton("Strings");
		btnControl.addActionListener(this);
		btnStrings.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnStrings);
		
		JButton btnStrgBuilder = new JButton("StringBuilders");
		btnControl.addActionListener(this);
		btnStrgBuilder.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnStrgBuilder);
		
		JButton btnArrayList = new JButton("ArrayLists");
		btnControl.addActionListener(this);
		btnArrayList.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnArrayList);
		
		JButton btnClasses = new JButton("Classes");
		btnControl.addActionListener(this);
		btnClasses.setPreferredSize(new Dimension(120, 80));
		panelButtons.add(btnClasses);
		
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(250, 100));
		
		panel.setLayout(new BorderLayout(0, 0));
		
		splitPane.setLeftComponent(scrollPane);
		splitPane.setRightComponent(panel);
		splitPane.setDividerLocation(500);
		System.out.println("Working Now!");
	}


	@Override
	public void actionPerformed(ActionEvent action) {
		switch(action.getActionCommand()) {
			case "Data Type":
				ArrayList<JLabel> labels = dataBoy.getSubtopic(action.getActionCommand());
				
				if(labels.size()>0) {
					
				}
				break;
				
			default:
				return;
		}
		
	}


}
