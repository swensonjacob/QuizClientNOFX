package quiz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static quiz.SwingSetup.*;


public class Gui extends JFrame implements ActionListener {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private List<JButton> answerButtons;
    private List<JButton> categoryButtons;

    private JLabel infoText = createLabel("Väntar på att en motståndare ska ansluta...");
    private JLabel questionText = createLabel("questiontext");
    private JLabel roundTextLabel = createLabel("Runda 0");
    private JLabel winOrLoseLabel = createLabel("Lika");
    private JLabel roundPointLabel = createPointLabel("0 - 0");
    private JLabel totalPointLabel = createPointLabel("0 - 0");
    private ServerHandler serverHandler;
    private Question currentQuestion;
    private JButton giveUpButton = createButton("Ge upp och avsluta");


    public Gui(ServerHandler serverHandler) {

        this.serverHandler = serverHandler;

        answerButtons = Arrays.asList(createButton("answer"),createButton("answer"),createButton("answer"),createButton("answer"));
        categoryButtons = Arrays.asList(createButton("category"),createButton("category"),createButton("category"),createButton("category"));

        //mainPanel
        JPanel infoPanel = createPanel();
        JPanel game = createPanel();
        JPanel category = createPanel();
        JPanel roundPoint = createPanel();
        JPanel totalPoint = createPanel();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(infoPanel, "info");
        mainPanel.add(game, "game");
        mainPanel.add(category,"category");
        mainPanel.add(roundPoint, "roundPoint");
        mainPanel.add(totalPoint,"totalPoint");

        //loadingPanel
        JPanel loadingPanel = createPanel();
        loadingPanel.setLayout(new GridLayout(2,1));
        loadingPanel.add(getLoaderLabel());
        infoText.setVerticalAlignment(JLabel.TOP);
        loadingPanel.add(infoText);

        //InfoPanel
        infoPanel.setLayout(new BorderLayout());
        infoPanel.add(createLogo(),BorderLayout.NORTH);
        infoPanel.add(loadingPanel,BorderLayout.CENTER);

        //gameButtonsPanel
        JPanel gameButtonsPanel = createPanel();
        gameButtonsPanel.setLayout(new GridLayout(2,2,10,10));
        gameButtonsPanel.setBorder(getEmptyBorder());

        answerButtons.forEach(button -> {
            gameButtonsPanel.add(button);
            button.addActionListener(this);
        });

        //gameTopPanel
        JPanel gameTopPanel = createPanel();
        gameTopPanel.setLayout(new BorderLayout());
        gameTopPanel.add(giveUpButton, BorderLayout.NORTH);
        gameTopPanel.add(createLogo(), BorderLayout.SOUTH);
        giveUpButton.addActionListener(this::actionPerformed);
        giveUpButton.setPreferredSize(new Dimension(30,25));

        //gamePanel
        questionText.setVerticalAlignment(JLabel.CENTER);
        game.setLayout(new BorderLayout());

        game.add(gameButtonsPanel, BorderLayout.SOUTH);
        game.add(questionText, BorderLayout.CENTER);
        game.add(gameTopPanel, BorderLayout.NORTH);




        //categoryButtonsPanel
        JPanel categoryButtonsPanel = createPanel();
        categoryButtonsPanel.setLayout(new GridLayout(2,2,10,10));
        categoryButtonsPanel.setBorder(getEmptyBorder());


        categoryButtons.forEach(button -> {
            categoryButtonsPanel.add(button);
            button.addActionListener(this);
        });

        //categoryPanel
        JLabel categorytext = createLabel("Välj kategori");
        categorytext.setVerticalAlignment(JLabel.CENTER);
        category.setLayout(new BorderLayout());
        category.add(createLogo(),BorderLayout.NORTH);
        category.add(categorytext,BorderLayout.CENTER);
        category.add(categoryButtonsPanel,BorderLayout.SOUTH);

        //RoundPointPanel
        JPanel scorePanel = createPanel();
        JPanel playerPanel = createPanel();
        playerPanel.setLayout(new GridLayout(1,2));
        playerPanel.add(createLabel("Din Poäng"));
        playerPanel.add(createLabel("Motståndarens Poäng"));
        scorePanel.setLayout(new BoxLayout(scorePanel,BoxLayout.Y_AXIS));
        roundTextLabel.setBorder(new EmptyBorder(10, 10, 20, 10));
        scorePanel.add(roundTextLabel);
        scorePanel.add(playerPanel);
        scorePanel.add(roundPointLabel);
        roundPoint.setLayout(new BorderLayout());
        roundPoint.add(createLogo(),BorderLayout.NORTH);
        roundPoint.add(scorePanel,BorderLayout.CENTER);

        //totalPointPanel
        JPanel scorePanel2 = createPanel();
        JPanel playerPanel2 = createPanel();
        playerPanel2.setLayout(new GridLayout(1,2));
        playerPanel2.add(createLabel("Din Poäng"));
        playerPanel2.add(createLabel("Motståndarens Poäng"));
        scorePanel2.setLayout(new BoxLayout(scorePanel2,BoxLayout.Y_AXIS));
        JLabel totalTextLabel = createLabel("Total Poäng");
        scorePanel2.add(winOrLoseLabel);
        scorePanel2.add(totalTextLabel);
        scorePanel2.add(playerPanel2);
        scorePanel2.add(totalPointLabel);
        totalPoint.setLayout(new BorderLayout());
        totalPoint.add(createLogo(),BorderLayout.NORTH);
        totalPoint.add(scorePanel2,BorderLayout.CENTER);

        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setVisible(true);
        setSize(new Dimension(560,640));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {

            for (JButton categoryButton:categoryButtons ) {
                if (e.getSource() == categoryButton) {
                    categoryButton.setBackground(new Color(198, 187, 171));
                    serverHandler.sendCategory(categoryButton.getText());
                }
            }
                for (JButton answerButton:answerButtons ) {
                    if (e.getSource() == answerButton) {
                        if(!answerButton.getText().equals(currentQuestion.getAnswerCorrect())) {
                            answerButton.setBackground(new Color(255, 133, 140));
                        }
                    isCorrectAnswer(answerButton);
                }
                if (answerButton.getText().equals(currentQuestion.getAnswerCorrect())) {
                    answerButton.setBackground(new Color(148, 255, 170));
                }
            }
            if (e.getSource() == giveUpButton) {
                serverHandler.writeStringToServer("GETTUPP");
                System.exit(0);


            }
        } catch (Exception ea) {
            System.out.println(ea.getMessage());
        }
    }

    public void isCorrectAnswer(JButton b) {
        if (b.getText().equals(currentQuestion.getAnswerCorrect())) {
            serverHandler.writeStringToServer( currentQuestion.getAnswerCorrect());
        } else {
            serverHandler.writeStringToServer("wrong");
        }
    }

    public void setQuestionPanel() {
        Collections.shuffle(answerButtons);
        questionText.setText(currentQuestion.getQuestionText());
        answerButtons.get(0).setText(currentQuestion.getAnswerOne());
        answerButtons.get(1).setText(currentQuestion.getAnswerTwo());
        answerButtons.get(2).setText(currentQuestion.getAnswerThree());
        answerButtons.get(3).setText(currentQuestion.getAnswerCorrect());
        cardLayout.show(mainPanel, "game");
    }

    public void setCategoryPanel(List<CategoryName> categoryList) {

        for (int i = 0; i < categoryButtons.size(); i++) {
            categoryButtons.get(i).setText(capitalize(categoryList.get(i).toString().toLowerCase()));
        }
        cardLayout.show(mainPanel, "category");
        resetButtonBackground(categoryButtons);
    }

    public void setInfoPanel(String text) {
        infoText.setText(text);
        cardLayout.show(mainPanel, "info");
    }

    public void setRoundPointPanel(ScoreBoard scoreBoard) {
        roundTextLabel.setText("Runda " + scoreBoard.getCurrentRound());
        roundPointLabel.setText(scoreBoard.getPlayerScore().get(0) + " - " + scoreBoard.getOpponentScore().get(0));
        cardLayout.show(mainPanel,"roundPoint");
    }

    public void setTotalPointPanel(ScoreBoard scoreBoard) {
        totalPointLabel.setText(scoreBoard.getTotalScorePlayer() + " - " + scoreBoard.getTotalScoreOpponent());
        if (scoreBoard.getTotalScorePlayer()>scoreBoard.getTotalScoreOpponent()) {
            winOrLoseLabel.setText("Du vann!");
        } else if(scoreBoard.getTotalScorePlayer()<scoreBoard.getTotalScoreOpponent()){
            winOrLoseLabel.setText("Du Förlorade");
        }
        cardLayout.show(mainPanel,"totalPoint");
    }

    public void resetButtonBackground(List<JButton> buttons) {
        for (JButton button: buttons) {
            button.setBackground(Color.WHITE);
        }
    }

    public void updateQuestion(Question question) {
        this.currentQuestion=question;
        resetButtonBackground(this.answerButtons);
        setQuestionPanel();

    }
    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
