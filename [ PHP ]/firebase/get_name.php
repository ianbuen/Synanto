<?php 
// $_SERVER['REQUEST_METHOD']= 'POST';
// $_POST['userID'] = '6';
// $_POST['eventID'] = '30';

	require_once('../db_connect.php');
	
    if($_SERVER['REQUEST_METHOD']=='GET'){

        $userID = $_GET['id'];

        $sql = "SELECT CONCAT(firstname, ' ', lastname) AS fullname 
				FROM `tbl_accounts`
				WHERE userID=$userID";

        $table = mysqli_query($con, $sql);
		
		$json = array();
		
		if ($table->num_rows > 0){
			$row = $table->fetch_assoc();
			$json[] = $row['fullname'];
		}
		
		echo json_encode($json);

    }
	
	mysqli_close($con);