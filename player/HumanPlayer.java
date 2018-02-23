package part2.player;

import part2.model.Board;

public class HumanPlayer extends Player {
    public HumanPlayer(boolean isBlack, Board board) {
        super(isBlack, board);
    }

    public boolean isTurn() {
        return board.isBlackTurn() == isBlack;
    }

    public void move() {
        final long startTime = System.currentTimeMillis();
        while (isTurn()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
        final long endTime = System.currentTimeMillis();
        timeCount += endTime - startTime;
        moveCount++;
    }
}
