package events_gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.json.JSONArray;
import org.json.JSONObject;

import concurrent_asyncs.AsyncFetcher;
import concurrent_asyncs.InternetChecker;
import concurrent_asyncs.JokeFetcher;
import concurrent_asyncs.QuizFetcher;
import enum_oops2.QuizCategory;
import history.ScoreNode;
import history.ScoreStorageMultiUser;
import nio_fileoperations.DataSaver;
import record_pojos.JokeData;
import record_pojos.QuizData;
import record_pojos.ScoreTracker;



/**
 * 
 * @author Joseph Adetunji Ayoade 2025
 * 
 * {@summary This application is created to satisfy the requirement for OOPS II TUS MSc. Software Design With Artificial Intelligence }
 * 
 * Who wants to be a Knowledge Bank, a Quiz & Joke Game, is a desktop application built with Java Swing that delivers a fun and educational 
 * experience. Users are presented with quiz questions. 
 * Upon answering correctly, they are rewarded with a joke fetched live from the web. 
 * If incorrect, a humorous or sarcastic joke is still shown. The application supports asynchronous data fetching, 
 * offline caching, enums, score tracking, and a modular design leveraging core Java features.
 */
public class QuizJokeApp extends JFrame{
    private static JLabel questionLabel;
    private static JPanel optionsPanel;
    private static JButton submitButton;
    private static JButton nextButton;
    private static JLabel jokeLabel;
    private static JPanel northPanel;
    private static JLabel lblNewLabel;
    private static JPanel eastPanel;
    private static JLabel lblNewLabel_1;
    private static JToggleButton networkButton;
    private static ButtonGroup answerGroup;
    private static QuizData currentQuiz;
    private static boolean connected;
    private static JTextArea historyScores;
    private JScrollPane scrollPane;
    private static final AsyncFetcher<QuizData> quizFetcher = new AsyncFetcher<>();
    private static final AsyncFetcher<JokeData> jokeFetcher = new AsyncFetcher<>();
    
    private static JLabel scoreLabel;
    private static JLabel categoryLabel;
    private static final ScoreTracker scoreTracker = new ScoreTracker();
    
    //retrieve user from storage
	private static Map<String, ScoreNode> allHistories = ScoreStorageMultiUser.loadAll();

    private static String username;

    private static List<QuizData> offlineQuizzes;
	private static List<JokeData> offlineJokes;
	
    public QuizJokeApp() {

        this.setTitle("Who wants to be a Knowledge Bank?");
       

        //add the application icon, this method is used because I would be packaging it as a jar
        this.setIconImage(
        	    Toolkit.getDefaultToolkit().getImage(
        	        QuizJokeApp.class.getClassLoader().getResource("data/wwtbakbsmall.png")
        	    )
        	);

        this.getContentPane().setBackground(new Color(255, 153, 204));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(this);
        this.setSize(700, 500);
        JPanel paddedContent = new JPanel(new BorderLayout(10, 10));
        paddedContent.setBorder(new CompoundBorder(new LineBorder(new Color(255, 153, 102)), new EmptyBorder(15, 15, 15, 15))); // top, left, bottom, right
        paddedContent.setBackground(new Color(255, 153, 204)); 
        this.setContentPane(paddedContent);


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
        
        //Add the application logo
        lblNewLabel_1.setIcon(new ImageIcon(
        	    Toolkit.getDefaultToolkit().getImage(
            	        QuizJokeApp.class.getClassLoader().getResource("data/wwtbakbsmall.png")
            	    ))
            	);
        northPanel.add(lblNewLabel_1, BorderLayout.WEST);
        
        networkButton = new JToggleButton("ONLINE");
        networkButton.putClientProperty("JButton.buttonType", "switch");
        networkButton.setSelected(true);
        networkButton.setBackground(new Color(0, 200, 83));  // green
    	networkButton.setForeground(Color.WHITE);
        northPanel.add(networkButton, BorderLayout.EAST);
        // Add a listener to change color when online or offline
        networkButton.addItemListener(e -> {
            if (networkButton.isSelected()) {
            	checkInternetConnectionAsync(networkButton);
            	networkButton.setBackground(new Color(0, 200, 83));  // green
            	networkButton.setForeground(Color.WHITE);
            } else {
            	networkButton.setBackground(Color.LIGHT_GRAY);
            	networkButton.setForeground(Color.BLACK);
            	networkButton.setText("Offline");
            }
        });


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
        scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        scoreLabel.setForeground(new Color(0, 0, 0));
        scoreLabel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Score", TitledBorder.RIGHT, TitledBorder.TOP, null, new Color(255, 51, 204)));
        scoreLabel.setBackground(new Color(255, 153, 204));
        categoryLabel = new JLabel("", SwingConstants.CENTER);
        categoryLabel.setForeground(new Color(0, 51, 102));
        categoryLabel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Category", TitledBorder.RIGHT, TitledBorder.TOP, null, new Color(255, 51, 204)));
        categoryLabel.setBackground(new Color(255, 153, 204));
        infoPanel.setLayout(new GridLayout(3, 1, 0, 5));
        infoPanel.add(scoreLabel);
        infoPanel.add(categoryLabel);
        
        scrollPane = new JScrollPane();
        scrollPane.setFont(new Font("Times New Roman", Font.PLAIN, 8));
        infoPanel.add(scrollPane);
        
        historyScores = new JTextArea("");
        historyScores.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "History", TitledBorder.RIGHT, TitledBorder.TOP, null, new Color(255, 51, 204)));
        historyScores.setFont(new Font("Tahoma", Font.PLAIN, 10));
        scrollPane.setViewportView(historyScores);
        historyScores.setLineWrap(true);
        historyScores.setWrapStyleWord(true);
        historyScores.setBackground(new Color(255, 255, 204));

        
        this.setVisible(true);

        checkInternetConnectionAsync(networkButton);
        

        
    	this.addWindowListener(new WindowAdapter() {
    		@Override
    		public void windowClosing(WindowEvent e) {
    			if(!username.isEmpty()) {
	    			System.out.println("Window closing... Saving information for user: "+username);
	    			ScoreNode previous = allHistories.get(username);
	    			ScoreNode newSession = new ScoreNode(username, LocalDateTime.now(), scoreTracker.getCorrect(), scoreTracker.getTotal(), previous);
	    			allHistories.put(username, newSession);
	    			ScoreStorageMultiUser.saveAll(allHistories);
    			}
    		}
    	});
        
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

    private static int offlineQuizCount = 0;
    private static int offlineJokeCount = 0;
    
    private static void loadQuiz() {
    	
    	if(networkButton.isSelected() && connected) {
	        jokeLabel.setText("Answer to see your reward...");
	        questionLabel.setText("Loading...");
	        optionsPanel.removeAll();
	        optionsPanel.revalidate();
	        optionsPanel.repaint();
	        
	        //the task is prepared to be invoked using AsyncFetcher, A task is created to fetch the quiz using a 
	        //Supplier, and the result is consumed by the displayQuiz as it receives the result
	        quizFetcher.fetch(QuizFetcher::fetchQuiz, QuizJokeApp::displayQuiz);
	        
    	}else {
    		
    		jokeLabel.setText("You are offline");
	        questionLabel.setText("Application is offline");
	        optionsPanel.removeAll();
	        optionsPanel.revalidate();
	        optionsPanel.repaint();
	        
	        displayQuiz(offlineQuizzes.get(--offlineQuizCount));
	        
	       
    	}
        	
    }
    
    static int c=0;

	private static void processOfflineCats() {
		System.out.println("Loading locally..."); c++;
		
		JPanel selectCategory = new JPanel();
		selectCategory.setLayout(new GridLayout());
      
		JRadioButton boxScience = new JRadioButton("Science"); boxScience.setActionCommand("science");
		JRadioButton boxHistory = new JRadioButton("History"); boxHistory.setActionCommand("history");
		JRadioButton boxArt = new JRadioButton("Art"); boxArt.setActionCommand("art");
		JRadioButton boxGeneral = new JRadioButton("General"); boxGeneral.setActionCommand("general");
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(boxGeneral);
		buttonGroup.add(boxHistory);
		buttonGroup.add(boxArt);
		buttonGroup.add(boxScience);
		
		selectCategory.add(boxGeneral );
		selectCategory.add(boxHistory );
		selectCategory.add(boxScience );
		selectCategory.add(boxArt );
		
		JOptionPane.showMessageDialog(null, selectCategory, "Play Offline: Select a Category", JOptionPane.INFORMATION_MESSAGE);
		
		String selectedBG = buttonGroup.getSelection().getActionCommand();
		System.out.println("Selected Radio:"+selectedBG);
		
		QuizCategory enumCategory = QuizCategory.fromString(selectedBG.toLowerCase());
		
		Predicate<QuizData> selectedCategory = quiz -> quiz.category() != null && quiz.category().equals(enumCategory);


		offlineQuizzes = quizSaver.loadAll().stream()
		    .filter(selectedCategory)
		    .collect(Collectors.toList());
		
		offlineQuizCount = offlineQuizzes.size();
		
		if(offlineJokeCount<1) {
			if(c > 3) {
				JOptionPane.showMessageDialog(null, "It seems there are no local quizzes available on your machine. Kindly connect to the internet.");
				return;
			}else {
				JOptionPane.showMessageDialog(null, selectedBG+" category is not available. Select another one.");
				processOfflineCats();
			}
			
		}
		
		System.out.println("List Quiz Selected:"+offlineQuizzes);
		
		offlineJokes = jokeSaver.loadAll();
		offlineJokeCount = offlineJokes.size();
		
		System.out.println("List Jokes:"+offlineJokes);
		
	}
	
	

    //consumer-like method returns void
    private static void displayQuiz(QuizData quiz) {
    	System.out.println("In display:"+quiz);
        currentQuiz = quiz;
        categoryLabel.setText(quiz.category().name());
        questionLabel.setText("<html><div style='text-align: center;'>" + quiz.question() + "</div></html>");
        answerGroup = new ButtonGroup();
        System.out.println(quiz.options());
        System.out.println();
        
        //save the quiz
        if(connected)quizSaver.appendOne(quiz); 

        for (String option : quiz.options()) {
            JRadioButton radio = new JRadioButton(option);
            radio.setFont(new Font("Calibri", Font.PLAIN, 16));
            radio.setActionCommand(option);
            radio.setHorizontalAlignment(SwingConstants.CENTER);
            radio.setBackground(new Color(255, 255, 204));
            System.out.println(option);
            answerGroup.add(radio);
            optionsPanel.add(radio);
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();
        
        
    }

    private static void handleSubmit(ActionEvent e) {
    	//if an answer is not selected and submit is clicked, return and prompt that the person should select answer first
        if (currentQuiz == null || answerGroup.getSelection() == null) {
            jokeLabel.setText("Please select an answer first.");
            return;
        }

        String selected = answerGroup.getSelection().getActionCommand();	//which radiobutton was selected?
        boolean correct = selected.equals(currentQuiz.correctAnswer());

        jokeLabel.setText(correct ? "Correct! Fetching a joke..." : "Oops! Let's lighten the mood...");
        
        //record the score
        scoreTracker.recordAttempt(correct);
        scoreLabel.setText(scoreTracker.toString());

      
        if(connected) {
        	//the joke fetcher task is prepared to be invoked using AsyncFetcher, A task is created to fetch the joke using a 
        	//Supplier, and the result is consumed by the lambda, which receives the result 'joke' and displays it and then saves it
            
	        jokeFetcher.fetch(JokeFetcher::fetchJoke, joke -> {
	        	jokeLabel.setText(joke.getFormattedJoke());
	        	if(!joke.delivery().equals("Couldn't fetch joke!")) jokeSaver.appendOne(joke);
	        });
        }else {
        	jokeLabel.setText(offlineJokes.get(--offlineJokeCount).getFormattedJoke());
        }
        
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
    
    
    private static final DataSaver<QuizData> quizSaver = new DataSaver<>(
    	    "quizzes.json",
    	    quiz -> String.format(
    	        "{\"question\":\"%s\", \"correctAnswer\":\"%s\", \"category\":\"%s\", \"options\":%s}",
    	        quiz.question(), quiz.correctAnswer(), quiz.category(), new JSONArray(quiz.options())
    	    ),
    	    line -> {
    	        try {
    	            JSONObject obj = new JSONObject(line);
    	            String question = obj.getString("question");
    	            String correct = obj.getString("correctAnswer");
    	            QuizCategory category = QuizCategory.fromString(obj.getString("category"));

    	            JSONArray optionsArray = obj.getJSONArray("options");
    	            List<String> options = new ArrayList<>();
    	            for (int i = 0; i < optionsArray.length(); i++) {
    	                options.add(optionsArray.getString(i));
    	            }

    	            return new QuizData(question, options, correct, category);
    	        } catch (Exception e) {
    	            return new QuizData("Parsing error", List.of(), "", null);
    	        }
    	    }
    	);
  

    private static void checkInternetConnectionAsync(JToggleButton toggleButton) {
        toggleButton.setText("Checking...");
        toggleButton.setBackground(Color.ORANGE);
        toggleButton.setSelected(true);

        new Thread(() -> {
            connected = InternetChecker.hasInternet();

            SwingUtilities.invokeLater(() -> {
                if (connected) {
                    toggleButton.setText("Online");
                    toggleButton.setBackground(new Color(76, 175, 80));
                    toggleButton.setForeground(Color.WHITE);
                    toggleButton.setSelected(true);
                    
                } else {
                    toggleButton.setText("Offline");
                    toggleButton.setBackground(Color.RED);
                    toggleButton.setForeground(Color.WHITE);
                    toggleButton.setSelected(false);
                    processOfflineCats();
                }
                loadQuiz();
                checkUser();
            });
        }).start();
    }

	private static void checkUser() {
	        username = JOptionPane.showInputDialog(null, "Enter your username. Press 'Cancel' to continue as guest (Note, your history would not be saved)", "User or Guest?", JOptionPane.INFORMATION_MESSAGE);
	        System.out.println(username);
	        
	        if(username != null) {
	        	username = username.toLowerCase();
	        	if(!allHistories.isEmpty()) {
	        		ScoreNode previous = allHistories.get(username.trim().toLowerCase());
	        		
	        		if(previous == null) {
	        			//save as new user
	        			ScoreNode newSession = new ScoreNode(username, LocalDateTime.now(), 0, 0, previous);
	                	allHistories.put(username, newSession);
	                	ScoreStorageMultiUser.saveAll(allHistories);
	        		}else {
	        			//user exist, print history
	        			ScoreNode history = allHistories.get(username);
	        			//if(history != null)historyScores.setText("History:"+"\n");
	        			if (history != null)history.printHistory(historyScores, 0);
	
	        		}
	        	}
	        }
		
	}
}
