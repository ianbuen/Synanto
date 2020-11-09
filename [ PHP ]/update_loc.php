<?php
//$_POST['lat'] = '120.1239324';
//$_POST['lon'] = '14.32523';
//$_SERVER['REQUEST_METHOD']= 'POST';
//$_POST['userID'] = '1';
    if($_SERVER['REQUEST_METHOD']=='POST'){
        require_once('db_connect.php');

        $lat = $_POST['lat'];
        $lon = $_POST['lon'];

        $userID = $_POST['userID'];

        $location = $lat . "," . $lon;

        $sql = "UPDATE tbl_participants SET userLocation='$location'
                WHERE userID = '$userID'";

        mysqli_query($con, $sql);

        mysqli_close($con);
    }
