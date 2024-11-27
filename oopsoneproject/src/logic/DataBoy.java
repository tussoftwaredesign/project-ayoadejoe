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
	
	public ArrayList<JLabel> getTopic(){
		
		ArrayList<JLabel> topics = new ArrayList<>();
		boolean newtopic = false;
		String topic="";
		String subtopic = "";
		String note = "";
		if(contents != null && contents.size()>0) {
			for(int i =0; i<contents.size(); i++) {
				
				String line = contents.get(i);
				
				if(line.trim().equals("#"))newtopic = false;
				
				if (line.contains("~")) {//this is a topic
					topic = line;
					String topicnote = contents.get(i+1);
					newtopic = true;
					continue;
				}
				
				if(newtopic && line.contains("-")) {
					subtopic = line;
					continue;
				}
				
				if(newtopic && line.contains("*")) {
					note = line;
					continue;
				}
				
				if(newtopic) {
					
				}

				
				
			}
		}	
		return topics;
	}
	
	public ArrayList<JButton> getSubtopics(String subTopic){
		
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
