<?php

/**
James Bilbrey
CMSC491 - Mobile Computing
Spring 2017
Assignment 2

Login.php
This file recieves username and password.
Indicates if the combination does not match any record
Returns the "unique" id if the combination is valid
*/

$username = $_REQUEST["username"];
$password = $_REQUEST["password"];

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

if(!$result)
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

$query = "SELECT ID FROM $table WHERE username='$username' AND password='$password'";
$result = $mysqli->query($query);

if ($result->num_rows != 1)
{
	print("INVALIDLOGIN");
	$mysqli->close();
	exit;
}

$row = $result->fetch_assoc();
print("SUCCESS.".$row['ID']);
$mysqli->close();

?>
