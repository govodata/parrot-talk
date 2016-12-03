var autonomy = require('ardrone-autonomy');
var mission  = autonomy.createMission();

module.exports = {
	flight_cmd: function(flight_command, distance){
		console.log(flight_command);
			
			//flight_command = b;
			//b=null;
			var exiting = false;
			process.on('SIGINT', function() {
				if (exiting) {
					process.exit(0);
				} else {
					console.log('Got SIGINT. Landing, press Control-C again to force exit.');
					exiting = true;
					mission.control().disable();
					mission.client().land(function() {
						process.exit(0);
					});
				}
			});
			switch (true){
				
				case /^takeoff/.test(flight_command):
					console.log("• doing takeoff");
					mission.takeoff();
					//flyDrone();
					//setTimeout(function1, 1000);
					break;
				
				case /^clockwise/.test(flight_command):
					console.log("• doing clockwise");
					mission.cw(distance);
					//flyDrone();
					//setTimeout(function1, 1000);
					break;
				
				case /^counterclockwise/.test(flight_command):
					console.log("• counterclockwise");
					mission.ccw(distance);
					//setTimeout(function1, 1000);
					break;
				
				case /^land/.test(flight_command):
					console.log("• doing land");
					mission.land();
					//flyDrone();
					break;
				
				case /^altitude/.test(flight_command):
					console.log("• doing altitude");
					mission.altitude(distance);
					//setTimeout(function1, 1000);
					break;
					
				case /^forward/.test(flight_command):
					console.log("• doing forward");
					mission.forward(distance);
					//setTimeout(function1, 1000);
					break;
					
				case /^backward/.test(flight_command):
					console.log("• doing backward");
					mission.backward(distance);
					//setTimeout(function1, 1000);
					break;
				
				case /^left/.test(flight_command):
					console.log("• doing left");
					mission.left(distance);
					//setTimeout(function1, 1000);
					break;
					
				case /^right/.test(flight_command):
					console.log("• doing right");
					mission.right(distance);
					//setTimeout(function1, 1000);
					break;
					
				case /^yaw/.test(flight_command):
					console.log("• doing right");
					mission.yaw(distance);
					//setTimeout(function1, 1000);
					break;

			}
			
	}
	
}
