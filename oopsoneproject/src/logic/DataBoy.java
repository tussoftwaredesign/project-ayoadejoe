package logic;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;

import interfaces.TopicsInterface;

public class DataBoy {
	
	private Scanner scTopics, scNotes, scImages;
	private ArrayList<String> contents = new ArrayList<>();
	private TopicsInterface topicInterface;
	
	public DataBoy(TopicsInterface topicInterface) throws FileNotFoundException{
		
		activateTopics(topicInterface);
		activateNotes(topicInterface);
		activateImages(topicInterface);
		
	}
	
	public ArrayList<JLabel> getTopic(String topic){
		
		ArrayList<JLabel> topics = new ArrayList<>();
		boolean checkIn = false;
		if(contents != null && contents.size()>0) {
			for(String line : contents) {
				if (line.contains(topic.trim()) && line.contains("~"))
					checkIn = true;
				
				if(line.trim().equals("#"))
					checkIn = false;
				
				if(checkIn) {
					JLabel label = new JLabel(line);
					label.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							// TODO Auto-generated method stub
							super.mouseClicked(e);
						}
					});
					label.setName(line);
					topics.add(label);
				}
				
				
			}
		}	
		return null;
	}
	
	protected ArrayList<JButton> getSubtopics(String subTopic){
		
		return null;
	}
	
	private void activateTopics(TopicsInterface topicInterface) throws FileNotFoundException{
		try {
			scTopics = new Scanner(new File("Topics.selfie"));
			while(scTopics.hasNextLine()) {
				contents.add(scTopics.nextLine());
			}
			topicInterface.onComplete(true);
		} catch (FileNotFoundException e) {
			topicInterface.onComplete(false);
			throw e;
		}finally {
			if (scTopics != null)scTopics.close();
		}
	}
	
	private void activateNotes(TopicsInterface topicInterface) throws FileNotFoundException{
		try {
			scNotes = new Scanner(new File("Notes.selfie"));
			while(scNotes.hasNextLine()) {
				contents.add(scNotes.nextLine());
			}
			topicInterface.onComplete(true);
		} catch (FileNotFoundException e) {
			topicInterface.onComplete(false);
			throw e;
		}finally {
			if (scNotes != null)scNotes.close();
		}
	}
	
	private void activateImages(TopicsInterface topicInterface) throws FileNotFoundException{
		try {
			scImages = new Scanner(new File("Images.selfie"));
			while(scImages.hasNextLine()) {
				contents.add(scImages.nextLine());
			}
			topicInterface.onComplete(true);
		} catch (FileNotFoundException e) {
			topicInterface.onComplete(false);
			throw e;
		}finally {
			if (scImages != null)scImages.close();
		}
	}

}
