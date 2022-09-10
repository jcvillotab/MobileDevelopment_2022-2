package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private TicTacToeGame mGame;
    // Various text displayed
    private TextView mHumanScoreTextView;
    private TextView mComputerScoreTextView;
    private TextView mTieScoreTextView;
    private TextView mInfoTextView;
    private Boolean mGameOver;
    private char mGoFirst = TicTacToeGame.HUMAN_PLAYER;

    private BoardView mBoardView;

    private SharedPreferences mPrefs;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    private int mTies = 0, mComputerWins = 0, mHumanWins = 0;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        // Restore the scores
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);

        mGame = new TicTacToeGame();
        mInfoTextView = (TextView) findViewById(R.id.information);
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        mHumanScoreTextView = findViewById(R.id.player_score);
        mComputerScoreTextView = findViewById(R.id.computer_score);
        mTieScoreTextView = findViewById(R.id.tie_score);

        if(savedInstanceState == null){
            startNewGame();
        }else{
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mHumanWins = savedInstanceState.getInt("mHumanWins");
            mComputerWins = savedInstanceState.getInt("mComputerWins");
            mTies = savedInstanceState.getInt("mTies");
            mGoFirst = savedInstanceState.getChar("mGoFirst");

        }

        displayScores();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", Integer.valueOf(mHumanWins));
        outState.putInt("mComputerWins", Integer.valueOf(mComputerWins));
        outState.putInt("mTies", Integer.valueOf(mTies));
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", mGoFirst);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        ed.commit();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mHumanWins = savedInstanceState.getInt("mHumanWins");
        mComputerWins = savedInstanceState.getInt("mComputerWins");
        mTies = savedInstanceState.getInt("mTies");
        mGoFirst = savedInstanceState.getChar("mGoFirst");
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        mGameOver = false;
        mBoardView.invalidate();

        // Human goes first
        mInfoTextView.setText(R.string.first_human);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event){
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                int winner = mGame.checkForWinner(mGame.getmBoard());
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mComputerMediaPlayer.start(); // Play the sound effect
                        }
                    },1000);
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner(mGame.getmBoard());
                }
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_human);
                }
                else if (winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    mTies++;
                    displayScores();
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    mGameOver = true;
                    mHumanWins++;
                    displayScores();
                }
                else{
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mComputerWins++;
                    displayScores();
                    mGameOver = true;
                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;

        }
    };

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            if(player == TicTacToeGame.HUMAN_PLAYER){
                mHumanMediaPlayer.start();
            }
            return true;
        }
        return false;
    }

    private void displayScores() {
        mHumanScoreTextView.setText(Integer.toString(mHumanWins));
        mComputerScoreTextView.setText(Integer.toString(mComputerWins));
        mTieScoreTextView.setText(Integer.toString(mTies));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.reset_scores:
                mHumanWins = 0;
                mComputerWins = 0;
                mTies = 0;
                displayScores();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.machine);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};
                int selected = 2;
        // selected is the radio button that should be selected.
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                switch (item){
                                    case 0:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                        break;
                                    case 1:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                        break;
                                    default:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                        break;
                                }
                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT).show();

                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }




}