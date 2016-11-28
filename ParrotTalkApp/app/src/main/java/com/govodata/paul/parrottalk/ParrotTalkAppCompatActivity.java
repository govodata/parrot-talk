package com.govodata.paul.parrottalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.microsoft.azure.storage.StorageException;

abstract class ParrotTalkAppCompatActivity extends AppCompatActivity {

    // AzureQueue class for messaging.
    private static AzureQueue azureQueue;

    static void addMessagesToAzureQueue(String... messages) throws StorageException {
        azureQueue.addMessages(messages);
    }

    static void setAzureQueue(final String storageConnectionString) throws Exception {
        azureQueue = new AzureQueue(storageConnectionString);
    }

    static void setAzureQueue(final String storageConnectionString, final String queueName) throws Exception {
        azureQueue = new AzureQueue(storageConnectionString, queueName);
    }

    abstract void connectAzureQueue(final String storageConnectionString);

    // Methods for getting and setting saved QueueName, StorageAccountName, and StorageConnection String.
    String getQueueName() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.connection_settings), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.queue_name_string), "");
    }

    String getStorageAccountName() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.connection_settings), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.account_name_string), "");
    }

    String getStorageConnectionString() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.connection_settings), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.storage_connection_string), "");
    }

    void setQueueName(final String queueName) {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.connection_settings), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.queue_name_string), queueName);
        editor.apply();
    }

    void setStorageAccountName(final String storageConnectionString) {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.connection_settings), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.account_name_string), storageConnectionString);
        editor.apply();
    }

    void setStorageConnectionString(final String storageConnectionString) {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.connection_settings), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.storage_connection_string), storageConnectionString);
        editor.apply();
    }
}
