/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arkanopong;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Marta
 */
public class Lobby extends Frame implements ActionListener {
    String IPStr = "";
    JLabel label;
    JTextField IP;
    JButton connect;
    JPanel panel;

    public Lobby() {
        super("Lobby");
        //JFrame lobby = new JFrame("Lobby");
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        label = new JLabel("Podaj adres IP");
        IP = new JTextField(15);
        connect = new JButton();
        connect.setText("Connect");
        connect.addActionListener(this);
        panel.add(label);
        panel.add(connect);
        panel.add(IP);
        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        IPStr = IP.getText();
        //this.setVisible(false);
    }

    public String getText() {
        return IPStr;
    }

    public void Close() {
        dispose();
    }


}
