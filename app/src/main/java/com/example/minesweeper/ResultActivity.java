package com.example.minesweeper;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);
        TextView resultTextView = findViewById(R.id.customTextView);
        String resultMessage = getIntent().getStringExtra("RESULT_MESSAGE");
        int timeElapsed = getIntent().getIntExtra("TIME_ELAPSED", 0);
        if (resultMessage != null) {
            resultTextView.setText(resultMessage + "\nTime Used: " + timeElapsed + " seconds");
        }
    }
}