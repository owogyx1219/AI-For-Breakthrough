package part2.model;

import part2.gui.Main;

public class Board {
    private final int width, height;
    private final Piece[][] board;
    private final int pieceToStop;
    private final Main GUI;
    private int white_remaining;
    private int black_remaining;
    private int max_moved_distances_in_row_white = 0;
    private int max_moved_distances_in_row_black = 0;
    private int total_number_of_piece = 32;

    private boolean _isBlackTurn = true;
    private boolean isBlackTurn = true;
    private boolean gameEnd = false;


    private int WinValue = 500000;
    private int PieceAlmostWinValue = 10000;
    private int PieceValue = 1300;
    private int PieceDangerValue = 10;
    private int PieceHighDangerValue = 100;
    private int PieceAttackValue = 50;
    private int PieceProtectionValue = 65;
    private int PieceConnectionHValue = 35;
    private int PieceConnectionVValue = 15;
    private int PieceColumnHoleValue = 20;
    private int PieceHomeGroundValue = 10;

    public Board(int width, int height, boolean isBlackTurn, int pieceToStop, Main GUI) {
        this.width = width;
        this.height = height;
        this.board = new Piece[height][width];
        this.pieceToStop = pieceToStop;
        this._isBlackTurn = isBlackTurn;
        this.GUI = GUI;
        this.white_remaining = 16;
        this.black_remaining = 16;
        init();
    }

    public Board(Board other, Main GUI)
    {
        this.width = other.width;
        this.height = other.height;
        this.pieceToStop = other.pieceToStop;
        this.GUI = GUI;
        this.white_remaining = other.white_remaining;
        this.black_remaining = other.black_remaining;
        this._isBlackTurn = other._isBlackTurn;
        this.isBlackTurn = other.isBlackTurn;
        this.gameEnd = other.gameEnd;
        this.board = new Piece[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (other.board[row][col] != null)
                    this.board[row][col] = new Piece(other.board[row][col], this);
            }
        }
    }

    private void init()
    {
        this.white_remaining = this.black_remaining = 2 * width;
        this.isBlackTurn = this._isBlackTurn;
        this.gameEnd = false;
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++)
                board[row][col] = null;
        for (int col = 0; col < width; col++) {
            initPiece(new Piece(true, 0, col, this));
            initPiece(new Piece(true, 1, col, this));
            initPiece(new Piece(false, height - 2, col, this));
            initPiece(new Piece(false, height - 1, col, this));
        }
    }

    public void reset()
    {
        init();
        if (GUI != null)
            GUI.render();
    }

    public int getEvaluationValue(boolean usePieceValue, boolean playerIsBlack) {
        int Value = 0;

        // evaluate the state of the game
        boolean WhiteWins = false;
        boolean BlackWins = false;

        if(playerIsBlack)
        {
            // scan all squares
            for (int column = 0; column < width; column++) {
                int BlackPiecesOnColumn = 0;
                int WhitePiecesOnColumn = 0;

                for (int row = 0; row < height; row++) {
                    Piece currentPiece = getPiece(row, column);

                    if (currentPiece != null) {
                        if (currentPiece.isBlack() == true) {

                            BlackPiecesOnColumn++;
                            if(usePieceValue)
                            {
                                Value += getPieceValue(row, column);
                            }

                            if (row == height - 1) {
                                BlackWins = true;
                            } else if (row == height - 2) {
                                boolean ThreatA = false;
                                boolean ThreatB = false;
                                if (column > 0) {
                                    ThreatA = (getPiece(height - 1, column - 1) == null);
                                }
                                if (column < width - 1) {
                                    ThreatB = (getPiece(height - 1, column + 1) == null);
                                }

                                if ((!ThreatA) && (!ThreatB)) {
                                    Value += PieceAlmostWinValue;
                                }
                            } else if (row == 0) {
                                Value += PieceHomeGroundValue;
                            }
                        } else {

                            WhitePiecesOnColumn++;
                            if(usePieceValue)
                            {
                                Value -= getPieceValue(row, column);
                            }

                            if (row == 0) {
                                WhiteWins = true;
                            } else if (row == 1) {
                                boolean ThreatA = false;
                                boolean ThreatB = false;
                                if (column > 0) {
                                    ThreatA = (getPiece(0, column - 1) == null);
                                }
                                if (column < width - 1) {
                                    ThreatB = (getPiece(0, column + 1) == null);
                                }

                                if ((!ThreatA) && (!ThreatB)) {
                                    Value -= PieceAlmostWinValue;
                                }
                            } else if (row == height - 1) {
                                Value -= PieceHomeGroundValue;
                            }
                        }
                    }

                    if (BlackPiecesOnColumn == 0) Value -= PieceColumnHoleValue;
                    if (WhitePiecesOnColumn == 0) Value += PieceColumnHoleValue;

                    // if no more material available
                    if (getWhiteRemaining() == 0) BlackWins = true;
                    if (getBlackRemaining() == 0) WhiteWins = true;

                    // winning positions
                    if (BlackWins)
                    {
                        Value += WinValue;
                    }
                    if (WhiteWins)
                    {
                        Value -= WinValue;
                    }
                }
            }
        }
        else
        {
            // scan all squares
            for (int column = 0; column < width; column++) {
                int BlackPiecesOnColumn = 0;
                int WhitePiecesOnColumn = 0;

                for (int row = 0; row < height; row++) {
                    Piece currentPiece = getPiece(row, column);

                    if (currentPiece != null) {
                        if (currentPiece.isBlack() == true) {

                            BlackPiecesOnColumn++;
                            if(usePieceValue)
                            {
                                Value -= getPieceValue(row, column);
                            }

                            if (row == height - 1) {
                                BlackWins = true;
                            } else if (row == height - 2) {
                                boolean ThreatA = false;
                                boolean ThreatB = false;
                                if (column > 0) {
                                    ThreatA = (getPiece(height - 1, column - 1) == null);
                                }
                                if (column < width - 1) {
                                    ThreatB = (getPiece(height - 1, column + 1) == null);
                                }

                                if ((!ThreatA) && (!ThreatB)) {
                                    Value -= PieceAlmostWinValue;
                                }
                            } else if (row == 0) {
                                Value -= PieceHomeGroundValue;
                            }
                        } else {

                            WhitePiecesOnColumn++;
                            if(usePieceValue)
                            {
                                Value += getPieceValue(row, column);
                            }

                            if (row == 0) {
                                WhiteWins = true;
                            } else if (row == 1) {
                                boolean ThreatA = false;
                                boolean ThreatB = false;
                                if (column > 0) {
                                    ThreatA = (getPiece(0, column - 1) == null);
                                }
                                if (column < width - 1) {
                                    ThreatB = (getPiece(0, column + 1) == null);
                                }

                                if ((!ThreatA) && (!ThreatB)) {
                                    Value += PieceAlmostWinValue;
                                }
                            } else if (row == height - 1) {
                                Value += PieceHomeGroundValue;
                            }
                        }
                    }

                    if (BlackPiecesOnColumn == 0) Value += PieceColumnHoleValue;
                    if (WhitePiecesOnColumn == 0) Value -= PieceColumnHoleValue;

                    // if no more material available
                    if (getWhiteRemaining() == 0) BlackWins = true;
                    if (getBlackRemaining() == 0) WhiteWins = true;

                    // winning positions
                    if (BlackWins)
                    {
                        Value -= WinValue;
                    }
                    if (WhiteWins)
                    {
                        Value += WinValue;
                    }
                }
            }
        }

        return Value;
    }

    public int getPieceValue(int row, int column)
    {
        int Value = PieceValue;
        Piece currentPiece = getPiece(row, column);

        // add connections value
        if (currentPiece.ConnectH)
        {
            Value += PieceConnectionHValue;
        }
        if (currentPiece.ConnectV)
        {
            Value += PieceConnectionVValue;
        }

        // add to the value the protected value
        Value += currentPiece.ProtectedValue;

        // evaluate attack
        if(currentPiece.AttackedValue > 0)
        {
            Value -= currentPiece.AttackedValue;
            if (currentPiece.ProtectedValue == 0)
                Value -= currentPiece.AttackedValue;
        }
        else
        {
            if (currentPiece.ProtectedValue != 0)
            {
                // pawns at the end that are not attacked are worth more points
                if (currentPiece.isBlack())
                {
                    if (row == height - 3) Value += PieceDangerValue;
                    else if (row == height - 2) Value += PieceHighDangerValue;
                }
                else
                {
                    if (row == 2) Value += PieceDangerValue;
                    else if (row == 1) Value += PieceHighDangerValue;
                }
            }
        }

        // danger value
        if (currentPiece.isBlack())
            Value += row * PieceDangerValue;
        else
            Value += (height - row) * PieceDangerValue;

        return Value;
    }


    public Piece getPiece(int row, int col) throws IndexOutOfBoundsException {
        if (row >= 0 && row < height && col >= 0 && col < width)
            return board[row][col];
        else
            throw new IndexOutOfBoundsException("Index out of bound.");
    }

    public int getBlackRemaining() {
        return black_remaining;
    }

    public int getWhiteRemaining() {
        return white_remaining;
    }


    public int getMaxBlacksMovedDistanceInRow() {
        return max_moved_distances_in_row_black;
    }

    public int getMaxWhitesMovedDistanceInRow() {
        return max_moved_distances_in_row_white;
    }

    public int getTotalNumberOfPieces() { return total_number_of_piece; }

    public boolean isGameEnd() {
        return gameEnd;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isBlackTurn() {
        return isBlackTurn;
    }

    public boolean validMove(int srcRow, int srcCol, int destRow, int destCol) throws IndexOutOfBoundsException {
        Piece srcPiece = getPiece(srcRow, srcCol);
        Piece destPiece = getPiece(destRow, destCol);
        if (srcPiece == null)
            return false;
        if(srcPiece.isBlack() != this.isBlackTurn())
            return false;
//        if (destPiece != null && destPiece.isBlack() == srcPiece.isBlack())
//            return false;
        return srcPiece.EffectiveMove(destRow, destCol);
    }

    public boolean isBlackWin()
    {
        if (gameEnd && !isBlackTurn)
            return true;
        if (white_remaining < pieceToStop)
            return true;
        int blackCount = 0;
        for (int i = 0; i < width; i++)
        {
            if (getPiece(height - 1, i) != null && getPiece(height - 1, i).isBlack())
                blackCount++;
        }
        if (blackCount >= pieceToStop)
            return true;

        return false;
    }

    public boolean isWhiteWin()
    {
        if (gameEnd && isBlackTurn)
            return true;
        if (black_remaining < pieceToStop)
            return true;
        int whiteCount = 0;
        for (int i = 0; i < width; i++)
        {
            if (getPiece(0, i) != null && !getPiece(0, i).isBlack())
                whiteCount++;
        }
        if (whiteCount >= pieceToStop)
            return true;

        return false;
    }

    public void movePiece(int srcRow, int srcCol, int destRow, int destCol) throws Exception {
        Piece srcPiece = getPiece(srcRow, srcCol);
        Piece destPiece = getPiece(destRow, destCol);
        if(srcPiece.isBlack())
        {
            srcPiece.movedDistancesInRow += Math.abs(srcRow-destRow);
        }
        if(!srcPiece.isBlack())
        {
            srcPiece.movedDistancesInRow += Math.abs(srcRow-destRow);
        }

        if(srcPiece.isBlack() && srcPiece.movedDistancesInRow > max_moved_distances_in_row_black)
        {
            max_moved_distances_in_row_black = srcPiece.movedDistancesInRow;
        }
        else if(!srcPiece.isBlack() && srcPiece.movedDistancesInRow > max_moved_distances_in_row_white)
        {
            max_moved_distances_in_row_white = srcPiece.movedDistancesInRow;
        }


        if (validMove(srcRow, srcCol, destRow, destCol)) {

            if(destCol > 1)
            {
                Piece LeftPiece = getPiece(destRow, destCol-1);
                if(LeftPiece != null)
                {
                    if(LeftPiece.isBlack() == srcPiece.isBlack())
                    {
                        LeftPiece.ConnectH = true;
                        srcPiece.ConnectH = true;
                    }
                }
            }

            if(destCol < 7)
            {
                Piece RightPiece = getPiece(destRow, destCol+1);
                if(RightPiece != null)
                {
                    if(RightPiece.isBlack() == srcPiece.isBlack())
                    {
                        RightPiece.ConnectH = true;
                        srcPiece.ConnectH = true;
                    }
                }
            }


            if (destPiece != null) {
                total_number_of_piece--;
                if (isBlackTurn)
                    white_remaining--;
                else
                    black_remaining--;
            }
            srcPiece.setPosition(destRow, destCol);
            board[destRow][destCol] = srcPiece;
            board[srcRow][srcCol] = null;
            isBlackTurn = !isBlackTurn;
            if (GUI != null)
                GUI.render();
            if (isBlackWin() || isWhiteWin()) {
                gameEnd = true;
                if (GUI != null)
                    GUI.gameEnd();
            }
        } else {
            throw new Exception("Not a valid move!");
        }
    }

    private void initPiece(Piece p) {
        board[p.getRowPosition()][p.getColPosition()] = p;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                if (board[i][j] == null)
                    ret.append('.');
                else if (board[i][j].isBlack())
                    ret.append('B');
                else
                    ret.append('W');
            }
            ret.append('\n');
        }
        ret.append("White remaining: " + white_remaining + "\n");
        ret.append("Black remaining: " + black_remaining + "\n");
        ret.append("White wins? " + isWhiteWin() + "\n");
        ret.append("Black wins? " + isBlackWin() + "\n");
        return ret.toString();
    }
}
