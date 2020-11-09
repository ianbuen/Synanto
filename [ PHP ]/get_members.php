<?php

require_once('db_connect.php');

//$_SERVER['REQUEST_METHOD'] = 'GET';
//$_GET['eventID'] = 17;

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    $eventID = $_GET['eventID'];

    $sql = "SELECT CONCAT(tbl_accounts.lastname, ', ', tbl_accounts.firstname) AS 'name'
            FROM tbl_accounts INNER JOIN tbl_participants ON tbl_accounts.userID = tbl_participants.userID
            WHERE tbl_participants.eventID = $eventID";

    $r = mysqli_query($con, $sql);

    $names = array();

    if ($r->num_rows > 0) {

        while ($row = $r->fetch_array()) {
            $names[] = $row['name'];
        }
    }

    $string = "";
    for ($i = 0; $i < sizeof($names); $i++) {
        $string = $string . $names[$i] . "\n";
    }

    echo json_encode($names);

    mysqli_close($con);
}