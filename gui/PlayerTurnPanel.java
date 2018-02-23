package part2.gui;

import part2.model.Board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

public class PlayerTurnPanel extends JPanel {

    private final JButton player1;
    private final JButton player2;
    private final String nameForPlayer1;
    private final String nameForPlayer2;
    private final Board chessboard;

    //constructor
    public PlayerTurnPanel(Board cb) {
        this.chessboard = cb;

        nameForPlayer1 = generateRandomName();
        nameForPlayer2 = generateRandomName();

        UIManager.put("Button.disabledText", Color.black);
        player1 = new JButton("Black: " + nameForPlayer1);
        player2 = new JButton("White: " + nameForPlayer2);
        player1.setOpaque(true);
        player2.setOpaque(true);
        player1.setPreferredSize(new Dimension(120, 60));
        player2.setPreferredSize(new Dimension(120, 60));
        player1.setEnabled(false);
        player2.setEnabled(false);

        this.setLayout(new GridLayout(2, 1));

        this.add(player1);
        this.add(player2);
    }

    void setTurn()
    {
        if (chessboard.isBlackTurn()) {
            player1.setBackground(new Color(165, 121, 105));
            player2.setBackground(new Color(255, 253, 235));
        } else {
            player1.setBackground(new Color(255, 253, 235));
            player2.setBackground(new Color(165, 121, 105));
        }
    }

    public void setName(String name1, String name2) {
        player1.setText("Black: " + name1);
        player2.setText("White: " + name2);
    }

    //generate random unique name for the two players
    public String generateRandomName() {
        return randomString("abcdefghijklmnopqrstuvwxyz", 4);
    }

    /*
     * generate a random and unique string at certain length
     * @param charList list of characters that can be used
     * @param strLength the desired length of the randomly generated string
     * @return the random string generated
     */
    public String randomString(String charList, int strLength) {
        Random rand = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < strLength; i++) {
            str.append(charList.charAt(rand.nextInt(charList.length())));
        }
        return str.toString();
    }
}
