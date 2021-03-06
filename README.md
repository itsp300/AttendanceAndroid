# Attendance Android
A handy app that allows you to view your attendance data.

## Required Endpoints
* ### /api/secure/__summary__
  * Brief summary of the students attendance records.
  * Called when user loads the summary page.
  * Incoming JSON
  ```javascript
  {
  "studentName": "name",
  "attendanceTotal": 10,
  "lectureTotal": 11,
  "missedTotal": 1
  }
  ```
* ### /api/secure/subjectAttendances
  * Displays a list of subjects and their associated totals.
  * Called when user loads the subject page.
  * Incoming JSON
  ```javascript
  {
    "subjectAttendances": 
    [
      {
        "subjectCode": "ITSP300",
        "attendanceTotal": 1,
        "lectureTotal": 2,
        "imageName": "image1.jpg"
      },
      {
        "subjectCode": "ITSP300",
        "attendanceTotal": 1,
        "lectureTotal": 2,
        "imageName": "image1.jpg"
      }
    ]
  }
  ```
* ### /api/secure/messages
  * Shows a list of messages received by the device like notifications.
  * Messages that are returned are saved on the database (Most likely will need to be changed to store messages locally).
  * Called when user loads the messages page.
  * Incoming JSON
  ```javascript
   {
   "messages": 
   [
     {
       "title": "ITOO312 Attendance Noted",
       "description": "You have successfully been registered for class ITOO312!",
     },
     {
       "title": "ITOO312 Attendance Noted",
       "description": "You have successfully been registered for class ITOO312!",
     }
   ]
  }
  ```
* ### /api/secure/notifications
  * Displays a list of new notifications.
  * Notifications do not need to persist in a database. Send and forget.
  * Called every second.
  * Incoming JSON
  ```javascript
  {
   "notifications": 
   [
     {
       "title": "ITOO312 Attendance Noted",
       "description": "You have successfully been registered for class ITOO312!",
       "icon": "normal"
     },
     {
       "title": "ITOO312 Attendance Noted",
       "description": "You have successfully been registered for class ITOO312!",
       "icon": "normal"
     }
   ]
  }
  ```
* ### /api/secure/qrCode
  * Sends the scanned QR code to the server.
  * Called when user has successfully scanned a QR code.
  * Outgoing JSON
  ```javascript
  {
    "qrCode": "458s8df7sd8f7sd8fsdsafd"
  }
   ```
