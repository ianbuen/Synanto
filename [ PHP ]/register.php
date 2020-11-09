<?php
// $_SERVER['REQUEST_METHOD'] = 'POST';
// $_POST['user'] = 'ian';
// $_POST['pass'] = '123456';
// $_POST['first'] = 'ian';
// $_POST['last'] = 'buen';
// $_POST['fcmtoken'] = 'sample';
if($_SERVER['REQUEST_METHOD']=='POST'){
    require_once('db_connect.php');

    $json = array();

    $sqlUser = "SELECT `username` FROM tbl_accounts WHERE `username`='".$_POST['user']."'";
    $sqlEmail = "SELECT `email` FROM tbl_accounts WHERE `email`='".$_POST['email']."'";

    if (mysqli_query($con, $sqlUser)->num_rows > 0) {
        $json['code'] = 0;
        $json['message'] = "Username already in use.";
    } else if (mysqli_query($con, $sqlEmail)->num_rows > 0) {
        $json['code'] = 0;
        $json['message'] = "E-mail already in use.";
    } else {
        $sql = "INSERT INTO tbl_accounts(username, password, firstname, lastname, email) ".
               "VALUES('".$_POST['user']."', '".$_POST['pass']."', '".$_POST['first']."', '".$_POST['last']."', '".$_POST['email']."')";

        if (mysqli_query($con, $sql)) {
            $json['code'] = 1;
            $json['message'] = "Registration successful!";
        } else {
            $json['code'] = 0;
            $json['message'] = array("user"=>$_POST['user'], "pass"=>$_POST['pass'], "first"=>$_POST['first'], "last"=>$_POST['last'], "email"=>$_POST['email']);
        }
    }

    echo json_encode($json);

    mysqli_close($con);
}
