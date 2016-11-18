package com.example.paul.parrottalk;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final short SPEECH_REQUEST = 1;

    private TextView statusTextView;

    // Controller class for parsing speech input.
    private Controller controller = new Controller();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statusTextView = (TextView) findViewById(R.id.statusView);

        Button commandButton = (Button) findViewById(R.id.commandButton);
        commandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechInput();
            }
        });
    }

    private void speechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say controller.");

        try {
            startActivityForResult(intent, SPEECH_REQUEST);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(
                    MainActivity.this,
                    "Speech recognition is not supported on this device.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String s = "";

        switch (requestCode) {
            case SPEECH_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    try {
                        s = controller.parseString(result.get(0));
                    } catch (IllegalArgumentException ex) {
                        Toast.makeText(
                                MainActivity.this,
                                "Unable to parse \"" + result.get(0) + "\".\nTry again.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                    statusTextView.setText(s);
                }
                break;
        }
    }
}
