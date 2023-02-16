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

    include "db_connect.php";

    if(isset($_GET["details"])){
        $q = "SELECT lamps.*, modes.modeName, modes.modeDesc FROM lamps INNER JOIN modes ON opMode=modeId WHERE idLamp='$idLamp' LOCK IN SHARE MODE";
    }
    else{
        $q = "SELECT * FROM lamps WHERE idLamp='$idLamp' LOCK IN SHARE MODE";
    }
    $risultato = mysqli_query($con, $q);
    $data = mysqli_fetch_array($risultato, MYSQLI_ASSOC);
    echo json_encode($data);
?>