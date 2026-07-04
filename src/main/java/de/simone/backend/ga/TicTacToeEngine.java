package de.simone.backend.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class TicTacToeEngine {
    // The grid where the game is played, represented as a 2D array
    private char[][] grid = new char[3][3];

    // The players, represented as X and O
     static final char PLAYER_X = 'X';
     static final char PLAYER_O = 'O';

    // The current player
    char currentPlayer = PLAYER_X;

    // event sequence
    List<ImmutablePair<Integer, Integer>> moves;

    public enum GameState {
        IN_PROGRESS,
        X_WINS,
        O_WINS,
        TIE,
        ILLEGAL_MOVE,
    }

    public TicTacToeEngine() {
        this.moves = new ArrayList<>();

        // Initialize the grid with empty spaces
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = ' ';
            }
        }
    }

    public GameState setInitialMoves(List<ImmutablePair<Integer, Integer>> moves) {
        GameState state = GameState.IN_PROGRESS;
        for (ImmutablePair<Integer, Integer> move : moves) {
            state = makeMove(move.getLeft(), move.getRight());
            if (state != GameState.IN_PROGRESS) {
                break;
            }
        }
        return state;
    }

    public GameState makeMove(int row, int col) {
        GameState state = GameState.IN_PROGRESS;
        if (row < 0 || row >= 3 || col < 0 || col >= 3 || grid[row][col] != ' ') {
            return GameState.ILLEGAL_MOVE;
        }

        // Update the grid with the player's move
        grid[row][col] = currentPlayer;
        moves.add(ImmutablePair.of(row, col));

        // Check if the game is over
        if (isGameOver()) {
            if (hasWinner()) {
                state = (currentPlayer == PLAYER_X) ? GameState.X_WINS : GameState.O_WINS;
            } else {
                state = GameState.TIE;
            }
        }

        // Switch to the other player
        currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        return state;
    }

    // Print the grid to the console
    public void printGrid() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(" " + grid[i][j] + " ");
                if (j < 2) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i < 2) {
                System.out.println("---+---+---");
            }
        }
    }

    // Check if the game is over (i.e. someone has won or there are no more empty
    // spaces)
    private boolean isGameOver() {
        return hasWinner() || isFull();
    }

    // Check if there is a winner (i.e. someone has three marks in a row)
    private boolean hasWinner() {
        // Check for horizontal wins
        for (int i = 0; i < 3; i++) {
            if (isRowWin(i)) {
                return true;
            }
        }

        // Check for vertical wins
        for (int i = 0; i < 3; i++) {
            if (isColWin(i)) {
                return true;
            }
        }

        // Check for diagonal wins
        if (isDiag1Win() || isDiag2Win()) {
            return true;
        }

        // If none of the above checks passed, there is no winner
        return false;
    }

    // Check if the given row has a winning combination
    private boolean isRowWin(int row) {
        return (grid[row][0] != ' ' && grid[row][0] == grid[row][1] && grid[row][1] == grid[row][2]);
    }

    // Check if the given column has a winning combination
    private boolean isColWin(int col) {
        return (grid[0][col] != ' ' && grid[0][col] == grid[1][col] && grid[1][col] == grid[2][col]);
    }

    // Check if the first diagonal has a winning combination
    private boolean isDiag1Win() {
        return (grid[0][0] != ' ' && grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]);
    }

    // Check if the second diagonal has a winning combination
    private boolean isDiag2Win() {
        return (grid[0][2] != ' ' && grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]);
    }

    // Check if there are no more empty spaces in the grid
    private boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}