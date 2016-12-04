var azure = require('azure-storage');
var acct = require('../index');
//process.env.AZURE_STORAGE_ACCOUNT = '';
//process.env.AZURE_STORAGE_ACCESS_KEY = '';
//var queueName = "";

var queueName = acct.queueName;
var queueService = azure.createQueueService();



queueService.createQueueIfNotExists(queueName, function(error) {
	if (error) {
			throw error;
	}
});

module.exports = {
	get: function(callback){
		queueService.getMessages(queueName, function(error, serverMessages) {
			if (!error) {
    
				if(serverMessages[0] != null){
					var message = serverMessages[0];
					var cmd = (Buffer.from(message.messageText, 'base64')).toString('ascii');;
					console.log(cmd);
				
					queueService.deleteMessage(queueName, message.messageId, message.popReceipt, function(error) {
						if (error) {
							throw error;
						}
					});
					callback(cmd);
				}
				else{
					  callback(null);
				}
			}
		});
	},
	add: function(item){
		queueService.createMessage(queueName, item, function(error) {
			if (error) {
				throw error;
			}
		});
	}
}