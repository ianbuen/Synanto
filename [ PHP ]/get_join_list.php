<?php
    require_once('db_connect.php');

    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	$userID = $_GET['userID'];
				
	$sql = "SELECT * FROM tbl_events INNER JOIN tbl_participants 
		ON tbl_events.eventID = tbl_participants.eventID
		WHERE tbl_events.eventID NOT IN
			  (SELECT tbl_events.eventID FROM tbl_events INNER JOIN tbl_participants 
			   ON tbl_events.eventID = tbl_participants.eventID   
			   WHERE tbl_participants.userID = $userID)
		AND str_to_date(CONCAT(SUBSTRING_INDEX(eventDate, ' ', 1), ' ', eventTime), '%m/%d/%Y %l:%i %p') > NOW()
		GROUP BY tbl_events.eventID";

        $r = mysqli_query($con,$sql);

        //creating a blank array
        $details = array();

        //looping through all the records fetched
        while($row = mysqli_fetch_array($r)){

            //Pushing name and id in the blank array created
            array_push($details,array(
                "eventID"=>$row['eventID'],
                "eventName"=>$row['eventName'],
                "eventDescription"=>$row['eventDescription'],
                "eventDate"=>$row['eventDate'],
                "eventTime"=>$row['eventTime'],
                "eventVenue"=>$row['eventVenue']
            ));
        }


        $result = array();
        $result['details'] = $details;        
        
        //Displaying the array in json format
        header('Content-Type: text/javascript');
    	echo json_encode($result, JSON_PRETTY_PRINT);

        mysqli_close($con);
    }
