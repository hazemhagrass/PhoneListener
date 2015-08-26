/**
 * @constructor
 */
var PhoneListener = function() {};

PhoneListener.prototype.monitor = function(success, failure) {
	cordova.exec(function() {}, failure, "PhoneListener", "monitor", []);
};

// Plug in to Cordova
cordova.addConstructor(function() {

	if (!window.Cordova) {
		window.Cordova = cordova;
	};


	if (!window.plugins) window.plugins = {};
	window.plugins.PhoneListener = new PhoneListener();
});