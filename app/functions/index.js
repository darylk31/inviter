const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.EventChat = functions.database.ref('/Notifications/{username}/event_chat/{event_id}').onWrite( event => {

  const username = event.params.username;
  const event_id = event.params.event_id;

  console.log("Notification to: ", username);
  console.log("Update from event: ", event_id);


  var tokenID = admin.database().ref("/Notifications/" + username + "/deviceToken")
                    .once("value", function(snapshot){
                      snapshot.forEach(function(child){
                        console.log("Child Key is: ", child.key);
                      });
                    });
  });

exports.FriendRequest = functions.database.ref("/Notifications/{username}/Add_Request/{requester}").onWrite( event => {

  const username = event.params.username;
  const requester = event.params.requester;

  const message = requester + " would like to add you!";


  const payload = {
    "notification" : {
      "title": "Friend Request",
      "body" : message,
      "icon" : "default"
    }
  }

  admin.database().ref("/Notifications/" + username + "/deviceToken")
                    .once("value", function(snapshot){
                      snapshot.forEach(function(child){
                        return admin.messaging().sendToDevice(child.key, payload);
                        console.log(child.key, "Message: ", message);
                      });
                    });
});

exports.EventRequest = functions.database.ref("/Notifications/{username}/Event_Request/{event_id}/{requester}").onWrite( event => {

  const username = event.params.username;
  const requester = event.params.requester;
  var value = admin.database().ref("/Notifications/" + username + "/Event_Request/" + requester).once("value");

  const message = requester + "invited you to: " + value + ".";

  const payload = {
    "notification" : {
      "title": "Event Invite",
      "body": message,
      "icon": "default"
    }
  }

  admin.database().ref("/Notifications/" + username + "/deviceToken")
                    .once("value", function(snapshot){
                      snapshot.forEach(function(child){
                        return admin.messaging().sendToDevice(child.key, payload);
                        console.log(child.key, "Message: ", message);
                      });
                    });
})
