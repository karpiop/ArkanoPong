package arkanopong;

import javax.swing.*;

public class ArkanopongClient {

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException {
        Lobby myLobby = new Lobby();
        myLobby.setSize(300, 200);
        myLobby.setVisible(true);
        while (myLobby.getText().equals("")) {
            Thread.sleep(1000);
        }
        Client client;
        do {
            client = new Client(myLobby.getText());
        } while(!client.isOpenedProperly());
        myLobby.Close();
        JFrame window = new JFrame();
        window.add(client);
        client.createWindow(window);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setResizable(false);
        window.setTitle("Arkanopong");
        window.setBounds(10, 10, Game.getBoardSize() + 200, Game.getBoardSize() + 30);
        window.getContentPane().add(client);
        new Thread(client::painterThread).start();
        client.readFromSocket();
    }

}

