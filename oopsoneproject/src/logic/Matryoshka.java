package logic;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class Matryoshka {

	private ArrayList<Topic> allTopic = new ArrayList<>();
	Topic newTopic = null;
    Topic subTopic = null;
    Topic subSubTopic = null;
    
	//creates Objects of each topic, subsequently creating a full array of all the topics
	public Matryoshka(ArrayList<String> topicsFileList) throws IllegalArgumentException{
		ArrayList<JLabel> subtopics = new ArrayList<>();
		boolean newtopic = false;
		String topic="";
		ObjectifyCount i = new ObjectifyCount(0);

		if(topicsFileList != null && topicsFileList.size()>0) {
			while(i.i < topicsFileList.size()) {
				
				String line = topicsFileList.get(i.i).trim();
				line = line.replace("\u00A0", "").replace("\uFEFF", ""); // Remove non-breaking space and BOM
				//System.out.println(line);
				if(line.trim().equals("#")) {
					newtopic = false;
					//add the topic just concluded into the array
					allTopic.add(newTopic);
					//clean the slate and start new topic
					newTopic = null;
					subTopic = null;
					subSubTopic = null;
					
				}else if (line.contains("~")) {//this is a topic
					 loadTopic(line, topicsFileList, i);
					 System.out.println(line);
			         newtopic = true;
			         
				}else if(newtopic && line.contains(">")) {
					System.out.println(line);
					loadSubtopic(line, topicsFileList, i);
					
				}else if(newtopic && line.contains("**")) {
					System.out.println(line);
					loadSubSubtopic(line, topicsFileList, i);
				}
				i.i++;
			}
		}	
	}

	public ArrayList<Topic> getAllTopics() {
		return allTopic;
	}

	public void setAllTopics(ArrayList<Topic> eachTopic) {
		this.allTopic = eachTopic;
	}
	
	private void loadTopic(String line, ArrayList<String> topicsFileList, ObjectifyCount i) throws IllegalArgumentException{
		if (i.i + 1 >= topicsFileList.size()) throw new IllegalArgumentException("Malformed file: Missing content for topic " + line+" line no:"+i);
	    //if exception throws, program continues to run
	    String content = topicsFileList.get(++i.i); // Increment i
	    newTopic = new Topic(line, content);
	}

	private void loadSubtopic(String line, ArrayList<String> topicsFileList, ObjectifyCount i) {
		if (i.i + 2 >= topicsFileList.size()) throw new IllegalArgumentException("Malformed file: Missing content or image for subtopic " + line+" line no:"+i);
        
	    String subtopicContent = topicsFileList.get(++i.i);
	    subTopic = new Topic(line, subtopicContent);
	    String subtopicImage = topicsFileList.get(++i.i);
	    subTopic.addImage(subtopicImage);
	    newTopic.addSubtopic(subTopic);
	}

	private void loadSubSubtopic(String line, ArrayList<String> topicsFileList, ObjectifyCount i) {
		if (i.i + 1 >= topicsFileList.size()) throw new IllegalArgumentException("Malformed file: Missing content for sub-subtopic " + line+" line no:"+i);
	    //if exception throws, program continues to run
	    String subSubTopicNotes = topicsFileList.get(++i.i);
	    subSubTopic = new Topic(line, subSubTopicNotes);
	    subTopic.addSubtopic(subSubTopic);
	}

	
	
	//for testing
	public void printTopics(List<Topic> topics, int depth) {
	    for (Topic topic : topics) {
	        for (int i = 0; i < depth; i++) {
	            System.out.print("  ");
	        }
	        System.out.println("- " + topic.getTitle());
	        if (topic.getSubtopics() != null && !topic.getSubtopics().isEmpty()) {
	            printTopics(topic.getSubtopics(), depth + 1);
	        }
	    }
	}


}


class ObjectifyCount{	//by objectifying count, we would make it mutable, so that changes made in each method as i would be passed by reference
	int i;
	public ObjectifyCount(int i) {
        this.i = i;
    }
}