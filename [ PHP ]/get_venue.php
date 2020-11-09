<?php

require_once('db_connect.php');

// $_GET['eventID'] = 17;
// $_SERVER['REQUEST_METHOD'] = 'GET';
if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    $eventID = $_GET['eventID'];

    $sql = "SELECT eventVenue FROM tbl_events WHERE eventID='$eventID'";

    $r = mysqli_query($con, $sql);

    $venue = array();

    if ($r->num_rows > 0) {
        $row = $r->fetch_assoc();
        $venue['venue'] = $row['eventVenue'];
    }

    echo json_encode($venue);

    mysqli_close($con);
}