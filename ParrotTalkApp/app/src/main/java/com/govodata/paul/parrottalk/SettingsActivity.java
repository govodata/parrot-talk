package com.govodata.paul.parrottalk;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends ParrotTalkAppCompatActivity {

    private TextView defaultNameTextView;
    private EditText queueEditText, nameEditText, keyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show default name for the Azure Queue.
        defaultNameTextView = (TextView) findViewById(R.id.defaultNameTextView);
        defaultNameTextView.setText(getText(R.string.default_queue_name)
                + " \"" + AzureQueue.DEFAULT_QUEUE_NAME + "\"");

        queueEditText = (EditText) findViewById(R.id.queueEditText);
        // When focus is changed from the queueEditText, hide keyboard.
        backgroundFocus(queueEditText);
        // Clear button.
        clearButton(queueEditText);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        // When focus is changed from the connectionEditText, hide keyboard.
        backgroundFocus(nameEditText);
        // Clear button.
        clearButton(nameEditText);

        keyEditText = (EditText) findViewById(R.id.keyEditText);
        // When focus is changed from the connectionEditText, hide keyboard.
        backgroundFocus(keyEditText);
        // Clear button.
        clearButton(keyEditText, getText(R.string.key_string));

        // Show the Azure Queue name if already entered.
        if (!getQueueName().isEmpty()) {
            queueEditText.setText(getQueueName());
        }
        // Show the storage account name if already entered.
        if (!getStorageAccountName().isEmpty()) {
            nameEditText.setText(getStorageAccountName());
        }
        // Show that the storage account key has already been entered.
        if (!getStorageConnectionString().isEmpty()) {
            keyEditText.setHint("*******************************");
        }

        Button enterButton = (Button) findViewById(R.id.enterButton);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInput();
            }
        });
    }

    private void checkInput() {
        // If the storage name and/or key is not entered,
        // show toast asking user to enter one.
        if (nameEditText.getText().toString().isEmpty() && keyEditText.getText().toString().isEmpty()) {
            nameEditText.requestFocus();
            Toast.makeText(SettingsActivity.this,
                    getText(R.string.enter_storage_connection_string) + ".",
                    Toast.LENGTH_LONG)
                    .show();
        }
        else if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.requestFocus();
            Toast.makeText(SettingsActivity.this,
                    getText(R.string.enter_storage_name) + ".",
                    Toast.LENGTH_LONG)
                    .show();
        }
        else if (keyEditText.getText().toString().isEmpty()) {
            // Saved connection string matches account name, connect to azure.
            if (getStorageConnectionString().matches("(.*)" + getStorageAccountName() + "(.*)")) {
                connectAzureQueue(getStorageConnectionString());
            }
            else {
                keyEditText.requestFocus();
                Toast.makeText(SettingsActivity.this,
                        getText(R.string.enter_storage_key) + ".",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
        // Connect to azure.
        else {
            // Convert to proper name format:
            // lowercase letters and numbers.
            final String accountName = nameEditText.getText().toString()
                    .toLowerCase()
                    .replaceAll("[^\\w\\d]", "");

            // Remove spaces from key.
            final String accountKey = keyEditText.getText().toString()
                    .replaceAll(" ", "");

            // Create connection string.
            final String storageConnectionString = "DefaultEndpointsProtocol=https;"
                    + "AccountName=" + accountName
                    + ";AccountKey=" + accountKey;

            connectAzureQueue(storageConnectionString);
        }
    }

    @Override
    void connectAzureQueue(final String storageConnectionString) {
        // Save account name.
        setStorageAccountName(nameEditText.getText().toString());
        try {
            // If no queue name is entered, use default name.
            if (queueEditText.getText().toString().isEmpty()) {
                // Set saved queue name to empty.
                setQueueName("");
                setAzureQueue(storageConnectionString);
            }
            // User entered queue name.
            else {
                // Convert to proper name format:
                // lowercase letters, numbers, and dash
                final String queueName = queueEditText.getText().toString()
                        .toLowerCase()
                        .replaceAll("[^\\w\\d-]", "");
                // Save queue name.
                setQueueName(queueName);
                // Connect to Azure.
                setAzureQueue(storageConnectionString, queueName);
            }
            // Save connection string.
            setStorageConnectionString(storageConnectionString);
            // Connection successful, return to main activity.
            finish();
        } catch (Exception e) {
            // Set saved connection string to empty.
            setStorageConnectionString("");
            Toast.makeText(SettingsActivity.this,
                    e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Focus is on background, hide keyboard.
    private void backgroundFocus(final EditText editText) {
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    // Helper method to hide keyboard.
    /* Add to content view of the activity:
        android:clickable="true"
        android:focusableInTouchMode="true"
    */
    // http://stackoverflow.com/a/19828165
    private void hideKeyboard(final View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Clear button.
    // http://stackoverflow.com/questions/23184120/androidhow-to-clear-an-edittext-by-cross-button-in-the-right-side
    private void clearButton(final EditText editText) {
        editText.setOnTouchListener(new OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = editText.getRight()
                            - Math.abs(editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width());
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        // Clicked on clear icon
                        editText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    // Clear button + set hint.
    private void clearButton(final EditText editText, final CharSequence hint) {
        editText.setHint(hint);
        clearButton(editText);
    }
}
