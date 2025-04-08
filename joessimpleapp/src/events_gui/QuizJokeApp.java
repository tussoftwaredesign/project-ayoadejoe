package events_gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import concurrent_asyncs.AsyncFetcher;
import concurrent_asyncs.JokeFetcher;
import concurrent_asyncs.QuizFetcher;
import nio_fileoperations.DataSaver;
import record_pojos.JokeData;
import record_pojos.QuizData;
import record_pojos.ScoreTracker;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JSeparator;
import java.awt.Component;
import javax.swing.border.CompoundBorder;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class QuizJokeApp {
    private static JLabel questionLabel;
    private static JPanel optionsPanel;
    private static JButton submitButton;
    private static JButton nextButton;
    private static JLabel jokeLabel;
    private static ButtonGroup answerGroup;
    private static QuizData currentQuiz;
    private static final AsyncFetcher<QuizData> quizFetcher = new AsyncFetcher<>();
    private static final AsyncFetcher<JokeData> jokeFetcher = new AsyncFetcher<>();
    
    private static JLabel scoreLabel;
    private static JLabel categoryLabel;
    private static final ScoreTracker scoreTracker = new ScoreTracker();

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

   
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Who wants to be a Knowledge Bank?");
        frame.setIconImage(
        	    Toolkit.getDefaultToolkit().getImage(
        	        QuizJokeApp.class.getClassLoader().getResource("data/wwtbakbsmall.png")
        	    )
        	);

        frame.getContentPane().setBackground(new Color(255, 153, 204));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        JPanel paddedContent = new JPanel(new BorderLayout(10, 10));
        paddedContent.setBorder(new CompoundBorder(new LineBorder(new Color(255, 153, 102)), new EmptyBorder(15, 15, 15, 15))); // top, left, bottom, right
        paddedContent.setBackground(new Color(255, 153, 204)); 
        frame.setContentPane(paddedContent);


        optionsPanel = new JPanel();
        optionsPanel.setBackground(new Color(255, 255, 204));
        paddedContent.add(optionsPanel, BorderLayout.CENTER);
        optionsPanel.setLayout(new GridLayout(4, 0, 0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 153, 204));
        submitButton = createRoundedButton("Submit Answer");
        nextButton = createRoundedButton("Next Question");
        buttonPanel.add(submitButton);
        buttonPanel.add(nextButton);

        jokeLabel = new JLabel("Answer to see your reward...", SwingConstants.CENTER);
        jokeLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        jokeLabel.setForeground(Color.DARK_GRAY);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(255, 153, 204));
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(jokeLabel, BorderLayout.SOUTH);
        paddedContent.add(southPanel, BorderLayout.SOUTH);
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        northPanel = new JPanel();
        northPanel.setBackground(new Color(255, 153, 204));
        paddedContent.add(northPanel, BorderLayout.NORTH);
        northPanel.setLayout(new BorderLayout(0, 0));
        questionLabel = new JLabel("Loading question...", SwingConstants.CENTER);
        northPanel.add(questionLabel);
        questionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        
        lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setIcon(new ImageIcon(
        	    Toolkit.getDefaultToolkit().getImage(
            	        QuizJokeApp.class.getClassLoader().getResource("data/wwtbakbsmall.png")
            	    ))
            	);
        northPanel.add(lblNewLabel_1, BorderLayout.WEST);


        submitButton.addActionListener(QuizJokeApp::handleSubmit);
        nextButton.addActionListener(e -> loadQuiz());
        
        eastPanel = new JPanel();
        paddedContent.add(eastPanel, BorderLayout.EAST);
        eastPanel.setPreferredSize(new Dimension(200, 10));
        eastPanel.setLayout(new BorderLayout(0, 0));
        
     // Score and Category panel
        JPanel infoPanel = new JPanel();
        eastPanel.add(infoPanel);
        infoPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        infoPanel.setBackground(new Color(255, 255, 204));
        scoreLabel = new JLabel("Score: 0/0 (0%)", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        scoreLabel.setForeground(new Color(0, 0, 0));
        scoreLabel.setBorder(new LineBorder(new Color(255, 153, 255), 1, true));
        scoreLabel.setBackground(new Color(255, 153, 204));
        categoryLabel = new JLabel("Category: N/A", SwingConstants.CENTER);
        categoryLabel.setForeground(new Color(0, 51, 102));
        categoryLabel.setBorder(new LineBorder(new Color(255, 153, 255)));
        categoryLabel.setBackground(new Color(255, 153, 204));
        infoPanel.setLayout(new GridLayout(2, 1, 0, 5));
        infoPanel.add(scoreLabel);
        infoPanel.add(categoryLabel);
        //frame.add(infoPanel, BorderLayout.BEFORE_FIRST_LINE);


        frame.setVisible(true);
        loadQuiz(); // initial load
    }

    private static JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 40));
        return button;
    }

    private static void loadQuiz() {
        jokeLabel.setText("Answer to see your reward...");
        questionLabel.setText("Loading...");
        optionsPanel.removeAll();
        optionsPanel.revalidate();
        optionsPanel.repaint();
        quizFetcher.fetch(QuizFetcher::fetchQuiz, QuizJokeApp::displayQuiz);
    }

    private static void displayQuiz(QuizData quiz) {
        currentQuiz = quiz;
        categoryLabel.setText("Category: " + quiz.category().name());
        questionLabel.setText("<html><div style='text-align: center;'>" + quiz.question() + "</div></html>");
        answerGroup = new ButtonGroup();

        for (String option : quiz.options()) {
            JRadioButton radio = new JRadioButton(option);
            radio.setFont(new Font("Calibri", Font.PLAIN, 16));
            radio.setActionCommand(option);
            radio.setHorizontalAlignment(SwingConstants.CENTER);
            radio.setBackground(new Color(255, 255, 204));
            answerGroup.add(radio);
            optionsPanel.add(radio);
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();
        
        
    }

    private static void handleSubmit(ActionEvent e) {
        if (currentQuiz == null || answerGroup.getSelection() == null) {
            jokeLabel.setText("Please select an answer first.");
            return;
        }

        String selected = answerGroup.getSelection().getActionCommand();
        boolean correct = selected.equals(currentQuiz.correctAnswer());

        jokeLabel.setText(correct ? "Correct! Fetching a joke..." : "Oops! Let's lighten the mood...");
        scoreTracker.recordAttempt(correct);
        scoreLabel.setText(scoreTracker.toString());

        jokeFetcher.fetch(JokeFetcher::fetchJoke, joke -> {
        	jokeLabel.setText(joke.getFormattedJoke());
        	jokeSaver.appendOne(joke);
        });
        
    }
    
    private static final DataSaver<JokeData> jokeSaver = new DataSaver<>(
    	    "jokes.json", 
    	    joke -> String.format("{\"setup\":\"%s\", \"delivery\":\"%s\"}", joke.setup(), joke.delivery()),
    	    line -> {
    	        try {
    	            JSONObject obj = new JSONObject(line);
    	            return new JokeData(obj.getString("setup"), obj.getString("delivery"));
    	        } catch (Exception e) {
    	            return new JokeData("Oops!", "Failed to parse joke.");
    	        }
    	    }
    	);
    private static JPanel northPanel;
    private static JLabel lblNewLabel;
    private static JPanel eastPanel;
    private static JLabel lblNewLabel_1;

    
}
