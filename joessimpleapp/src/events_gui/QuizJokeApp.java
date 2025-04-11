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
 * Who Wants to be a Knowledge Bank - Quiz & Joke App
 * A desktop application built using Java Swing that presents users with quiz questions
 * and rewards correct answers with jokes fetched live from the internet.
 *
 * @Author: Joseph Adetunji Ayoade
 * Created for: MSc Software Design With AI - OOP2 Coursework
 * April 2025
 */
public class QuizJokeApp extends JFrame {
    // UI Components
    private JLabel questionLabel;
    private JPanel optionsPanel;
    private JButton submitButton;
    private JButton nextButton;
    private JLabel jokeLabel;
    private JToggleButton networkButton;
    private ButtonGroup answerGroup;
    private JLabel scoreLabel;
    private JLabel categoryLabel;
    private JTextArea historyScores;

    // Data and State
    private QuizData currentQuiz;
    private boolean connected;
    private String username;
    private List<QuizData> offlineQuizzes;
    private List<JokeData> offlineJokes;
    private int offlineQuizCount = 0;
    private int offlineJokeCount = 0;

    // Fetchers and Storage
    private final AsyncFetcher<QuizData> quizFetcher = new AsyncFetcher<>();
    private final AsyncFetcher<JokeData> jokeFetcher = new AsyncFetcher<>();
    private final ScoreTracker scoreTracker = new ScoreTracker();
    private final Map<String, ScoreNode> allHistories = ScoreStorageMultiUser.loadAll();
    private final DataSaver<JokeData> jokeSaver = createJokeSaver();
    private final DataSaver<QuizData> quizSaver = createQuizSaver();

    public QuizJokeApp() {
        initializeUI();
        setupEventListeners();
        checkInternetConnectionAsync();
    }
    
    //Logic Section
    
    private void setupEventListeners() {
        // Network toggle listener
        networkButton.addItemListener(e -> {
            if (networkButton.isSelected()) {
                checkInternetConnectionAsync();
                networkButton.setBackground(new Color(0, 200, 83));
                networkButton.setForeground(Color.WHITE);
            } else {
                networkButton.setBackground(Color.LIGHT_GRAY);
                networkButton.setForeground(Color.BLACK);
                networkButton.setText("Offline");
            }
        });

        // Submit and next button listeners
        submitButton.addActionListener(this::handleSubmit);
        nextButton.addActionListener(e -> loadQuiz());

        // Window closing listener to save user data
        addWindowListener(new WindowAdapter() {
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
        if (networkButton.isSelected() && connected) {
            jokeLabel.setText("Answer to see your reward...");
            questionLabel.setText("Loading...");
            optionsPanel.removeAll();
            optionsPanel.revalidate();
            optionsPanel.repaint();
            quizFetcher.fetch(QuizFetcher::fetchQuiz, this::displayQuiz);
        } else {
            jokeLabel.setText("You are offline");
            questionLabel.setText("Application is offline");
            optionsPanel.removeAll();
            optionsPanel.revalidate();
            optionsPanel.repaint();
            displayQuiz(offlineQuizzes.get(--offlineQuizCount));
        }
    }

    private void displayQuiz(QuizData quiz) {
        System.out.println("In display: " + quiz);
        currentQuiz = quiz;
        categoryLabel.setText(quiz.category().name());
        questionLabel.setText("<html><div style='text-align: center;'>" + quiz.question() + "</div></html>");
        answerGroup = new ButtonGroup();

        if (connected) quizSaver.appendOne(quiz);

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

    private void handleSubmit(ActionEvent e) {
        if (currentQuiz == null || answerGroup.getSelection() == null) {
            jokeLabel.setText("Please select an answer first.");
            return;
        }

        String selected = answerGroup.getSelection().getActionCommand();
        boolean correct = selected.equals(currentQuiz.correctAnswer());
        jokeLabel.setText(correct ? "Correct! Fetching a joke..." : "Oops! Let's lighten the mood...");
        scoreTracker.recordAttempt(correct);
        scoreLabel.setText(scoreTracker.toString());

        if (connected) {
            jokeFetcher.fetch(JokeFetcher::fetchJoke, joke -> {
                jokeLabel.setText(joke.getFormattedJoke());
                if (!joke.delivery().equals("Couldn't fetch joke!")) jokeSaver.appendOne(joke);
            });
        } else {
            jokeLabel.setText(offlineJokes.get(--offlineJokeCount).getFormattedJoke());
        }
    }

    private void checkInternetConnectionAsync() {
        networkButton.setText("Checking...");
        networkButton.setBackground(Color.ORANGE);
        networkButton.setSelected(true);

        new Thread(() -> {
            connected = InternetChecker.hasInternet();
            SwingUtilities.invokeLater(() -> {
                if (connected) {
                    networkButton.setText("Online");
                    networkButton.setBackground(new Color(76, 175, 80));
                    networkButton.setForeground(Color.WHITE);
                    networkButton.setSelected(true);
                } else {
                    networkButton.setText("Offline");
                    networkButton.setBackground(Color.RED);
                    networkButton.setForeground(Color.WHITE);
                    networkButton.setSelected(false);
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
                    if (history != null) history.printHistory(historyScores, 0);
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


    // Initialize the UI components and layout
    private void initializeUI() {
        setTitle("Who wants to be a Knowledge Bank?");
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                QuizJokeApp.class.getClassLoader().getResource("data/wwtbakbsmall.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(700, 500);

        // Main content panel with padding
        JPanel paddedContent = new JPanel(new BorderLayout(10, 10));
        paddedContent.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 153, 102)),
                new EmptyBorder(15, 15, 15, 15)));
        paddedContent.setBackground(new Color(255, 153, 204));
        setContentPane(paddedContent);

        // Center: Options panel for quiz answers
        optionsPanel = new JPanel(new GridLayout(4, 0));
        optionsPanel.setBackground(new Color(255, 255, 204));
        paddedContent.add(optionsPanel, BorderLayout.CENTER);

        // South: Buttons and joke label
        paddedContent.add(createSouthPanel(), BorderLayout.SOUTH);

        // North: Question label and network toggle
        paddedContent.add(createNorthPanel(), BorderLayout.NORTH);

        // East: Score, category, and history
        paddedContent.add(createEastPanel(), BorderLayout.EAST);

        setVisible(true);
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(255, 153, 204));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 153, 204));
        submitButton = createRoundedButton("Submit Answer");
        nextButton = createRoundedButton("Next Question");
        buttonPanel.add(submitButton);
        buttonPanel.add(nextButton);

        // Joke label
        jokeLabel = new JLabel("Answer to see your reward...", SwingConstants.CENTER);
        jokeLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        jokeLabel.setForeground(Color.DARK_GRAY);

        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(jokeLabel, BorderLayout.SOUTH);
        return southPanel;
    }

    private JPanel createNorthPanel() {
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(new Color(255, 153, 204));

        // Question label
        questionLabel = new JLabel("Loading question...", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        northPanel.add(questionLabel, BorderLayout.CENTER);

        // Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                QuizJokeApp.class.getClassLoader().getResource("data/wwtbakbsmall.png"))));
        northPanel.add(logoLabel, BorderLayout.WEST);

        // Network toggle button
        networkButton = new JToggleButton("ONLINE");
        networkButton.putClientProperty("JButton.buttonType", "switch");
        networkButton.setSelected(true);
        networkButton.setBackground(new Color(0, 200, 83));
        networkButton.setForeground(Color.WHITE);
        northPanel.add(networkButton, BorderLayout.EAST);

        return northPanel;
    }

    private JPanel createEastPanel() {
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setPreferredSize(new Dimension(200, 10));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        infoPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        infoPanel.setBackground(new Color(255, 255, 204));

        // Score label
        scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(160, 160, 160)),
                "Score", TitledBorder.RIGHT, TitledBorder.TOP, null, new Color(255, 51, 204)));
        infoPanel.add(scoreLabel);

        // Category label
        categoryLabel = new JLabel("", SwingConstants.CENTER);
        categoryLabel.setForeground(new Color(0, 51, 102));
        categoryLabel.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(160, 160, 160)),
                "Category", TitledBorder.RIGHT, TitledBorder.TOP, null, new Color(255, 51, 204)));
        infoPanel.add(categoryLabel);

        // History text area
        historyScores = new JTextArea("");
        historyScores.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(160, 160, 160)),
                "History", TitledBorder.RIGHT, TitledBorder.TOP, null, new Color(255, 51, 204)));
        historyScores.setFont(new Font("Tahoma", Font.PLAIN, 10));
        historyScores.setLineWrap(true);
        historyScores.setWrapStyleWord(true);
        historyScores.setBackground(new Color(255, 255, 204));
        JScrollPane scrollPane = new JScrollPane(historyScores);
        infoPanel.add(scrollPane);

        eastPanel.add(infoPanel, BorderLayout.CENTER);
        return eastPanel;
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 40));
        return button;
    }

    

}