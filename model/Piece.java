package part2.model;


public class Piece {

    //true if the color of current piece is BLACK, false if the color of current piece is WHITE
    //assume player has the black chess, opponent is has the white chess
    private final boolean isBlack;
    //current row position of the piece
    private int row;
    //current column position of the piece
    private int col;
    private Board board;
    public int movedDistancesInRow;
    public int AttackedValue;
    public int ProtectedValue;
    public boolean ConnectH;
    public boolean ConnectV;

//    private int PieceAttackValue = 50;
//    private int PieceProtectionValue = 65;

    private int PieceAttackValue = 50;
    private int PieceProtectionValue = 65;

    public Piece(boolean isBlack, int row, int col, Board board) {
        this.isBlack = isBlack;
        this.row = row;
        this.col = col;
        this.board = board;
        movedDistancesInRow = 0;
        AttackedValue = 0;
        ProtectedValue = 0;
        ConnectH = false;
        ConnectV = false;
    }

    public Piece(Piece other, Board board) {
        this.isBlack = other.isBlack;
        this.row = other.row;
        this.col = other.col;
        this.board = board;
    }


    public boolean isBlack() {
        return isBlack;
    }

    public int getRowPosition() {
        return row;
    }

    public int getColPosition() {
        return col;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean WithinValidBoundary(int row, int col) {
        if (row >= 0 && row < board.getHeight() && col >= 0 && col < board.getWidth()) {
            return true;
        }
        return false;
    }


    /*
     * check if it's valid to move the pawn from current position to destination
     * @param destRow row index of the destination
     * @param destCol column index of the destination
     * @return true if it's a valid move, otherwise false
     */
    public boolean EffectiveMove(int destRow, int destCol) {
        int rowDistance = Math.abs(getRowPosition() - destRow);
        int colDistance = Math.abs(getColPosition() - destCol);
        boolean moveOneSquareDiagonal = (rowDistance == 1 && colDistance == 1);
        boolean moveOneSquareForward = (rowDistance == 1 && colDistance == 0);

        //if any index out of bound or its position stays the same, then it's not a valid move
        if (!WithinValidBoundary(destRow, destCol) || (row == destRow && col == destCol)) {
            return false;
        }

        //if chess move backward
        boolean blackChessMoveBackward = (isBlack && destRow < row);
        boolean whiteChessMoveBackward = (!isBlack && destRow > row);
        if (blackChessMoveBackward || whiteChessMoveBackward) {
            return false;
        }

        int rowDirection = isBlack ? 1 : -1;

        //move one step forward
        if (moveOneSquareForward) {
            Piece ForwardPiece = board.getPiece(row + 1 * rowDirection, col);
            boolean squareEmpty1 = (ForwardPiece == null);
            if (squareEmpty1) {
                return true;
            }
            else
            {
                if(ForwardPiece != null)
                {
                    if(ForwardPiece.isBlack() == this.isBlack()) {
                        ForwardPiece.ConnectV = true;
                        this.ConnectV = true;
                    }
                }
                return false;
            }
        }

        //move one step diagonally if there is a opponent piece at diagonal of distance 1
        boolean blackEatWhite = (isBlack && (destRow > row) && moveOneSquareDiagonal);
        boolean whiteEatBlack = (!isBlack && (destRow < row) && moveOneSquareDiagonal);
        if (blackEatWhite || whiteEatBlack) {
            Piece pieceOnDiagonal = board.getPiece(destRow, destCol);
            if (pieceOnDiagonal != null) {
                if (pieceOnDiagonal.isBlack() != isBlack) {
                    pieceOnDiagonal.AttackedValue += PieceAttackValue;
                    return true;
                }
                else
                {
                    pieceOnDiagonal.ProtectedValue += PieceProtectionValue;
                    return false;
                }
            } else {
                return true;
            }
        }

        return false;
    }
}
