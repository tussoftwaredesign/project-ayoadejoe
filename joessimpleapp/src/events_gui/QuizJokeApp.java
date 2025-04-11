package events_gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
 * Who Wants to be a Knowledge Bank - Quiz & Joke App
 * A desktop application that presents users with quiz questions and rewards correct answers with jokes.
 *
 * @Author: Joseph Adetunji Ayoade
 * Created for: MSc Software Design With AI - OOP2 Coursework
 * April 2025
 */

public class QuizJokeApp {
    // UI Reference : Separation of concern
    private final QuizJokeAppUI ui;
    
    //LOGIC
    // Fetchers
    //The AsyncSaver method fetch uses functional interfaces, Supplier and Consumer which are lazy to retrieve the jsons from the Quiz and Joke APIs
    //The consumer acts as a callback and is invoked when the Async Fetchers return from the API with their jsons
    //The lambdas are invoked when the AsyncFetcher object is created
    //The objects of both Azynfetchers are then used to retrieve quizzes and jokes as json using the Joke and Quiz Fetcher Async classes
    private final AsyncFetcher<QuizData> quizFetcher = new AsyncFetcher<>();
    private final AsyncFetcher<JokeData> jokeFetcher = new AsyncFetcher<>();
    // Data and State
    private QuizData currentQuiz;
    private boolean connected;
    private String username;
    
  //We initialize the scoretracker which is a basic class with to keep track of the user scores
    private final ScoreTracker scoreTracker = new ScoreTracker();
    //Custom methods to inject lambdas into the generic DataSaver constructor 
    private final DataSaver<JokeData> jokeSaver = createJokeSaver();  
    private final DataSaver<QuizData> quizSaver = createQuizSaver();
    
    private List<QuizData> offlineQuizzes;
    private List<JokeData> offlineJokes;
    private int offlineQuizCount = 0;
    private int offlineJokeCount = 0;
    
    private ButtonGroup answerGroup;

    //Storage
    //This map is created to load the users and their history, it uses the name as key and the recursive class node as the details
    private final Map<String, ScoreNode> allHistories = ScoreStorageMultiUser.loadAll();

    
    
    
   //Methods

    public QuizJokeApp() {
        ui = new QuizJokeAppUI();
        setupEventListeners();
        checkInternetConnectionAsync();
    }

    private void setupEventListeners() {
        // Network toggle listener
        ui.getNetworkButton().addItemListener(e -> {
            if (ui.getNetworkButton().isSelected()) {
                checkInternetConnectionAsync();
                ui.getNetworkButton().setBackground(new Color(50, 200, 50));
                ui.getNetworkButton().setForeground(Color.WHITE);
            } else {
                ui.getNetworkButton().setBackground(Color.LIGHT_GRAY);
                ui.getNetworkButton().setForeground(Color.BLACK);
                ui.getNetworkButton().setText("Offline");
            }
        });

        // Submit and next button listeners
        ui.getSubmitButton().addActionListener(this::handleSubmit);
        ui.getNextButton().addActionListener(e -> loadQuiz());

        // Window closing listener to save user data
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (username != null && !username.isEmpty()) {
                    System.out.println("Window closing... Saving information for user: " + username);
                    ScoreNode previous = allHistories.get(username);
                    ScoreNode newSession = new ScoreNode(username, LocalDateTime.now(),
                            scoreTracker.getCorrect(), scoreTracker.getTotal(), previous);
                    allHistories.put(username, newSession);
                    ScoreStorageMultiUser.saveAll(allHistories);
                }
            }
        });
    }

    private void loadQuiz() {
        if (ui.getNetworkButton().isSelected() && connected) {
            ui.getJokeLabel().setText("Answer to see your reward...");
            ui.getQuestionLabel().setText("Loading...");
            ui.getOptionsPanel().removeAll();
            ui.getOptionsPanel().revalidate();
            ui.getOptionsPanel().repaint();
            quizFetcher.fetch(QuizFetcher::fetchQuiz, this::displayQuiz);
        } else {
            ui.getJokeLabel().setText("You are offline");
            ui.getQuestionLabel().setText("Application is offline");
            ui.getOptionsPanel().removeAll();
            ui.getOptionsPanel().revalidate();
            ui.getOptionsPanel().repaint();
            displayQuiz(offlineQuizzes.get(--offlineQuizCount));
        }
    }

    private void displayQuiz(QuizData quiz) {
        System.out.println("In display: " + quiz);
        currentQuiz = quiz;
        ui.getCategoryLabel().setText(quiz.category().name());
        ui.getQuestionLabel().setText("<html><div style='text-align: center;'>" + quiz.question() + "</div></html>");
        answerGroup = new ButtonGroup();

        if (connected) quizSaver.appendOne(quiz);

        for (String option : quiz.options()) {
            JRadioButton radio = new JRadioButton(option);
            radio.setFont(new Font("Calibri", Font.PLAIN, 16));
            radio.setActionCommand(option);
            radio.setHorizontalAlignment(SwingConstants.CENTER);
            radio.setBackground(new Color(255, 255, 204));
            answerGroup.add(radio);
            ui.getOptionsPanel().add(radio);
        }

        ui.getOptionsPanel().revalidate();
        ui.getOptionsPanel().repaint();
    }

    private void handleSubmit(ActionEvent e) {
        if (currentQuiz == null || answerGroup.getSelection() == null) {
            ui.getJokeLabel().setText("Please select an answer first.");
            return;
        }

        String selected = answerGroup.getSelection().getActionCommand();
        boolean correct = selected.equals(currentQuiz.correctAnswer());
        ui.getJokeLabel().setText(correct ? "Correct! Fetching a joke..." : "Oops! Let's lighten the mood...");
        scoreTracker.recordAttempt(correct);
        ui.getScoreLabel().setText(scoreTracker.toString());

        if (connected) {
            jokeFetcher.fetch(JokeFetcher::fetchJoke, joke -> {
                ui.getJokeLabel().setText(joke.getFormattedJoke());
                if (!joke.delivery().equals("Couldn't fetch joke!")) jokeSaver.appendOne(joke);
            });
        } else {
            ui.getJokeLabel().setText(offlineJokes.get(--offlineJokeCount).getFormattedJoke());
        }
    }

    private void checkInternetConnectionAsync() {
        ui.getNetworkButton().setText("Checking...");
        ui.getNetworkButton().setBackground(Color.ORANGE);
        ui.getNetworkButton().setSelected(true);

        new Thread(() -> {
            connected = InternetChecker.hasInternet();
            SwingUtilities.invokeLater(() -> {
                if (connected) {
                    ui.getNetworkButton().setText("Online");
                    ui.getNetworkButton().setBackground(new Color(76, 175, 80));
                    ui.getNetworkButton().setForeground(Color.WHITE);
                    ui.getNetworkButton().setSelected(true);
                } else {
                    ui.getNetworkButton().setText("Offline");
                    ui.getNetworkButton().setBackground(Color.RED);
                    ui.getNetworkButton().setForeground(Color.WHITE);
                    ui.getNetworkButton().setSelected(false);
                    processOfflineCats();
                }
                loadQuiz();
                checkUser();
            });
        }).start();
    }

    private void checkUser() {
        username = JOptionPane.showInputDialog(null,
                "Enter your username. Press 'Cancel' to continue as guest (Note, your history would not be saved)",
                "User or Guest?", JOptionPane.INFORMATION_MESSAGE);
        System.out.println(username);

        if (username != null) {
            username = username.toLowerCase();
            if (!allHistories.isEmpty()) {
                ScoreNode previous = allHistories.get(username.trim().toLowerCase());
                if (previous == null) {
                    ScoreNode newSession = new ScoreNode(username, LocalDateTime.now(), 0, 0, previous);
                    allHistories.put(username, newSession);
                    ScoreStorageMultiUser.saveAll(allHistories);
                } else {
                    ScoreNode history = allHistories.get(username);
                    if (history != null) history.printHistory(ui.getHistoryScores(), 0);
                }
            }
        }
    }

    private void processOfflineCats() {
        System.out.println("Loading locally...");
        JPanel selectCategory = new JPanel(new GridLayout());
        JRadioButton boxScience = new JRadioButton("Science");
        boxScience.setActionCommand("science");
        JRadioButton boxHistory = new JRadioButton("History");
        boxHistory.setActionCommand("history");
        JRadioButton boxArt = new JRadioButton("Art");
        boxArt.setActionCommand("art");
        JRadioButton boxGeneral = new JRadioButton("General");
        boxGeneral.setActionCommand("general");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(boxGeneral);
        buttonGroup.add(boxHistory);
        buttonGroup.add(boxArt);
        buttonGroup.add(boxScience);

        selectCategory.add(boxGeneral);
        selectCategory.add(boxHistory);
        selectCategory.add(boxScience);
        selectCategory.add(boxArt);

        JOptionPane.showMessageDialog(null, selectCategory,
                "Play Offline: Select a Category", JOptionPane.INFORMATION_MESSAGE);

        String selectedBG = buttonGroup.getSelection().getActionCommand();
        System.out.println("Selected Radio: " + selectedBG);

        QuizCategory enumCategory = QuizCategory.fromString(selectedBG.toLowerCase());
        Predicate<QuizData> selectedCategory = quiz -> quiz.category() != null && quiz.category().equals(enumCategory);

        offlineQuizzes = quizSaver.loadAll().stream()
                .filter(selectedCategory)
                .collect(Collectors.toList());
        offlineQuizCount = offlineQuizzes.size();

        if (offlineQuizCount < 1) {
            JOptionPane.showMessageDialog(null,
                    selectedBG + " category is not available. Select another one.");
            processOfflineCats();
            return;
        }

        System.out.println("List Quiz Selected: " + offlineQuizzes);
        offlineJokes = jokeSaver.loadAll();
        offlineJokeCount = offlineJokes.size();
        System.out.println("List Jokes: " + offlineJokes);
    }

    private DataSaver<JokeData> createJokeSaver() {
        return new DataSaver<>("jokes.json",
                joke -> String.format("{\"setup\":\"%s\", \"delivery\":\"%s\"}", joke.setup(), joke.delivery()),
                line -> {
                    try {
                        JSONObject obj = new JSONObject(line);
                        return new JokeData(obj.getString("setup"), obj.getString("delivery"));
                    } catch (Exception e) {
                        return new JokeData("Oops!", "Failed to parse joke.");
                    }
                });
    }

    private DataSaver<QuizData> createQuizSaver() {
        return new DataSaver<>("quizzes.json",
                quiz -> String.format(
                        "{\"question\":\"%s\", \"correctAnswer\":\"%s\", \"category\":\"%s\", \"options\":%s}",
                        quiz.question(), quiz.correctAnswer(), quiz.category(), new JSONArray(quiz.options())),
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
                });
    }


}