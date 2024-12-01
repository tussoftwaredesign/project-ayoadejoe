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

public class DatabaseManager {
	
	private Scanner scTopics, scNotes, scImages;
	private ArrayList<String> topics = new ArrayList<>();
	private ArrayList<String> notes = new ArrayList<>();
	private ArrayList<String> images = new ArrayList<>();
	private TopicsInterface topicInterface;
	private Matryoshka matryoshka;
	
	public DatabaseManager(TopicsInterface topicInterface) throws FileNotFoundException, IllegalArgumentException{
		
		activateTopics(topicInterface);
		activateNotes(topicInterface);
		activateImages(topicInterface);
		if(topics != null) {
			matryoshka = new Matryoshka(topics);		//Create objects of each topic by recursion because each topic has a subtopic
			//matryoshka.printTopics(matryoshka.getAllTopics(), 0);
		}
	}
	
	public Topic getSubTopics(String requestedTopic){
		//get all the topics from Matryoshka
		if(matryoshka != null) {
			ArrayList<Topic> allTopics = matryoshka.getAllTopics();
			
			//for each main topic
			for(Topic mainTopic : allTopics) {
				if(mainTopic.getTitle().replace("~", "").trim().equals(requestedTopic)) {
					System.out.println("Topic found:"+mainTopic.getTitle());
					return mainTopic;
				}
			}
		}
		return null;
	}
	

	
	private void activateTopics(TopicsInterface topicInterface) throws FileNotFoundException{
		try {
			scTopics = new Scanner(new File("Topics.selfie"));
			while(scTopics.hasNextLine()) {
				String line = scTopics.nextLine().trim();
				line = line.replace("\u00A0", "").replace("\uFEFF", ""); // Remove non-breaking space and BOM
				topics.add(line);
			}
			//System.out.println(topics);
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
				notes.add(scNotes.nextLine());
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
				images.add(scImages.nextLine());
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
