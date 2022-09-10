package co.edu.unal.tictactoe;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class TicTacToeGame {

    private char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
    private Random mRand;

    public static final int BOARD_SIZE = 9;
    public static final char OPEN_SPOT = ' ';
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';

    // The computer's difficulty levels
    public enum DifficultyLevel {Easy, Harder, Expert};
    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard(){
        Arrays.fill(mBoard,OPEN_SPOT);
    }

    /** Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8).
     */
    public boolean setMove(char player, int location){
        if(location>=0 && location<=8 && mBoard[location]==OPEN_SPOT){
            mBoard[location] = player;
            return true;
        }
        return false;
    }

    public char[] getmBoard() {
        return mBoard;
    }

    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */


    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    public int getComputerMove(){
        int move;
        double randomNumber;


        if(mDifficultyLevel == DifficultyLevel.Easy) {
            move = getRandomMove();

        }else if(mDifficultyLevel == DifficultyLevel.Harder){
            randomNumber = ThreadLocalRandom.current().nextDouble(0, 1);
            System.out.println(randomNumber);
            if(randomNumber > 0.4){
                move = computeMove();

            }else{
                move = getRandomMove();
            }
        }else{
            move = computeMove();
        }
        return move;
    }

    public int getRandomMove(){
        int move;
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        return move;
    }

    public int computeMove(){
        int bestScore = -500;
        int bestMove = 0;
        int score;


        for (int i = 0; i < mBoard.length; i++) {
            if(mBoard[i] == OPEN_SPOT){
                mBoard[i] = COMPUTER_PLAYER;
                score = minmax(mBoard, 0, false);

                mBoard[i] = OPEN_SPOT;

                if(score > bestScore){
                    bestScore = score;
                    bestMove = i;
                }


            }
        }
        mBoard[bestMove] = COMPUTER_PLAYER;
        return bestMove;

    }

    public int minmax(char[] board, int depth, boolean isMaximizing){
        int winFlag = checkForWinner(board);

        if(winFlag == 3){
            return 20-depth;
        }else if(winFlag == 2){
            return -(20-depth);
        }else if(winFlag == 1){
            return 0;
        }

        int bestScore;
        int score;

        bestScore = isMaximizing? -500: 500;
        for (int i = 0; i < board.length; i++) {
            if(board[i] == OPEN_SPOT){
                board[i] = isMaximizing ? COMPUTER_PLAYER : HUMAN_PLAYER;
                score = minmax(board, depth + 1, !isMaximizing);
                board[i] = OPEN_SPOT;
                if(score > bestScore && isMaximizing) {
                    bestScore = score;
                }else if(score < bestScore && !isMaximizing){
                    bestScore = score;
                }
            }
        }
        return bestScore;
    }



    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public int checkForWinner(char[] board) {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (board[i] == HUMAN_PLAYER &&
                    board[i+1] == HUMAN_PLAYER &&
                    board[i+2]== HUMAN_PLAYER)
                return 2;
            if (board[i] == COMPUTER_PLAYER &&
                    board[i+1]== COMPUTER_PLAYER &&
                    board[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (board[i] == HUMAN_PLAYER &&
                    board[i+3] == HUMAN_PLAYER &&
                    board[i+6]== HUMAN_PLAYER)
                return 2;
            if (board[i] == COMPUTER_PLAYER &&
                    board[i+3] == COMPUTER_PLAYER &&
                    board[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((board[0] == HUMAN_PLAYER &&
                board[4] == HUMAN_PLAYER &&
                board[8] == HUMAN_PLAYER) ||
                (board[2] == HUMAN_PLAYER &&
                        board[4] == HUMAN_PLAYER &&
                        board[6] == HUMAN_PLAYER))
            return 2;
        if ((board[0] == COMPUTER_PLAYER &&
                board[4] == COMPUTER_PLAYER &&
                board[8] == COMPUTER_PLAYER) ||
                (board[2] == COMPUTER_PLAYER &&
                        board[4] == COMPUTER_PLAYER &&
                        board[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public char[] getBoardState() {
        return mBoard;
    }

    public void setBoardState(char[] board){
        this.mBoard = board;
    }

    public int getBoardOccupant(int i){
        return mBoard[i];
    }


}
