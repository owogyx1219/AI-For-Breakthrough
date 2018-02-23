package part2.gui;

import part2.model.Board;
import part2.player.AlphaBetaPlayer;
import part2.player.HumanPlayer;
import part2.player.MinimaxPlayer;
import part2.player.Player;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    private final Board chessboard;

    private final JFrame gameFrame;
    private final BoardPanel panel;
    private final PlayerTurnPanel playerTurnPanel;
    private boolean isAutomatic = false;

    private Player playerBlack, playerWhite;

    private BiFunction<Board, Player, Double> offensive_heuristic_1 =
            (board, player) -> 2 * (30 - (player.isBlack() ? board.getWhiteRemaining() : board.getBlackRemaining()) + Math.random());
    private BiFunction<Board, Player, Double> defensive_heuristic_1 =
            (board, player) -> 2 * (player.isBlack() ? board.getBlackRemaining() : board.getWhiteRemaining()) + Math.random();

    private BiFunction<Board, Player, Double> offensive_heuristic_2 =
            (board, player) -> (double) board.getEvaluationValue(false, player.isBlack()) + Math.random();
    private BiFunction<Board, Player, Double> defensive_heuristic_2 =
            (board, player) -> (double) board.getEvaluationValue(true, player.isBlack()) + Math.random();


    public Main(boolean blackStart) {
        this.chessboard = new Board(8, 8, blackStart, 1, this);
        this.gameFrame = new JFrame("Game of Breakthrough");
        this.panel = new BoardPanel(chessboard);
        this.playerTurnPanel = new PlayerTurnPanel(chessboard);

        this.gameFrame.setSize(new Dimension(120 + 60 * chessboard.getWidth(), 60 * chessboard.getHeight()));
        this.gameFrame.setLocationRelativeTo(null);
        this.gameFrame.setResizable(false);
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.playerTurnPanel.setTurn();

        this.gameFrame.add(this.panel, BorderLayout.CENTER);
        this.gameFrame.add(this.playerTurnPanel, BorderLayout.WEST);

        this.gameFrame.setVisible(true);

        render();
        matchupSingle((Void) -> { this.alphabetaVShuman(5); return null; });
    }

    public void render()
    {
        panel.render();
        playerTurnPanel.setTurn();
    }

    public void gameEnd()
    {
        if (!isAutomatic) {
            JOptionPane optionPane = new JOptionPane(
                    "Game Ends! "
                            + (chessboard.isWhiteWin() ? "White" : (chessboard.isBlackWin() ? "Black" : "[ERROR]"))
                            + " wins.\n\n"
                            + "=====Black=====\n"
                            + "Total " + playerBlack.getMoveCount() + " moves\n"
                            + "Captured " + (2 * chessboard.getWidth() - chessboard.getWhiteRemaining())
                            + " opponent's workers\n"
                            + "Expanded " + playerBlack.getNodeCount() + " nodes (avg. "
                            + String.format("%.2f", playerBlack.getAvgNodeCount()) + " nodes, avg. "
                            + String.format("%.2f", playerBlack.getAvgTimeCount()) + "ms to move)\n\n"
                            + "=====White=====\n"
                            + "Total " + playerWhite.getMoveCount() + " moves\n"
                            + "Captured " + (2 * chessboard.getWidth() - chessboard.getBlackRemaining())
                            + " opponent's workers\n"
                            + "Expanded " + playerWhite.getNodeCount() + " nodes (avg. "
                            + String.format("%.2f", playerWhite.getAvgNodeCount()) + " nodes, avg. "
                            + String.format("%.2f", playerWhite.getAvgTimeCount()) + "ms to move)\n\n"
                            + "Do you want to start a new game?",
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION);

            JDialog dlg = optionPane.createDialog("Game End");
            dlg.setVisible(true);
            int value = (Integer) optionPane.getValue();
            if (value == JOptionPane.YES_OPTION) {
                chessboard.reset();
                playerWhite.reset();
                playerBlack.reset();
            }
        }
    }

    private void proceedGame() {
        chessboard.reset();
        playerWhite.reset();
        playerBlack.reset();
        while (!chessboard.isGameEnd()) {
            if (playerBlack.isTurn())
                playerBlack.move();
            else if (playerWhite.isTurn())
                playerWhite.move();
        }
    }

    private void matchupSingle(Function<Void, Void> matchup) {
        System.out.println("Running matchup");
        matchup.apply(null);
        System.out.println("=====================RESULT======================");
        System.out.println((chessboard.isWhiteWin() ? "White" : (chessboard.isBlackWin() ? "Black" : "[ERROR]"))
                + " wins.\n\n"
                + "=====Black=====\n"
                + "Total " + playerBlack.getMoveCount() + " moves\n"
                + "Captured " + (2 * chessboard.getWidth() - chessboard.getWhiteRemaining())
                + " opponent's workers\n"
                + "Expanded " + playerBlack.getNodeCount() + " nodes (avg. "
                + String.format("%.2f", playerBlack.getAvgNodeCount()) + " nodes, avg. "
                + String.format("%.2f", playerBlack.getAvgTimeCount()) + "ms to move)\n\n"
                + "=====White=====\n"
                + "Total " + playerWhite.getMoveCount() + " moves\n"
                + "Captured " + (2 * chessboard.getWidth() - chessboard.getBlackRemaining())
                + " opponent's workers\n"
                + "Expanded " + playerWhite.getNodeCount() + " nodes (avg. "
                + String.format("%.2f", playerWhite.getAvgNodeCount()) + " nodes, avg. "
                + String.format("%.2f", playerWhite.getAvgTimeCount()) + "ms to move)");
        System.out.println("=================================================");
    }

    private void matchupIterations(int numberOfGames, Function<Void, Void> matchup) {
        isAutomatic = true;
        int whiteWin = 0;
        int blackWin = 0;
        ArrayList<Integer> whiteMove = new ArrayList<>();
        ArrayList<Integer> blackMove = new ArrayList<>();
        ArrayList<Integer> whiteCaptured = new ArrayList<>();
        ArrayList<Integer> blackCaptured = new ArrayList<>();
        ArrayList<Integer> whiteExpandedNode = new ArrayList<>();
        ArrayList<Integer> blackExpandedNode = new ArrayList<>();
        ArrayList<Double> whiteAvgExpandedNode = new ArrayList<>();
        ArrayList<Double> blackAvgExpandedNode = new ArrayList<>();
        ArrayList<Double> whiteAvgTime = new ArrayList<>();
        ArrayList<Double> blackAvgTime = new ArrayList<>();


        System.out.println("Running " + numberOfGames + " matchups");
        for(int i = 0; i < numberOfGames; i++)
        {
            System.out.println((i + 1) + "/" + numberOfGames);
            try {
                matchup.apply(null);
            } catch (Exception ex) { i--; }
            whiteMove.add(playerWhite.getMoveCount());
            blackMove.add(playerBlack.getMoveCount());
            whiteCaptured.add(2 * chessboard.getWidth() - chessboard.getWhiteRemaining());
            blackCaptured.add(2 * chessboard.getWidth() - chessboard.getBlackRemaining());
            whiteExpandedNode.add(playerWhite.getNodeCount());
            blackExpandedNode.add(playerBlack.getNodeCount());
            whiteAvgExpandedNode.add(playerWhite.getAvgNodeCount());
            blackAvgExpandedNode.add(playerBlack.getAvgNodeCount());
            whiteAvgTime.add(playerWhite.getAvgTimeCount());
            blackAvgTime.add(playerBlack.getAvgTimeCount());
            if(chessboard.isWhiteWin())
                whiteWin++;
            else
                blackWin++;
        }
        System.out.println("=====================RESULT======================");
        System.out.println("Black won " + blackWin + " matchups (" + String.format("%.2f", (double)blackWin / numberOfGames * 100) + "%)");
        System.out.println("White won " + whiteWin + " matchups (" + String.format("%.2f", (double)whiteWin / numberOfGames * 100) + "%)\n");
        System.out.println("Average Black Move: " + String.format("%.2f", blackMove.stream().mapToDouble(a -> a).average().getAsDouble()));
//        System.out.println(blackMove);
        System.out.println("Average White Move: " + String.format("%.2f", whiteMove.stream().mapToDouble(a -> a).average().getAsDouble()));
//        System.out.println(whiteMove);
        System.out.println("Average White Piece Captured By Black: " + String.format("%.2f", whiteCaptured.stream().mapToDouble(a -> a).average().getAsDouble()));
//        System.out.println(whiteCaptured);
        System.out.println("Average Black Piece Captured By White: " + String.format("%.2f", blackCaptured.stream().mapToDouble(a -> a).average().getAsDouble()));
//        System.out.println(blackCaptured);
        System.out.println("Average Expanded Black Nodes: " + String.format("%.2f", blackExpandedNode.stream().mapToDouble(a -> a).average().getAsDouble()));
//        System.out.println(blackExpandedNode);
        System.out.println("Average Expanded White Nodes: " + String.format("%.2f", whiteExpandedNode.stream().mapToDouble(a -> a).average().getAsDouble()));
//        System.out.println(whiteExpandedNode);
//        System.out.println("Black Avg Expanded Node");
//        System.out.println(blackAvgExpandedNode.stream().map(d -> String.format("%.2f", d)).collect(Collectors.toList()));
//        System.out.println("White Avg Expanded Node");
//        System.out.println(whiteAvgExpandedNode.stream().map(d -> String.format("%.2f", d)).collect(Collectors.toList()));
//        System.out.println("Black Avg Time to Make a Move (ms)");
//        System.out.println(whiteAvgTime.stream().map(d -> String.format("%.2f", d)).collect(Collectors.toList()));
//        System.out.println("White Avg Time to Make a Move (ms)");
//        System.out.println(blackAvgTime.stream().map(d -> String.format("%.2f", d)).collect(Collectors.toList()));
        System.out.println("=================================================");
        isAutomatic = false;
    }

    private void matchup1(int blackDepth, int whiteDepth) {
        playerTurnPanel.setName("MNO1", "ABO1");
        playerBlack = new MinimaxPlayer(true, offensive_heuristic_1, blackDepth, chessboard);
        playerWhite = new AlphaBetaPlayer(false, offensive_heuristic_1, whiteDepth, chessboard);

        proceedGame();
    }

    private void matchup2(int blackDepth, int whiteDepth) {
        playerTurnPanel.setName("ABO2", "ABD1");
        playerBlack = new AlphaBetaPlayer(true, offensive_heuristic_2, blackDepth, chessboard);
        playerWhite = new AlphaBetaPlayer(false, defensive_heuristic_1, whiteDepth, chessboard);

        proceedGame();
    }

    private void matchup3(int blackDepth, int whiteDepth) {
        playerTurnPanel.setName("ABD2", "ABO1");
        playerBlack = new AlphaBetaPlayer(true, defensive_heuristic_2, blackDepth, chessboard);
        playerWhite = new AlphaBetaPlayer(false, offensive_heuristic_1, whiteDepth, chessboard);

        proceedGame();
    }

    private void matchup4(int blackDepth, int whiteDepth) {
        playerTurnPanel.setName("ABO2", "ABO1");
        playerBlack = new AlphaBetaPlayer(true, offensive_heuristic_2, blackDepth, chessboard);
        playerWhite = new AlphaBetaPlayer(false, offensive_heuristic_1, whiteDepth, chessboard);

        proceedGame();
    }

    private void matchup5(int blackDepth, int whiteDepth) {
        playerTurnPanel.setName("ABD2", "ABD1");
        playerBlack = new AlphaBetaPlayer(true, defensive_heuristic_2, blackDepth, chessboard);
        playerWhite = new AlphaBetaPlayer(false, defensive_heuristic_1, whiteDepth, chessboard);

        proceedGame();
    }

    private void matchup6(int blackDepth, int whiteDepth) {
        playerTurnPanel.setName("ABO2", "ABD2");
        playerBlack = new AlphaBetaPlayer(true, offensive_heuristic_2, blackDepth, chessboard);
        playerWhite = new AlphaBetaPlayer(false, defensive_heuristic_2, whiteDepth, chessboard);

        proceedGame();
    }

    private void humanVShuman() {
        playerTurnPanel.setName("HM1", "HM2");
        playerBlack = new HumanPlayer(true, chessboard);
        playerWhite = new HumanPlayer(false, chessboard);

        proceedGame();
    }

    private void alphabetaVShuman(int blackDepth) {
        playerTurnPanel.setName("ABD2", "HM");
        playerBlack = new AlphaBetaPlayer(true, defensive_heuristic_2, blackDepth, chessboard);
        playerWhite = new HumanPlayer(false, chessboard);

        proceedGame();
    }

    public static void main(String[] args) {
        new Main(true);
    }
}
