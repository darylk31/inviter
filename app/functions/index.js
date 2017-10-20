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
       "type": "EventChat",
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
  var tokenID = admin.database().ref("/Users/" + username + "/Device_Token")
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
    "data" : {
      "type" : "FriendRequest",
      "body" :  message
    }
  }

  function sendMessage(tokenID){
    return admin.messaging().sendToDevice(tokenID, payload).then(response => {
                              console.log("Friend Request sent.");
                            });

  }

  var deviceToken = admin.database().ref("/Users/" + username + "/Device_Token")
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



  var deviceToken = admin.database().ref("/Users/" + username + "/Device_Token")
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
      "data" : {
        "type": "EventRequest",
        "body": message
        }
      }

        return admin.messaging().sendToDevice(tokenID, payload).then(response => {
        console.log("EventRequest : ", message);
      });
    });
  }
 })

 exports.PersonalChat = functions.database.ref("/Personal_Chat/{event_id}/Chat/{chat_id}").onWrite(event => {

   const event_id = event.params.event_id;
   const chat_id = event.params.chat_id;
   const message = event.data.val();

   var member_ref = admin.database().ref("/Personal_Chat/" + event_id + "/Members").once("value", function(snapshot){
     snapshot.forEach(function(child){
       updateUser(child.key);
       getTokenId(child.key);
     })
   });

   function updateUser(username){
     var user_event_ref = admin.database().ref("/Users/" + username + "/Events/" + event_id );
     var time = {last_modified: message.timeStamp};
     user_event_ref.update(time);
   }

   function getTokenId(username){
   if (username != message.name){
     var tokenID = admin.database().ref("/Users/" + username + "/Device_Token")
                     .once("value", function(snapshot){
                       snapshot.forEach(function(child){
                         sendMessage(child.key);
                       });
                     });
                   }
                 }

  function sendMessage(tokenID){
    const payload = {
     "data" : {
       "type": "PersonalChat",
       "title": message.name,
       "body" : message.text,
       "PersonalChatID" : event_id
     }
   }
    return admin.messaging().sendToDevice(tokenID, payload).then(response => {
                              console.log("Token id:%s Recieved Chat Notification.",tokenID);
                            });
                          }

 })
