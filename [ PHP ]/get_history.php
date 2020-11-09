<?php

require_once('db_connect.php');

//$_SERVER['REQUEST_METHOD'] = 'GET';
//$_GET['userID'] = 1;

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    $userID = $_GET['userID'];

    $output = array();

    // SQL QUERY FOR HOSTED EVENTS
    $sql = "SELECT tbl_events.eventID, tbl_events.eventName, tbl_events.eventDate, tbl_events.eventTime
                FROM tbl_events INNER JOIN tbl_participants 
	            ON tbl_events.eventID = tbl_participants.eventID
                WHERE tbl_participants.isHost = 1 AND 
                tbl_participants.userID = $userID AND
				str_to_date(CONCAT(SUBSTRING_INDEX(eventDate, ' ', 1), ' ', eventTime), '%m/%d/%Y %l:%i %p') < NOW()";

    $res = mysqli_query($con, $sql);

    if ($res->num_rows > 0) {
        for ($i = 0; $row = $res->fetch_assoc(); $i++) {
            $output[$i] = $row;
        }
    }

//    header('Content-Type: text/javascript');
//    echo json_encode($output, JSON_PRETTY_PRINT);
    echo json_encode($output);

    mysqli_close($con);
}