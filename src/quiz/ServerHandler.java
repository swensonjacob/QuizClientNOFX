package quiz;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerHandler implements Runnable{

    private ObjectOutputStream writer;
    private Gui gui;
    private List<CategoryName> categoryList;

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    @Override
    public void run() {

        try(Socket socket = new Socket("localhost",5989)) {

            writer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream serverReader = new ObjectInputStream(socket.getInputStream());

            Object serverInput;

            while ((serverInput = serverReader.readObject()) != null) {

                if (serverInput instanceof Question) {
                    Thread.sleep(500);
                    Question question = (Question) serverInput;
                    gui.updateQuestion(question);

                } else if (serverInput instanceof String) {
                    Thread.sleep(500);
                    gui.setInfoPanel(serverInput.toString());

                    if (((String) serverInput).equalsIgnoreCase("GETTUPP")){
                        gui.setInfoPanel("Du vann! Din motståndare har gett upp, ett nytt spel kommer att startas");
                        Thread.sleep(5000);
                        Main.runner();
                        gui.dispose();
                    }

            } else if (serverInput instanceof List<?>) {
                    categoryList = (List<CategoryName>) serverInput;
                    gui.setCategoryPanel(categoryList);

                } else if (serverInput instanceof ScoreBoard) {
                    ScoreBoard scoreBoard = (ScoreBoard) serverInput;
                    Thread.sleep(500);
                    if (scoreBoard.isLastRound()) {
                        gui.setTotalPointPanel(scoreBoard);

                        int result = JOptionPane.showConfirmDialog(new JFrame(),
                                "Vill du spela igen?", "tryck Ja eller Nej",
                                JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            Main.runner();
                        } else if (result == JOptionPane.NO_OPTION) {
                            System.exit(0);
                            gui.dispose();
                        }
                    } else {
                        gui.setRoundPointPanel(scoreBoard);
                    }
                    Thread.sleep(4000);
                }

            }
        } catch (Exception e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void writeStringToServer(String text) {
        try {
            writer.writeObject(text);
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void sendCategory(String category) {

        for (CategoryName categoryName:categoryList) {
            if (categoryName.toString().equalsIgnoreCase(category)) {
                try {
                    writer.writeObject(categoryName);
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }
        }
    }
}
