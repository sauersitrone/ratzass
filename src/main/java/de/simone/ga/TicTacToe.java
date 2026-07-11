package de.simone.ga;

import java.util.Scanner;

import org.apache.commons.lang3.tuple.ImmutablePair;

import de.simone.ga.TicTacToeEngine.GameState;


public class TicTacToe {

    public static void main(String[] args) {

        TicTacToeEngine engine = new TicTacToeEngine();
        TicTacToeGAPlayer gaPlayer = new TicTacToeGAPlayer();

        // Start the game loop
        while (true) {
            engine.printGrid();

            int row, col;
            if (engine.currentPlayer == TicTacToeEngine.PLAYER_O) {
                System.out.println("Player O's turn (GA Player)");
                ImmutablePair<Integer, Integer> move = gaPlayer.makeMove(engine.moves);
                row = move.getLeft();
                col = move.getRight();
            } else {
                System.out.println("Player " + engine.currentPlayer + ", enter your move (row, col): ");
                @SuppressWarnings("resource")
                Scanner scanner = new Scanner(System.in);
                row = scanner.nextInt();
                col = scanner.nextInt();
            }

            // Update the grid with the player's move
            GameState state = engine.makeMove(row, col);
            if (state == GameState.ILLEGAL_MOVE) {
                System.out.println("Illegal move, try again.");
                continue;
            }

            if (state == GameState.X_WINS)
                System.out.println("Player X wins!");

            if (state == GameState.O_WINS)
                System.out.println("Player O wins!");

            if (state == GameState.TIE)
                System.out.println("It's a tie!");

            if (state != GameState.IN_PROGRESS) {
                engine.printGrid();
                break;
            }

        }

        System.out.println("Game over.");
    }
}
