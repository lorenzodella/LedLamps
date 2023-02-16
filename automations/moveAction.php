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
    if(!isset($_GET["from"])){
        echo "error: initial postition unset";
        die;
    }
    if(!isset($_GET["to"])){
        echo "error: final postition unset";
        die;
    }
    $idAutomation = $_GET["idAutomation"];
    $from = $_GET["from"];
    $to = $_GET["to"];

    include "../db_connect.php";
    
    $q = "CALL move_action($idAutomation,$from,$to);";
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


