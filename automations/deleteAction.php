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
    if(!isset($_GET["pos"])){
        echo "error: postition unset";
        die;
    }
    $idAutomation = $_GET["idAutomation"];
    $pos = $_GET["pos"];

    include "../db_connect.php";
    
    $q = "CALL delete_action($idAutomation,$pos);";
    $risultato = mysqli_query($con, $q);
    if($risultato==false){
        echo "error: procedure failed";
    }
    else{
        $q = "SELECT actions.*, modes.modeName FROM actions INNER JOIN modes USING (modeId) WHERE idAutomation = ".$idAutomation;
        $risultato = mysqli_query($con, $q);
        $data = mysqli_fetch_all($risultato, MYSQLI_ASSOC);
        echo json_encode(array("actions"=>$data));
    }
        
?>
