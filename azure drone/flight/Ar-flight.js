var azureQ = require("./AzureQueue");
var flightD = require("./flightDrone");


var comand = null;
var distance = 0;

function voice_Control() {
	
	azureQ.get(function(cmd_q){
		
		if(cmd_q != null){
			var command_array = cmd_q.split(" ");
			comand = command_array[0];
			distanceValidation(command_array[1], comand);
		}else{
			comand= cmd_q;
		}
		console.log(comand);
		console.log(distance);
		
	});
	if(comand == null || comand ==''){
			setTimeout(voice_Control, 3000);
	}
	else{
			console.log("flight entered");
			flightD.flight_cmd(comand, distance);
			comand = null;
			distance = 0.0;
		    setTimeout(voice_Control, 2000);
	}
}

function distanceValidation(x, command){
	switch (true) {
		case command == "hover":
			if(isNaN(x)){
				distance = 1000;
			}else{
				distance = x * 1000;
			}
			break;
			
		case command == "clockwise":
		case command == "counterclockwise":
		case command == "yaw":
				distance = 90;
			break;
		case command == "land":
		case command == "zero":
		case command == "takeoff":
			break;
		default:
			if(isNaN(x)){
				distance = 1;
			}else{
				distance = x;
			}
			break;
		
	}
}
voice_Control();

