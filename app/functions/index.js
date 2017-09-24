const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.EventChat = functions.database.ref('/Events/{event_id}/Chat/{chat_id}').onWrite( event => {

  const event_id = event.params.event_id;
  const original = event.data.val();
  const chat_id = event.params.chat_id;

  var event_ref = admin.database().ref("/Events/" + event_id +"/").once("value");
  return event_ref.then(function(snapshot){
    var t = snapshot.val();
    var message = original.name + ": " + original.text;



   const payload = {
     "data" : {
       "title": t.event_name,
       "body" : message,
       "timeStamp" : original.timeStamp
     }
   }


  var attendee_ref = admin.database().ref("/Events/" + event_id + "/Attendee")
                        .once("value", function(snapshot){
                          snapshot.forEach(function(child){
                            getTokenId(child.key);
                          });
                        });

  function getTokenId(username){
  var tokenID = admin.database().ref("/Notifications/" + username + "/deviceToken")
                    .once("value", function(snapshot){
                      snapshot.forEach(function(child){
                        sendMessage(child.key);
                      });
                    });
                  }

  function sendMessage(tokenID){
    return admin.messaging().sendToDevice(tokenID, payload).then(response => {
                              console.log("Event Chat Notification.");
                            });
                            }
   });
  });

exports.FriendRequest = functions.database.ref("/Users/{username}/Inbox/Add_Request/{requester}").onWrite( event => {

  const username = event.params.username;
  const requester = event.params.requester;

  const message = requester + " would like to add you!";


  const payload = {

    "notification" : {
      "title": "Friend Request",
      "body" :  message,
      "icon" : "default"
    }
  }

  function sendMessage(tokenID){
    return admin.messaging().sendToDevice(tokenID, payload).then(response => {
                              console.log("Friend Request sent.");
                            });

  }

  var deviceToken = admin.database().ref("/Users/" + username + "/DeviceToken")
                    .once("value", function(snapshot){
                      snapshot.forEach(function(child){
                          sendMessage(child.key);
                        });
                      });
});

exports.EventRequest = functions.database.ref("/Notifications/{username}/Event_Request/{event_id}/{requester}").onWrite( event => {

  const username = event.params.username;
  const requester = event.params.requester;
  const event_id = event.params.event_id;


  function sendMessage(tokenID){

  var event_ref = admin.database().ref("/Notifications/" + username + "/Event_Request/" + event_id + "/" + requester).once("value");

  return event_ref.then(result => {

      const event_name = result.val();

      const message = requester + " invited you to: " + event_name + ".";

      const payload = {
      "notification" : {
        "title": "Event Invite",
        "body": message,
        "icon": "default"
        }
      }

        return admin.messaging().sendToDevice(tokenID, payload).then(response => {
        console.log("Event Request sent.");
      });
    });
  }

  var deviceToken = admin.database().ref("/Notifications/" + username + "/deviceToken")
                    .once("value", function(snapshot){
                      snapshot.forEach(function(child){
                            sendMessage(child.key);
                          });
                        });
                      })
