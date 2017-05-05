This project consists of two components: an android application and a webserver.
The application sends user-generated data to the server, which allows interaction with others.
Due to time constraints, the app sends data over HTTPget instead of HTTPSpost.
The server responds to requests with information relevant to the current activity.
On the Map screen, the app listens for location updates and upon recieving one, sends the location to the server.
The server then replies with the last recorded locations of all users within a specified range.
By default this is 1km, but can be adjusted with the input box in the upper right.
The database consists of one table, which is set up when the first user registers.


In order to set up the server, create a database and modify the connection information at the top of each file.
To set up the app, open InvokeWebserver.java and change "localhost" in the connection string to the IP of the server.
The users table will be set up automatically when the first user registers.

Youtube demo: https://youtu.be/uZXCRQ7BodU
