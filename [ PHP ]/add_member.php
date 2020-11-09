<?php

    require_once('db_connect.php');

    if($_SERVER['REQUEST_METHOD']=='POST'){
        $event = $_POST['eventID'];
        $user = $_POST['userID'];

        $check = "SELECT eventID, userID FROM tbl_participants WHERE eventID='$event' AND userID='$user'";

        $sql = "INSERT INTO tbl_participants(eventID, userID, isHost) 
                VALUES('$event', '$user', '0')";

        $output = array();

        $res = mysqli_query($con, $check);
        if ($res->num_rows > 0) {
            $output['success'] = 0;
            $output['message'] = " is already in the group.";
        } else {
            if (mysqli_query($con,$sql)){
                $output['success'] = 1;
            } else {
                $output['success'] = 0;
                $output['message'] = " is already in the group.";
            }
        }

        echo json_encode($output);

        mysqli_close($con);
    }
