var autonomy = require('ardrone-autonomy');
var mission  = autonomy.createMission();

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
module.exports = {
	flight_cmd: function(flight_command, distance){
		console.log(flight_command);

			switch (true){

				case /^takeoff/.test(flight_command):
					console.log("• doing takeoff");
					mission.takeoff();
					break;

				case /^land/.test(flight_command):
					console.log("• doing land");
					mission.land();
					break;

				case /^altitude/.test(flight_command):
					console.log("• doing altitude");
					mission.altitude(distance);
					break;

				case /^yaw/.test(flight_command):
					console.log("• doing right");
					mission.yaw(distance);
					break;

				case /^clockwise/.test(flight_command):
					console.log("• doing clockwise");
					mission.cw(distance);
					break;

				case /^counterclockwise/.test(flight_command):
					console.log("• counterclockwise");
					mission.ccw(distance);

				case /^forward/.test(flight_command):
					console.log("• doing forward");
					mission.forward(distance);
					break;

				case /^backward/.test(flight_command):
					console.log("• doing backward");
					mission.backward(distance);
					break;

				case /^left/.test(flight_command):
					console.log("• doing left");
					mission.left(distance);
					break;

				case /^right/.test(flight_command):
					console.log("• doing right");
					mission.right(distance);
					break;

				case /^up/.test(flight_command):
					console.log("• doing up");
					mission.up(distance);
					break;

				case /^down/.test(flight_command):
					console.log("• doing down");
					mission.down(distance);
					break;

			}

	}

}
