<?php

require_once('db_connect.php');

if($_SERVER['REQUEST_METHOD']=='POST'){

    $output = array();

    $sql = "INSERT INTO tbl_events(eventName, 
                                   eventDescription, 
                                   eventDate, 
                                   eventTime, 
                                   eventVenue) 
                           VALUES ('".$_POST['name']."', 
                                   '".$_POST['desc']."', 
                                   '".$_POST['date']."',
                                   '".$_POST['time']."',
                                   '".$_POST['venue']."')";

    if (mysqli_query($con,$sql)){
        $sql = "SELECT MAX(eventID) AS eventID FROM tbl_events";

        $res = mysqli_query($con, $sql);

        if ($row = mysqli_fetch_assoc($res)) {
            $output['eventID'] = $row['eventID'];
            $eventID = $row['eventID'];
        }

        if (isset($eventID)) {
            $sql2 = "INSERT INTO tbl_participants(eventID, userID, ishost) VALUES('$eventID', '".$_POST['hostID']."', 1)";

            if (mysqli_query($con, $sql2))
                $output['success'] = 1;
            else
                $output['success'] = 0;
        } else
            $output['success'] = 0;

    } else{
        $output['success'] = 0;
    }

    echo json_encode($output);

    mysqli_close($con);
}