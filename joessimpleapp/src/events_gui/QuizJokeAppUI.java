package events_gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Handles the UI setup and component management for the QuizJokeApp.
 */
public class QuizJokeAppUI extends JFrame {
    // UI Components
    private final JLabel questionLabel;
    private final JPanel optionsPanel;
    private final JButton submitButton;
    private final JButton nextButton;
    private final JLabel jokeLabel;
    private final JToggleButton networkButton;
    private final JLabel scoreLabel;
    private final JLabel categoryLabel;
    private final JTextArea historyScores;

    public QuizJokeAppUI() {
        questionLabel = new JLabel("Loading question...", SwingConstants.CENTER);
        optionsPanel = new JPanel(new GridLayout(4, 0));
        submitButton = createRoundedButton("Submit Answer");
        nextButton = createRoundedButton("Next Question");
        jokeLabel = new JLabel("Answer to see your reward...", SwingConstants.CENTER);
        networkButton = new JToggleButton("ONLINE");
        scoreLabel = new JLabel("", SwingConstants.CENTER);
        categoryLabel = new JLabel("", SwingConstants.CENTER);
        historyScores = new JTextArea("");

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Who wants to be a Knowledge Bank?");
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                QuizJokeAppUI.class.getClassLoader().getResource("data/wwtbakbsmall.png")));
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
        buttonPanel.add(submitButton);
        buttonPanel.add(nextButton);

        // Joke label
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
        questionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        northPanel.add(questionLabel, BorderLayout.CENTER);

        // Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                QuizJokeAppUI.class.getClassLoader().getResource("data/wwtbakbsmall.png"))));
        northPanel.add(logoLabel, BorderLayout.WEST);

        // Network toggle button
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
        scoreLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(160, 160, 160)),
                "Score", TitledBorder.RIGHT, TitledBorder.TOP, null, new Color(255, 51, 204)));
        infoPanel.add(scoreLabel);

        // Category label
        categoryLabel.setForeground(new Color(0, 51, 102));
        categoryLabel.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(160, 160, 160)),
                "Category", TitledBorder.RIGHT, TitledBorder.TOP, null, new Color(255, 51, 204)));
        infoPanel.add(categoryLabel);

        // History text area
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

    // Getters for UI components
    public JLabel getQuestionLabel() {
        return questionLabel;
    }

    public JPanel getOptionsPanel() {
        return optionsPanel;
    }

    public JButton getSubmitButton() {
        return submitButton;
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public JLabel getJokeLabel() {
        return jokeLabel;
    }

    public JToggleButton getNetworkButton() {
        return networkButton;
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }

    public JLabel getCategoryLabel() {
        return categoryLabel;
    }

    public JTextArea getHistoryScores() {
        return historyScores;
    }
}