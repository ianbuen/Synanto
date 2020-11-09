<?php

require_once('db_connect.php');

//$_SERVER['REQUEST_METHOD'] = 'GET';
//$_GET['userID'] = 4;

if($_SERVER['REQUEST_METHOD']=='POST'){
    $userID = $_POST['userID'];
    $eventID = $_POST['eventID'];

    $result = array();

    // Check if already joined.
    $sql = "SELECT * FROM tbl_participants WHERE userID = $userID AND eventID = $eventID";

    // If not, let 'em join.
    if (mysqli_query($con, $sql)->num_rows == 0) {
        $sql = "INSERT INTO tbl_participants(eventID, userID, isHost)
                VALUES('$eventID', '$userID', '0')";

        if (mysqli_query($con, $sql)) {
            $result['success'] = 1;
        }
    } else {
        $result['success'] = 0;
    }
    
    echo json_encode($result);

    mysqli_close($con);
}
