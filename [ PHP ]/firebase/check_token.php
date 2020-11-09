<?php 
// $_SERVER['REQUEST_METHOD']= 'GET';
// $_GET['userID'] = '1';

	require_once('../db_connect.php');
	
    if($_SERVER['REQUEST_METHOD']=='GET'){

        $userID = $_GET['uid'];

        $sql = "SELECT fcmToken
				FROM tbl_accounts
				WHERE userID = '$userID'
				AND fcmToken IS NOT NULL";

        $table = mysqli_query($con, $sql);
		
		$json = array();
		$json['result'] = 0;
		
		if ($table->num_rows > 0) {
			$json['result'] = 1;
		}
		
		echo json_encode($json);

    }
	
	mysqli_close($con);