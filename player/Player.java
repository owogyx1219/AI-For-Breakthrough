package part2.player;

import part2.model.Board;

public abstract class Player {

    class ActionValue {
        int srcRow, srcCol;
        int destRow, destCol;
        double value;

        ActionValue(double value) {
            this.srcRow = this.srcCol = -1;
            this.destRow = this.destCol = -1;
            this.value = value;
        }

        ActionValue(int srcRow, int srcCol, int destRow, int destCol, double value) {
            this.srcRow = srcRow;
            this.srcCol = srcCol;
            this.destRow = destRow;
            this.destCol = destCol;
            this.value = value;
        }

        double getValue() {
            return value;
        }

        int getSrcRow() {
            return srcRow;
        }

        int getSrcCol() {
            return srcCol;
        }

        int getDestRow() {
            return destRow;
        }

        int getDestCol() {
            return destCol;
        }
    }

    final boolean isBlack;
    final Board board;
    int nodeCount = 0;
    int moveCount = 0;
    long timeCount = 0;

    public Player(boolean isBlack, Board board) {
        this.isBlack = isBlack;
        this.board = board;
    }

    public void reset() {
        nodeCount = 0;
        moveCount = 0;
        timeCount = 0;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public double getAvgNodeCount() {
        return (double) nodeCount / moveCount;
    }

    public double getAvgTimeCount() {
        return (double) timeCount / moveCount;
    }

    public abstract boolean isTurn();

    public abstract void move();
}
