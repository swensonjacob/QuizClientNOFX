package quiz;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerHandler implements Runnable{

    ObjectOutputStream writer;
    Gui gui;
    List<CategoryName> categoryList;
    Socket socket;

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    @Override
    public void run() {

        try {
            socket = new Socket("localhost",5989);
            writer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream serverReader = new ObjectInputStream(socket.getInputStream());

            String fromUser;
            BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));

            Object serverInput;

            while ((serverInput = serverReader.readObject()) != null) {

                if (serverInput instanceof Question) {
                    Thread.sleep(1500);
                    Question question = (Question) serverInput;
                    gui.updateQuestion(question);

                } else if (serverInput instanceof String) {
                    Thread.sleep(1500);
                    gui.setInfoPanel(serverInput.toString());
                    System.out.println("String received: "+serverInput);

                    /*den frågar om  spelaren vill spela igen eller inte.


                     */
                    if (((String) serverInput).equalsIgnoreCase("GETTUPP")){
                        gui.setInfoPanel("Du vann! Din motståndare har gett upp");
                        gui.revalidate();
                        gui.repaint();
                        Thread.sleep(10000);
                        System.exit(0);
                    }

                    if (((String) serverInput).contains("Lika")  ||((String) serverInput).contains("Du förlorade")){ // den som vinner får chansen bli frågad om hen vill förtäta spela eller inte.
                        //och om de är lika bådde blir frågade bestäma.

                        String userInput = JOptionPane.showInputDialog(null, "vill du spela igen?, svara ja eller nej");
                        if(userInput.equalsIgnoreCase("ja")){
                            gui.dispose();
                            Main.runner();
                        }else{
                            gui.dispose();
                        }




                    }if (((String)serverInput).equalsIgnoreCase("GETTUPP")){
                        gui.setInfoPanel("Du vann! Din motståndare har gett upp.");
                    }
            } else if (serverInput instanceof List<?>) {
                    categoryList = (List<CategoryName>) serverInput;
                    System.out.println("Välj kategori");
                    System.out.println(categoryList.get(0));
                    System.out.println(categoryList.get(1));
                    System.out.println(categoryList.get(2));
                    System.out.println(categoryList.get(3));
                    gui.setCategoryPanel(categoryList.get(0).toString(),categoryList.get(1).toString(),
                            categoryList.get(2).toString(),categoryList.get(3).toString());

            } else if (serverInput instanceof ScoreBoard) {
                    Thread.sleep(1500);
                    ScoreBoard scoreBoard = (ScoreBoard) serverInput;
                    gui.setRoundPointPanel();
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
    }public void disconnect() throws IOException {
        socket.close();
    }
}
