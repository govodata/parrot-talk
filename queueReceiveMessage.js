var azure = require('azure-storage');

process.env.AZURE_STORAGE_ACCOUNT = 'yourstorageaccount';
process.env.AZURE_STORAGE_ACCESS_KEY = 'yourstoragekey';

var queueSvc = azure.createQueueService();

queueSvc.getMessages('parrotqueue', function(error, result, response) {
  if(!error){
    // Message text is in messages[0].messageText
    var message = result[0];
    //**********************************************************************************
    // Decode message from base64 and convert into string
    var decodedMessage = (Buffer.from(message.messageText, 'base64')).toString('ascii');
    //**********************************************************************************
    console.log(decodedMessage);
    queueSvc.deleteMessage('parrotqueue', message.messageId, message.popReceipt, function(error, response) {
      if(!error){
        //message deleted
      }
    });
  }
});
