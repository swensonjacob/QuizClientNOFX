package quiz;

public class Main {

    public static void main(String[] args) {
        Thread t = new Thread(new QuestionGetter());
        t.start();
    }
}

