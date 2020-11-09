<?php

	//Defining Constants
	require_once('../db_connect.php');
	
	//Connecting to Database
	$con = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect');
	
	if($_SERVER['REQUEST_METHOD'] == 'POST'){
        $token = $_POST['token'];
	$userID = $_POST['userID'];

	$sql = "UPDATE tbl_accounts SET fcmToken='$token' WHERE userID='$userID'";

	$json = array();
	$json['success'] = 0;
		
        if (mysqli_query($con, $sql)) {
			$json['success'] = 1;
		}
		
		echo json_encode($json);
    }
	
	mysqli_close($con);
?>
