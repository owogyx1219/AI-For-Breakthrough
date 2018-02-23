package part2.gui;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import part2.model.Piece;


public class TileButton extends JButton {

    private int row;
    private int col;
    private Piece piece;
    private boolean backgroundIsBlack;

    //constructor for TileButton
    public TileButton(int row, int col, Piece piece, boolean backgroundIsBlack) {
        this.row = row;
        this.col = col;
        this.piece = piece;
        this.backgroundIsBlack = backgroundIsBlack;

        //draw background color for current button
        drawBackgroundTile();
        setOpaque(true);
        setBorderPainted(false);
        setIcon(getIcon());

        validate();
    }

    public void setPiece(Piece p) {
        this.piece = p;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public Piece getPieceFromTileButton() {
        return this.piece;
    }

    public ImageIcon getIcon() {
        try {
            String filename = getFileName();
            Image im = new ImageIcon(this.getClass().getResource("../Images/" + filename + ".png")).getImage();
            return new ImageIcon(im);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    private String getFileName() throws NullPointerException {
        if (piece == null)
            throw new NullPointerException("Empty cell.");

        String filename = "";
        String chessColor = "";

        if (piece.isBlack()) {
            chessColor = "black";
        } else {
            chessColor = "white";
        }
        filename = "Pawn" + "_" + chessColor;

        return filename;
    }

    public void drawBackgroundTile() {
        if (backgroundIsBlack) {
            setBackground(new Color(201, 142, 80));
        } else {
            setBackground(new Color(249, 207, 142));
        }
    }
}
