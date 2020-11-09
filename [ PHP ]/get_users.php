<?php

require_once('db_connect.php');

//$_SERVER['REQUEST_METHOD'] = 'GET';
//$_GET['eventID'] = 7;
//$_GET['userID'] = 1;

if($_SERVER['REQUEST_METHOD']=='GET'){
    $userID = $_GET['userID'];
    $eventID = $_GET['eventID']; 
    
    $sql = "SELECT DISTINCT tbl_accounts.userID, CONCAT(tbl_accounts.lastname, ', ', tbl_accounts.firstname) AS 'fullname' 
            FROM tbl_accounts WHERE tbl_accounts.userID != $userID";

    $res = mysqli_query($con,$sql);

    $output = array();

    $i = 0;
    if ($res->num_rows > 0){
        while ($row = $res->fetch_assoc()) {
            $output['user'][$i] = $row;
            $i++;
        }
    }

    echo json_encode($output);

    mysqli_close($con);
}
