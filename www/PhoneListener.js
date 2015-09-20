var PhoneListener = function() {};

PhoneListener.prototype.monitorSignal = function(success, failure) {
	cordova.exec(success, failure, "PhoneListener", "monitor_signal", []);
};

PhoneListener.prototype.getSimInfo = function(success, failure) {
	cordova.exec(success, failure, "PhoneListener", "sim_info", []);
};

// Plug in to Cordova
cordova.addConstructor(function() {

	if (!window.Cordova) {
		window.Cordova = cordova;
	};


	if (!window.plugins) window.plugins = {};
	window.plugins.PhoneListener = new PhoneListener();

});
