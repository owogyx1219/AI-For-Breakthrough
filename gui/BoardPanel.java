package part2.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import part2.model.*;

public class BoardPanel extends JPanel {

    private final Board chessboard;
    private final int width, height;
    private final TileButton[][] tiles;

    private TileButton selectedButtonPrev = null;
    private boolean backgroundIsBlack = false;

    public BoardPanel(Board cb) {
        super(new GridLayout(cb.getHeight(), cb.getWidth()));
        this.chessboard = cb;
        this.width = chessboard.getWidth();
        this.height = chessboard.getHeight();
        this.tiles = new TileButton[height][width];

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(60 * width, 60 * height));
        setLocation(0, 0);
        setUpPanel();
        validate();
    }

    public void setUpPanel() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Piece piece = chessboard.getPiece(row, col);

                TileButton tb = new TileButton(row, col, piece, (row + col) % 2 != 0);
                tb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectionButtonPressed(tb);
                    }
                });
                tiles[row][col] = tb;
                this.add(tiles[row][col]);
            }
        }
    }

    void render()
    {
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                tiles[i][j].setPiece(chessboard.getPiece(i, j));
                tiles[i][j].setIcon(tiles[i][j].getIcon());
                tiles[i][j].drawBackgroundTile();
            }
        }
    }

    /*
     * this function get executed when player presses any TileButton
     * @param selectedButtonCurr The TileButton has been selected as the piece to be moved
     */
    protected void selectionButtonPressed(TileButton selectedButtonCurr) {

        Piece curr_piece = selectedButtonCurr.getPieceFromTileButton();
        //selected an empty chess piece as its the button to be moved,

        if (chessboard.isGameEnd())
            return;

        if (selectedButtonPrev == null && curr_piece == null)
            return;


        //selected a certain chess piece as the button to be moved
        if (selectedButtonPrev == null && curr_piece != null) {
            // moving a chess that doesn't belong to current player is not allowed
            if (chessboard.isBlackTurn() != curr_piece.isBlack()) {
                JOptionPane.showMessageDialog(null, "It's NOT your turn!");
                return;
            } else {
                selectedButtonPrev = selectedButtonCurr;
                selectedButtonPrev.setBackground(new Color(219, 129, 126));
            }

        } else if (selectedButtonPrev != null) {
            try {
                chessboard.movePiece(selectedButtonPrev.getRow(), selectedButtonPrev.getCol(),
                        selectedButtonCurr.getRow(), selectedButtonCurr.getCol());
                selectedButtonPrev = null;
            } catch (Exception msg) {
                selectedButtonPrev.drawBackgroundTile();
                selectedButtonPrev = null;
                JOptionPane.showMessageDialog(null, msg.getMessage());
            }
        }
    }
}


