<?php
	require_once('db_connect.php');

// $_POST['id'] = '37';
// $_POST['name'] = 'SUCCESS';
// $_POST['description'] = 'GUMANA YUNG SQL HANEP';
// $_POST['date'] = '02/25/2017 (Sunday)';
// $_POST['time'] = '11:00 AM';
// $_POST['venue'] = 'G/F Canteen';

// $_SERVER['REQUEST_METHOD']= 'POST';

if($_SERVER['REQUEST_METHOD']=='POST'){

	$id = $_POST['id'];
	$name = $_POST['name'];
	$description = $_POST['description'];
	$date = $_POST['date'] ;
	$time = $_POST['time'] ;
	$venue = $_POST['venue'];

	$sql = "UPDATE tbl_events 
			SET eventName='$name',
				eventDescription='$description',
				eventDate='$date',
				eventTime='$time',
				eventVenue='$venue'
			WHERE eventID = '$id'";

	$json = array();
			
	if (mysqli_query($con, $sql) > 0) {
		$json['success'] = 1;
	} else {
		$json['success'] = 0;
	}
	
	echo json_encode($json);

	mysqli_close($con);
}
