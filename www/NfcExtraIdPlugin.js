var exec = require('cordova/exec');

module.exports = {
            show: function(success, error,action){
                exec(success, error, "NfcExtraIdPlugin", action, []);
            }
};
