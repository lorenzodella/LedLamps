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
    
    if(!isset($_GET["idAutomation"])){
        echo "error: idAutomation unset";
        die;
    }
    if(!isset($_GET["modeId"])){
        echo "error: modeId unset";
        die;
    }
    if(!isset($_GET["position"])){
        echo "error: postition unset";
        die;
    }
    if(!isset($_GET["custom"])){
        echo "error: custom unset";
        die;
    }
    if(!isset($_GET["fade1"])){
        echo "error: fade1 unset";
        die;
    }
    if(!isset($_GET["fade2"])){
        echo "error: fade2 unset";
        die;
    }
    if(!isset($_GET["time"])){
        echo "error: time unset";
        die;
    }
    $idAutomation = $_GET["idAutomation"];
    $modeId = $_GET["modeId"];
    $position = $_GET["position"];
    $custom = $_GET["custom"];
    $fade1 = $_GET["fade1"];
    $fade2 = $_GET["fade2"];
    $time = $_GET["time"];
    
    include "../db_connect.php";
    
    $q = "INSERT INTO actions VALUES ($idAutomation, $modeId, $position, '$custom', '$fade1', '$fade2', $time)";
	$ris = mysqli_query($con, $q);
    if(!$ris)
        echo "error: not inserted";
    else{
        $q = "SELECT actions.*, modes.modeName FROM actions INNER JOIN modes USING (modeId) WHERE idAutomation = ".$idAutomation;
        $risultato = mysqli_query($con, $q);
        $data = mysqli_fetch_all($risultato, MYSQLI_ASSOC);
        echo json_encode(array("actions"=>$data));
    }
?>