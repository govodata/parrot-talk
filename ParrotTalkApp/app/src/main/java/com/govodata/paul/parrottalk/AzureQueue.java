package com.govodata.paul.parrottalk;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

class AzureQueue {

    // Define the default name for the queue if not given
    static final String DEFAULT_QUEUE_NAME = "parrotqueue";

    // Define the message timeout in seconds
    private static final short MESSAGE_TIMEOUT = 10;

    private CloudQueue queue;
    private final CloudQueueClient queueClient;

    AzureQueue(final String storageConnectionString) throws Exception {
        this(storageConnectionString, DEFAULT_QUEUE_NAME);
    }

    AzureQueue(final String storageConnectionString, final String queueName) throws Exception {
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        queueClient = storageAccount.createCloudQueueClient();

        CreateQueue createQueue = new CreateQueue();
        createQueue.execute(queueName);
        if (createQueue.exception != null) {
            throw createQueue.exception;
        }
    }

    private class CreateQueue extends AsyncTask<String, String, String> {

        Exception exception = null;

        @Override
        protected String doInBackground(String... params) {
            for (String queueName : params) {
                try {
                    queue = queueClient.getQueueReference(queueName);
                    queue.createIfNotExists();
                } catch (Exception e) {
                    exception = e;
                    return e.getMessage();
                }
                if (isCancelled()) {
                    break;
                }
            }
            return null;
        }
    }

    void addMessages(String... messages) throws StorageException {
        AddMessagesToQueue addMessagesToQueue = new AddMessagesToQueue();
        addMessagesToQueue.execute(messages);
        if (addMessagesToQueue.exception != null) {
            throw addMessagesToQueue.exception;
        }
    }

    private class AddMessagesToQueue extends AsyncTask<String, String, String> {

        StorageException exception = null;

        @Override
        protected String doInBackground(String... params) {
            for (String message : params) {
                CloudQueueMessage cloudQueueMessage = new CloudQueueMessage(message);
                try {
                    // Specify that the message will be in the queue for
                    // the specified MESSAGE_TIMEOUT seconds.
                    queue.addMessage(cloudQueueMessage, MESSAGE_TIMEOUT, 0, null, null);
                } catch (StorageException e) {
                    exception = e;
                    return e.getMessage();
                }
                if (isCancelled()) {
                    break;
                }
            }
            return null;
        }
    }
}
