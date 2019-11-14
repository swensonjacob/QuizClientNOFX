package quiz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class QuestionGetter implements Runnable{

    @Override
    public void run() {

        try(Socket socket = new Socket("localhost",5989)) {

            ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream serverReader = new ObjectInputStream(socket.getInputStream());

            String fromUser;
            BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));


            Object serverInput;

            while ((serverInput = serverReader.readObject()) != null) {

                if (serverInput instanceof Question) {
                    Question question = (Question) serverInput;
                    System.out.println(question.getQuestionText());
                    System.out.println(question.getAnswerOne());
                    System.out.println(question.getAnswerTwo());
                    System.out.println(question.getAnswerThree());
                    System.out.println(question.getAnswerCorrect());
                    fromUser = userReader.readLine();
                    if (fromUser != null) {
                        writer.writeObject(fromUser);
                    }
                } else if (serverInput instanceof String) {
                    System.out.println(serverInput);

            } else if (serverInput instanceof CategoryChooser) {

                    CategoryChooser chooser = (CategoryChooser) serverInput;
                    System.out.println(chooser.getChooserMessage());
                    for (int i = 0; i <chooser.getEnumList().size() ; i++) {
                        System.out.println(chooser.getEnumList().get(i));
                    }
                    fromUser = userReader.readLine();
                    if (fromUser.equalsIgnoreCase("animal")) {
                        chooser.setChoosedCategory(CategoryName.ANIMAL);
                    } else if (fromUser.equalsIgnoreCase("furniture")) {
                        chooser.setChoosedCategory(CategoryName.FURNITURE);
                    }
                    writer.writeObject(chooser);
            }

            }
        } catch (Exception e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
