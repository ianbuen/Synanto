<?php

require_once('db_connect.php');

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    $eventID = $_GET['eventID'];

    $output = array();

    // GET ALL Event Details
    $sql = "SELECT * FROM tbl_events WHERE eventID = ". $eventID;

    $table = mysqli_query($con, $sql);

    $event = array();

    if ($table->num_rows > 0) {
        $row = $table->fetch_assoc();
        $event['name'] = $row['eventName'];
        $event['desc'] = $row['eventDescription'];
        $event['date'] = $row['eventDate'];
        $event['time'] = $row['eventTime'];
        $event['venue'] = $row['eventVenue'];
        $event['members'] = array();
    }


    // Get ALL MEMBERS
    $sql = "SELECT tbl_participants.userID, 
				   CONCAT(tbl_accounts.lastname, ', ', tbl_accounts.firstname) AS 'name', 
			       tbl_participants.isHost
		    FROM (tbl_accounts INNER JOIN tbl_participants 
            ON tbl_accounts.userID = tbl_participants.userID) 
            INNER JOIN tbl_events ON tbl_events.eventID = tbl_participants.eventID 
            WHERE tbl_events.eventID = $eventID
			ORDER BY tbl_participants.isHost DESC";

    $res = mysqli_query($con, $sql);

    if ($res->num_rows > 0) {
        for ($i = 0; $row = $res->fetch_assoc(); $i++) {
            $event['members'][$i] = $row;
        }
    }

    echo json_encode($event);

    mysqli_close($con);
}
