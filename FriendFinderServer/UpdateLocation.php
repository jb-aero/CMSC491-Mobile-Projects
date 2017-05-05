<?php

/**
James Bilbrey
CMSC491 - Mobile Computing
Spring 2017
Assignment 2

UpdateLocation.php
This file recieves latitude and longitude data from a mobile device
and lazily authenticates it with a lazy "unique" key and the username of the user.

The latitude and longitude are stored, and then data from the table is sent back to
the requester as a JSON string.
*/

include "DistanceCheck.php";

$latitude = $_REQUEST["latitude"];
$longitude = $_REQUEST["longitude"];
$id = $_REQUEST["id"];
$username = $_REQUEST["username"];
$distance = $_REQUEST["distance"];

$table = "tracking";

$mysqli = new mysqli("localhost", "root", "", "friendfinder");

if($mysqli->connect_errno){
	echo "Could not connect to the database\n";
	echo "Error: ". $mysqli->connect_errno . "\n";
	$mysqli->close();
	exit;
}

$query = "SHOW TABLES WHERE `Tables_in_friendfinder` = '$table'";
$result = $mysqli->query($query);

if($result == false)
{
	/*$query = "CREATE TABLE $id(Date DATETIME PRIMARY KEY, Latitude DECIMAL(16,10), Longitude DECIMAL(16,10))";

	$result = $mysqli->query($query);
	if(!$result)
	{
		print("The table was not created " . $mysqli->error . "\n");
	}*/
	print("Table does not exist.");
	$mysqli->close();
	exit;
}

$query = "UPDATE $table SET latitude='$latitude', longitude='$longitude' WHERE ID='$id' AND username='$username'";
$result = $mysqli->query($query);
if(!$result)
{
	print("Data was not inserted into the table " . $mysqli->error . "\n");
	$mysqli->close();
	exit;
}

$jobs = array();

$query = "SELECT * FROM $table WHERE username='$username'";
$self = $mysqli->query($query)->fetch_assoc();

$query = "SELECT * FROM $table ORDER BY timestamp DESC";
$result = $mysqli->query($query);

while($row = $result->fetch_assoc())
{
	if ($row['username'] != $username)
	{
		$cdist = getDistance($self['latitude'], $self['longitude'], $row['latitude'], $row['longitude']);
		if ($cdist < $distance)
		{
			$job = array($row['username'],$row['timestamp'],$row['latitude'],$row['longitude'],$cdist);
			$jobs[] = json_encode($job);
		}
	}
}
print(json_encode($jobs));

$mysqli->close();

?>
