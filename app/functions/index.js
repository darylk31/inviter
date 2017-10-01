const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.EventChat = functions.database.ref('/Events/{event_id}/Chat/{chat_id}').onWrite( event => {

  const event_id = event.params.event_id;
  const original = event.data.val();
  const chat_id = event.params.chat_id;

  var event_ref = admin.database().ref("/Events/" + event_id).once("value");
  return event_ref.then(function(snapshot){
    var event_snapshot = snapshot.val();
    var message = original.name + ": " + original.text;
   const payload = {
     "data" : {
       "title": event_snapshot.event_name,
       "body" : message,
       "eventID" : event_id
     }
   }

  var attendee_ref = admin.database().ref("/Events/" + event_id + "/Attendee")
                        .once("value", function(snapshot){
                          snapshot.forEach(function(child){
                            getTokenId(child.key);
                            updateUser(child.key);
                          });
                        });

  function getTokenId(username){
  if (username != original.name){
  console.log("Orignial %s sending to %s", original.name,username);
  var tokenID = admin.database().ref("/Users/" + username + "/DeviceToken")
                    .once("value", function(snapshot){
                      snapshot.forEach(function(child){
                        sendMessage(child.key);
                      });
                    });
                   }
                  }

  function updateUser(username){
    var event_ref = admin.database().ref("/Users/" + username + "/Events/" + event_id );
    var time = {last_modified: event_snapshot.last_modified};
    console.log("%s updated time : %s",event_id,time);
    event_ref.update(time);
    if (username != original.name){
    var unread_ref = event_ref.child("unread_messages");
    unread_ref.transaction(function(unread_number){
        return (unread_number || 0) + 1;})
    }
  }

  function sendMessage(tokenID){
    return admin.messaging().sendToDevice(tokenID, payload).then(response => {
                              console.log("Token id:%s Recieved Chat Notification.",tokenID);
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

exports.EventRequest = functions.database.ref("/Users/{username}/Inbox/Event_Request/{event_id}").onWrite( event => {

  const username = event.params.username;
  const event_id = event.params.event_id;
  const requester = event.data.val();



  var deviceToken = admin.database().ref("/Users/" + username + "/DeviceToken")
                    .once("value", function(snapshot){
                      snapshot.forEach(function(child){
                            sendMessage(child.key);
                          });
                        });


  function sendMessage(tokenID){
  var event_ref = admin.database().ref("/Events/"+ event_id ).once("value");
  return event_ref.then(result => {
      const event = result.val();
      const message = requester + " invited you to: " + event.event_name + ".";

      const payload = {
      "notification" : {
        "title": "Event Invite",
        "body": message,
        "icon": "default"
        }
      }

        return admin.messaging().sendToDevice(tokenID, payload).then(response => {
        console.log("EventRequest : ", message);
      });
    });
  }
                      })
