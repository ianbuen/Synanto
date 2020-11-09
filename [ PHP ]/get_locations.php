<?php 
// $_SERVER['REQUEST_METHOD']= 'GET';
// $_POST['userID'] = '1';
	require_once('db_connect.php');

    if($_SERVER['REQUEST_METHOD']=='GET'){

        $eventID = $_GET['eventID'];
        $userID = $_GET['userID'];

        $sql = "SELECT CONCAT(tbl_accounts.firstname, ' ', tbl_accounts.lastname) AS name,
		   tbl_participants.userLocation
		   FROM tbl_participants INNER JOIN tbl_accounts ON tbl_accounts.userID = tbl_participants.userID
		   WHERE tbl_participants.eventID = '$eventID' AND tbl_accounts.userID != '$userID'
		   ORDER BY tbl_participants.isHost DESC";
		
		$json = array();
		
		$res = mysqli_query($con, $sql);
		
		if ($res->num_rows > 0) {
			for ($i = 0; $row = $res->fetch_assoc(); $i++) {
				 $json[$i] = $row;
			}
		}
		
		echo json_encode($json);

        mysqli_close($con);
    }
