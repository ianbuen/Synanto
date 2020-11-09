<?php

if($_SERVER['REQUEST_METHOD']=='POST'){
    require_once('db_connect.php');

    $sql = "SELECT userID, username, password FROM tbl_accounts WHERE username='".$_POST['username']."' AND password='".$_POST['password']."'";

    $result = mysqli_query($con, $sql);

    $response = array();

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();

        $response['success'] = 1;
        $response['userID'] = $row['userID'];
    } else
        $response['success'] = 0;

    echo json_encode($response);

    mysqli_close($con);
}
