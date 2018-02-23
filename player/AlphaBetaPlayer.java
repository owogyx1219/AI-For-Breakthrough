package part2.player;

import part2.model.Board;
import part2.model.Piece;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AlphaBetaPlayer extends Player {
    private BiFunction<Board, Player, Double> evalFunc;
    private int maxDepth;

    public AlphaBetaPlayer(boolean isBlack, BiFunction<Board, Player, Double> evalFunc, int maxDepth, Board board) {
        super(isBlack, board);
        this.evalFunc = evalFunc;
        this.maxDepth = maxDepth;
    }

    public boolean isTurn() {
        return board.isBlackTurn() == isBlack;
    }

    public void move() {
        final long startTime = System.currentTimeMillis();
        ActionValue nextMove = alphabeta(board, true, 1, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        final long endTime = System.currentTimeMillis();
        timeCount += endTime - startTime;
        try {
            board.movePiece(nextMove.getSrcRow(), nextMove.getSrcCol(),
                    nextMove.getDestRow(), nextMove.getDestCol());
            moveCount++;
        } catch (Exception ex) {
            throw new Error(ex.getMessage());
        }
    }

    private ActionValue alphabeta(Board board, boolean isMax, int depth, int maxDepth, double alpha, double beta) {
        if (depth == maxDepth || board.isGameEnd())
            return new ActionValue(evalFunc.apply(board, this));

        double v;
        int srcRow = -1, srcCol = -1, destRow = -1, destCol = -1;
        if (isMax)
            v = Integer.MIN_VALUE;
        else
            v = Integer.MAX_VALUE;

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.isBlack() == board.isBlackTurn()) {
                    int pieceRow = piece.getRowPosition();
                    int pieceCol = piece.getColPosition();
                    for (int delta = -1; delta <= 1; delta++) {
                        Board tempBoard = new Board(board, null);
                        try {
                            tempBoard.movePiece(pieceRow, pieceCol,
                                    piece.isBlack() ? (pieceRow + 1) : (pieceRow - 1), pieceCol + delta);
                            nodeCount++;
                            double val = alphabeta(tempBoard, !isMax, depth + 1, maxDepth, alpha, beta).getValue();
                            if (isMax) {
                                if (val > v) {
                                    v = val;
                                    srcRow = row;
                                    srcCol = col;
                                    destRow = piece.isBlack() ? (pieceRow + 1) : (pieceRow - 1);
                                    destCol = pieceCol + delta;

                                    if (v >= beta)
                                        return new ActionValue(srcRow, srcCol, destRow, destCol, v);
                                    if (v > alpha)
                                        alpha = v;
                                }
                            } else if (!isMax) {
                                if (val < v) {
                                    v = val;
                                    srcRow = row;
                                    srcCol = col;
                                    destRow = piece.isBlack() ? (pieceRow + 1) : (pieceRow - 1);
                                    destCol = pieceCol + delta;

                                    if (v <= alpha)
                                        return new ActionValue(srcRow, srcCol, destRow, destCol, v);
                                    if (v < beta)
                                        beta = v;
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }
        return new ActionValue(srcRow, srcCol, destRow, destCol, v);
    }
}
