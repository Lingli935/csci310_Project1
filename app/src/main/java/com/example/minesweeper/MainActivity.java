package com.example.minesweeper;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private static final int ROW = 10;
    private static final int COL = 12;
    private static final int NUM_MINES = 4;
    private ArrayList<TextView> cellTextViews;
    private Cell[][] mineGrid;
    private Random random;
    private boolean isFlagMode = false;  // Initially set to dig mode
    private boolean isTimerRunning = false;
    private int secondsElapsed = 0;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private int revealedCells = 0;
    private boolean isGameWon = false;
    private  long startTime = 0;
    private TextView timerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cellTextViews = new ArrayList<>();
        random = new Random();
        mineGrid = new Cell[ROW][COL];

        timerTextView=findViewById(R.id.timerCounter);

        initGrid();
        placeMines();
        calculateAdjacentMines();
        setupUI();

        TextView modeTextView = findViewById(R.id.bottomImage);
        modeTextView.setOnClickListener(view -> toggleMode());

        setupTimer();
    }

    private void initGrid() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                mineGrid[i][j] = new Cell();
            }
        }
    }

    private void placeMines() {
        int minesPlaced = 0;
        while (minesPlaced < NUM_MINES) {
            int row = random.nextInt(ROW);
            int col = random.nextInt(COL);
            if (!mineGrid[row][col].getHasMine()) {
                mineGrid[row][col].setMine();
                minesPlaced++;
            }
//            // for debugging: to delete
//            System.out.print(row);
//            System.out.println(col);
        }
    }

    private void calculateAdjacentMines() {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (!mineGrid[i][j].getHasMine()) {
                    int adjacentMines = 0;
                    for (int k = 0; k < 8; k++) {
                        int newRow = i + dx[k];
                        int newCol = j + dy[k];
                        if (newRow >= 0 && newRow < ROW && newCol >= 0 && newCol < COL) {
                            if (mineGrid[newRow][newCol].getHasMine()) {
                                adjacentMines++;
                            }
                        }
                    }
                    mineGrid[i][j].setNumberOfAdjacentMines(adjacentMines);
                }
            }
        }

    }private void setupTimer() {
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                secondsElapsed++;
                long elapsedmillis = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedmillis / 1000);
                int minutes = seconds / 60;
                seconds = seconds%60;

                String time = String.format("%02d:%02d", minutes, seconds);
                timerTextView.setText(time);
                timerHandler.postDelayed(this, 1000);  // Update every second
            }
        };
    }

    private void startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true;
            startTime=System.currentTimeMillis();
            timerHandler.post(timerRunnable);  // Start the timer
        }
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);  // Stop the timer
        isTimerRunning = false;
    }

    private void checkWinCondition() {
        int nonMineCells = (ROW * COL) - NUM_MINES;
        if (revealedCells == nonMineCells && !isGameWon) {
            isGameWon = true;
            showGameResult("You Win\nGood Job");
        }
    }
    private void revealAllMines() {
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                Cell cell = mineGrid[row][col];
                int index = row * COL + col;
                TextView tv = cellTextViews.get(index);

                if (cell.getHasMine()) {
                    tv.setBackgroundResource(R.drawable.mine);
                    //tv.setBackgroundColor(Color.RED);
                } else {
                    int adjacentMines = cell.getNumberOfAdjacentMines();
                    if (adjacentMines > 0) {
                        //In Android, the TextView component requires its content (the text to display) to be a string (not an int or any other data type)
                        tv.setText(String.valueOf(adjacentMines));
                    } else {
                        tv.setText("");
                    }
                    tv.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        // for debugging: to delete
//        for (int i = 0; i < ROW; i++) {
//            for (int j = 0; j < COL; j++) {
//                System.out.println(mineGrid[i][j].getNumberOfAdjacentMines());
//            }
//        }
    }


    private void setupUI() {

        int numberMines = 4;
        GridLayout grid = (GridLayout) findViewById(R.id.gradLayout09);

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                TextView tv = new TextView(this);
                tv.setHeight(dpToPixel(32));
                tv.setWidth(dpToPixel(32));
                tv.setTextSize(32);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.WHITE);
                tv.setPadding(0, 0, 0, 0);  // Ensure no padding
                tv.setCompoundDrawablePadding(0);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i, 1);
                lp.columnSpec = GridLayout.spec(j, 1, 1f);
                grid.addView(tv, lp);
                cellTextViews.add(tv);
            }
        }
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int i = 0; i < cellTextViews.size(); i++) {
            if (cellTextViews.get(i) == tv)
                return i;
        }
        return -1;
    }

    // Convert dp to pixel
    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void showGameResult(String result) {
        stopTimer();  // Stop the timer when the game ends
        String finalTime = timerTextView.getText().toString();

        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("RESULT_MESSAGE", result);  // Pass the result message to ResultActivity
            intent.putExtra("TIME_ELAPSED", secondsElapsed);  // Pass the elapsed time (in seconds) to ResultActivity
            startActivity(intent);
            finish();
        }, 3000);
    }

    private int flagCounter = 4;

    // Toggle between dig mode and flag mode
    public void toggleMode() {
        TextView modeTextView = findViewById(R.id.bottomImage);  // Find the bottom TextView (dig/flag button)

        if (isFlagMode) {
            isFlagMode = false;
            modeTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.digging, 0, 0);  // Show dig image
        } else {
            isFlagMode = true;

            Drawable flagDrawable = getResources().getDrawable(R.drawable.flag);
            int width = dpToPixel(24);
            int height = dpToPixel(24);
            flagDrawable.setBounds(0, 0, width, height);
            modeTextView.setCompoundDrawables(null, flagDrawable, null, null);
        }
    }


    public void onClickTV(View view) {
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int row = n / COL;
        int col = n % COL;
        Cell cell = mineGrid[row][col];

        if (!isTimerRunning) {
            startTimer();
            System.out.println("Timer started!");
        }

        if (cell.getIsRevealed()) {
            return;
        }

        if (isFlagMode) {
            onFlagCell(tv);
            return;

        }

        if (cell.getHasMine()) {
            tv.setBackgroundResource(R.drawable.mine);
            revealAllMines();
            showGameResult("You Lose");
        } else {
            if (cell.getNumberOfAdjacentMines() == 0) {
                revealEmptyCells(row, col);
            } else {
                tv.setText(String.valueOf(cell.getNumberOfAdjacentMines()));
                tv.setBackgroundColor(Color.LTGRAY);
                cell.setRevealed(true);
                revealedCells++;
            }

            checkWinCondition();
        }
    }


    public void onFlagCell(TextView tv) {
        int n = findIndexOfCellTextView(tv);
        int row = n / COL;
        int col = n % COL;
        Cell cell = mineGrid[row][col];

        if (cell.getIsFlagged()) {
            cell.setFlagged(false);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);  // Remove the flag icon
            tv.setBackgroundColor(Color.GREEN);
            tv.setText("");
            flagCounter++;
        } else if (flagCounter > 0) {
            cell.setFlagged(true);

            Drawable flagDrawable = getResources().getDrawable(R.drawable.flag);

            int cellHeight = tv.getHeight();
            int cellWidth = tv.getWidth();

            int padding = 0;
            int width = cellWidth - 2 * padding;
            int height = cellHeight - 2 * padding;
            tv.setText(R.string.flag);

            flagCounter--;
        }

        TextView flagCounterTextView = findViewById(R.id.flagCounter);
        flagCounterTextView.setText(String.valueOf(flagCounter));
    }



    public void revealEmptyCells(int row, int col) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{row, col});

        mineGrid[row][col].setRevealed(true);
        revealCell(row, col);
        revealedCells++;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currRow = current[0];
            int currCol = current[1];

            for (int i = 0; i < 8; i++) {
                int newRow = currRow + dx[i];
                int newCol = currCol + dy[i];

                // Check if the new cell is within bounds and not revealed yet
                if (newRow >= 0 && newRow < ROW && newCol >= 0 && newCol < COL && !mineGrid[newRow][newCol].getIsRevealed()) {
                    // Reveal the neighbor cell
                    mineGrid[newRow][newCol].setRevealed(true);
                    revealCell(newRow, newCol);
                    revealedCells++;

                    // If the neighbor has 0 adjacent mines, add it to the queue for further expansion
                    if (mineGrid[newRow][newCol].getNumberOfAdjacentMines() == 0) {
                        queue.add(new int[]{newRow, newCol});
                    }
                }
            }
            checkWinCondition();

        }
    }

    private void revealCell(int row, int col) {
        TextView tv = cellTextViews.get(row * COL + col);
        int adjacentMines = mineGrid[row][col].getNumberOfAdjacentMines();

        tv.setBackgroundColor(Color.LTGRAY);
        if (adjacentMines > 0) {
            tv.setText(String.valueOf(adjacentMines));
        } else {
            tv.setText("");
        }
    }




}

