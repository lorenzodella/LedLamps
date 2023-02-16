<?php
    session_start();

    if (!isset($_SESSION['idLamp'])) {
        if(!isset($_POST['idLamp'])) {
            echo "error: forbidden";
            die;
        }
        else {
            $idLamp = $_POST['idLamp'];
        }
    }
    else {
        $idLamp = $_SESSION['idLamp'];
    }

    include "../db_connect.php";
    
    $q = "SELECT modeId, modeName FROM modes WHERE modeId != 43";
    $risultato = mysqli_query($con, $q);
    $data = mysqli_fetch_all($risultato, MYSQLI_ASSOC);
    echo json_encode(array("modes"=>$data));
        
?>