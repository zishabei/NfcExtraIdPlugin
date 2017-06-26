A plugin for get NFC extra_id.  
==============================
NOTIFY: `Used to android`
--------------------------

Install:
-----------
cordova plugin add nfcextraidplugin

Method  
------  
* cordova.plugins.NfcExtraIdPlugin.show(success,error,action);

Parameters  
-----------
* `success`: The callback that is called when NFC is started or stoped success.  
* `error`: The callback that is called when NFC is started or stoped fail.
* `action`:You need to pass in a parameter of type  String.When  

Description  
------------
Function `cordova.plugins.NfcExtraIdPlugin.show(success,error,action)` checks to see if the phone has NFC  
and if NFC is enabled.If everything is OK, and the `antion` is `"extra_id"`,the success callback is called with a message  
of "Start of read task".If you pass in any String to replace `action`,the success callback is called with a message  
of "Stop of read task".  

Example :  
---------
* This method is used to start read NFC EXTRA_ID.
```javascript
function methodStart(){
    cordova.plugins.NfcExtraIdPlugin.show(success,error,"extra_id");
    function success(message){
        alert(message);
    };
    function error(message){
        alert(message);
    };
}
```

* This method is used to stop read NFC EXTRA_ID.
```javascript
function methodStop(){
    cordova.plugins.NfcExtraIdPlugin.show(success,error,"stop");
    function success(message){
        alert(message);
    };
    function error(message){
        alert(message);
    };
}
```
