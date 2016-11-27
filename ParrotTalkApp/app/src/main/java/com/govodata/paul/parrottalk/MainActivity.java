package com.govodata.paul.parrottalk;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.azure.storage.StorageException;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final short SPEECH_REQUEST = 1;

    private TextView statusTextView;

    // Controller class for parsing speech input.
    private Controller controller = new Controller();

    // AzureQueue class for messaging.
    private static AzureQueue azureQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statusTextView = (TextView) findViewById(R.id.statusView);

        connectAzureQueue();

        Button commandButton = (Button) findViewById(R.id.commandButton);
        commandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getStorageConnectionString().isEmpty()) {
                    Toast.makeText(
                            MainActivity.this,
                            getText(R.string.enter_storage_connection_string) + " "
                                    + getText(R.string.in_settings) + ".",
                            Toast.LENGTH_LONG)
                            .show();
                }
                else {
                    speechInput();
                }
            }
        });
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
                                getText(R.string.unable_parse)
                                        + " \"" + result.get(0) + "\".\n"
                                        + getText(R.string.try_again) + ".",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                    statusTextView.setText(s);
                    // Add message to Azure queue.
                    try {
                        azureQueue.addMessages(s);
                    }
                    catch (StorageException e) {
                        Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void speechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        try {
            startActivityForResult(intent, SPEECH_REQUEST);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(
                    MainActivity.this,
                    getText(R.string.speech_not_supported) + ".",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    // Connects to Azure Storage with saved storage connection string and queue name.
    private void connectAzureQueue() {
        final String storageConnectionString = getStorageConnectionString();

        // Bring up settings activity if no storage connection string is saved.
        if (storageConnectionString.isEmpty()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else {
            final String queueName = getQueueName();
            try {
                if (queueName.isEmpty()) {
                    setAzureQueue(storageConnectionString);
                }
                else {
                    setAzureQueue(storageConnectionString, queueName);
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this,
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    static void setAzureQueue(final String storageConnectionString) throws Exception {
        azureQueue = new AzureQueue(storageConnectionString);
    }

    static void setAzureQueue(final String storageConnectionString, final String queueName) throws Exception {
        azureQueue = new AzureQueue(storageConnectionString, queueName);
    }

    private String getQueueName() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.connection_settings), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.queue_name_string), "");
    }

    private String getStorageConnectionString() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.connection_settings), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.storage_connection_string), "");
    }
}
