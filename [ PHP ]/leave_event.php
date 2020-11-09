<?php

require_once('db_connect.php');

//$_SERVER['REQUEST_METHOD'] = 'GET';
//$_GET['userID'] = 4;

if($_SERVER['REQUEST_METHOD']=='GET'){
    $userID = $_GET['userID'];
    $eventID = $_GET['eventID'];

    // Check if already joined.
    $sql = "DELETE FROM tbl_participants WHERE userID = $userID AND eventID = $eventID";

	$result = array();
	
    // If not, let 'em join.
    if (mysqli_query($con, $sql) > 0) {
        $result['success'] = 1;
    } else {
        $result['success'] = 0;
    }

    echo json_encode($result);

    mysqli_close($con);
}
