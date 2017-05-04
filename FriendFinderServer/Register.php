<?php

/**
James Bilbrey
CMSC491 - Mobile Computing
Spring 2017
Assignment 2

Register.php
This file recieves username, password, latitude, and longitude.
Indicates if the username is already in use.
Generates (lazily) a "unique" key for the user.
Uses the given latitude and longitude as the user's starting location.
Indicates successful registration.
*/

$username = $_REQUEST["username"];
$password = $_REQUEST["password"];
$latitude = $_REQUEST["latitude"];
$longitude = $_REQUEST["longitude"];

$table = "snacking";

$mysqli = new mysqli("localhost", "root", "", "friendfinder");

if($mysqli->connect_errno){
	echo "Could not connect to the database\n";
	echo "Error: ". $mysqli->connect_errno . "\n";
	$mysqli->close();
	exit;
}

$query = "SHOW TABLES WHERE `Tables_in_friendfinder` = '$table'";
$result = $mysqli->query($query);

if($result->num_rows == 0)
{
	$query = "CREATE TABLE $table (
			username char(32) NOT NULL,
			password char(32) NOT NULL,
			ID char(32) NOT NULL,
			timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
			latitude double(16,10) NOT NULL,
			longitude double(16,10) NOT NULL)";

	$result = $mysqli->query($query);
	if(!$result)
	{
		print("The table was not created " . $mysqli->error . "\n");
		$mysqli->close();
		exit;
	}
}

$query = "SELECT username FROM $table WHERE username='$username'";
$result = $mysqli->query($query);
if ($result->num_rows == 1)
{
	print("ALREADYEXISTS");
	$mysqli->close();
	exit;
}

$id = md5($username.$password);

$query = "INSERT INTO $table (username, password, ID, latitude, longitude)
		VALUES ('$username', '$password', '$id', '$latitude', '$longitude')";
$result = $mysqli->query($query);
if(!$result)
{
	print("Data was not inserted into the table: " . $mysqli->error . "\n");
	$mysqli->close();
	exit;
}

print("SUCCESSFUL");
$mysqli->close();

?>
