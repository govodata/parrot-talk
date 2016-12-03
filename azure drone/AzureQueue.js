var azure = require('azure-storage');

process.env.AZURE_STORAGE_ACCOUNT = 'parrottalkstorage';
process.env.AZURE_STORAGE_ACCESS_KEY = '2xBSgM7lGINalGRVz/5IgCWqQFDSdjG6lui2N+q9YIhqe/xE2OYY8V+DH0YDV/PzDQgZijZmBDDBuji4zXZmXA==';


var queueService = azure.createQueueService();
var queueName = "taskqueue";


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