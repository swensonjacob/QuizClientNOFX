package quiz;

public class Main {

    public static void main(String[] args) {

        ServerHandler serverHandler = new ServerHandler();
        Gui gui = new Gui(serverHandler);
        serverHandler.setGui(gui);

        new Thread(serverHandler).start();
    }
}

