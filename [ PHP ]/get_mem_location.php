<?php 
// $_SERVER['REQUEST_METHOD']= 'GET';
// $_POST['userID'] = '1';

    if($_SERVER['REQUEST_METHOD']=='GET'){
        require_once('db_connect.php');

        $userID = $_GET['userID'];

        $sql = "SELECT userLocation
				FROM tbl_participants
				WHERE userID = '".$userID."'";

        $table = mysqli_query($con, $sql);
		
		$json = array();
		
		if ($table->num_rows > 0) {
			$row = $table->fetch_assoc();
			$json['userLocation'] = $row['userLocation'];
		}
		
		echo json_encode($json);

        mysqli_close($con);
    }
