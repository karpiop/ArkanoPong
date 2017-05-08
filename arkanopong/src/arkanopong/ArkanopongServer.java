/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arkanopong;

import javax.swing.JFrame;
import java.io.IOException;

public class ArkanopongServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        Game game = new Game();

        try {
            game.play();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
