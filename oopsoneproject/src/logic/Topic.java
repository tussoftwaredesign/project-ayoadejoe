package logic;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    private String title;
    private String content;
    private List<Topic> subtopics;
    private List<String> images;

    public Topic(String title, String content) {
        this.title = title;
        this.content = content;
        this.subtopics = new ArrayList<>();
        this.images = new ArrayList<>();
    }

    // Getters and setters for title, content, subtopics, and images

    public void addSubtopic(Topic subtopic) {
        subtopics.add(subtopic);
    }

    public void addImage(String imagePath) {
        images.add(imagePath);
    }

    // Additional methods as needed
}
