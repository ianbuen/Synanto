<?php 
// $_SERVER['REQUEST_METHOD']= 'POST';
// $_POST['userID'] = '6';
// $_POST['eventID'] = '30';

	require_once('../db_connect.php');
	
    if($_SERVER['REQUEST_METHOD']=='POST'){

        $userID = $_POST['userID'];
        $eventID = $_POST['eventID'];

        $sql = "SELECT fcmToken 
				FROM `tbl_accounts` INNER JOIN `tbl_participants` 
				ON `tbl_accounts`.`userID`=`tbl_participants`.`userID`
				WHERE tbl_accounts.userID != $userID 
				AND tbl_participants.eventID = $eventID";

        $table = mysqli_query($con, $sql);
		
		$json = array();
		
		if ($table->num_rows > 0){
			while ($row = $table->fetch_assoc()) {
				$json[] = $row['fcmToken'];
			}
		}
		
		echo json_encode($json);

    }
	
	mysqli_close($con);