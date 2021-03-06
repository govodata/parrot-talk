var async = require('async')
  , fs    = require('fs')
  ;

module.exports = Mission;
function Mission(client, controller, options) {

    options = options || {};

    this._options   = options;
    this._client    = client;
    this._control   = controller;

}

Mission.prototype.client = function() {
    return this._client;
}

Mission.prototype.control = function() {
    return this._control;
}

Mission.prototype.run = function(callback) {
    //async.waterfall(this._steps, callback);
}

Mission.prototype.log = function(path) {
    var dataStream = fs.createWriteStream(path);
    var ekf = this._control._ekf;

    this._control.on('controlData', function(d) {
        var log = (d.state.x + "," +
                   d.state.y + "," +
                   d.state.z + "," +
                   d.state.yaw + "," +
                   d.state.vx + "," +
                   d.state.vy + "," +
                   d.goal.x + "," +
                   d.goal.y + "," +
                   d.goal.z + "," +
                   d.goal.yaw + "," +
                   d.error.ex + "," +
                   d.error.ey + "," +
                   d.error.ez + "," +
                   d.error.eyaw + "," +
                   d.control.ux + "," +
                   d.control.uy + "," +
                   d.control.uz + "," +
                   d.control.uyaw + "," +
                   d.last_ok + "," +
                   d.tag);

        if (d.tag > 0) {
            log = log + "," +
                  ekf._s.x + "," +
                  ekf._s.y + "," +
                  ekf._s.yaw.toDeg() + "," +
                  ekf._m.x + "," +
                  ekf._m.y + "," +
                  ekf._m.yaw.toDeg() + "," +
                  ekf._z.x + "," +
                  ekf._z.y + "," +
                  ekf._z.yaw.toDeg() + "," +
                  ekf._e.x + "," +
                  ekf._e.y + "," +
                  ekf._e.yaw.toDeg()
        } else {
            log = log + ",0,0,0,0,0,0"
        }

        log = log + "\n";

        dataStream.write(log);
    });
}

Mission.prototype.takeoff = function(cb) {
    var self = this;
        self._client.takeoff(cb);


    return this;
}

Mission.prototype.land = function(cb) {
    var self = this;
        self._client.land(cb);

    return this;
}

Mission.prototype.hover = function(delay,cb) {
    var self = this;
        self._control.hover();
        setTimeout(cb, delay);

    return this;
}

Mission.prototype.wait = function(delay, cb) {
        setTimeout(cb, delay);

    return this;
}

Mission.prototype.task = function(task, cb) {
        task(cb);

    return this;
}

Mission.prototype.taskSync = function(task,cb) {
        task();
        cb();

    return this;
}

Mission.prototype.zero = function() {
    var self = this;
        self._control.zero(cb);
        cb();

    return this;
}

Mission.prototype.go = function(goal, cb) {
    var self = this;
        self._control.go(goal, cb);

    return this;
}

Mission.prototype.forward = function(distance,cb) {
    var self = this;
        self._control.forward(distance, cb);

    return this;
}

Mission.prototype.backward = function(distance,cb) {
    var self = this;
        self._control.backward(distance, cb);

    return this;
}

Mission.prototype.left = function(distance,cb) {
    var self = this;
        self._control.left(distance, cb);

    return this;
}

Mission.prototype.right = function(distance, cb) {
    var self = this;
        self._control.right(distance, cb);

    return this;
}

Mission.prototype.up = function(distance,cb) {
    var self = this;
        self._control.up(distance, cb);

    return this;
}

Mission.prototype.down = function(distance, cb) {
    var self = this;
        self._control.down(distance, cb);

    return this;
}

Mission.prototype.cw = function(angle,cb) {
    var self = this;
        self._control.cw(angle, cb);

    return this;
}

Mission.prototype.ccw = function(angle,cb) {
    var self = this;
        self._control.ccw(angle, cb);

    return this;
}

Mission.prototype.altitude = function(altitude,cb) {
    var self = this;
        self._control.altitude(altitude, cb);

    return this;
}

Mission.prototype.yaw = function(angle,cb) {
    var self = this;
        self._control.yaw(angle,cb);

    return this;
}
