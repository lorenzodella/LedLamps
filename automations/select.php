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
    
    $obj = array("automations"=>array());

    $q = "SELECT automations.*, users.username FROM automations INNER JOIN users USING (idUser) WHERE idLamp = '$idLamp'";
    $risultato = mysqli_query($con, $q);
    $data = mysqli_fetch_all($risultato, MYSQLI_ASSOC);
    foreach($data as $automation){
        $q = "SELECT actions.*, modes.modeName FROM actions INNER JOIN modes USING (modeId) WHERE idAutomation = ".$automation["idAutomation"];
        $risultato = mysqli_query($con, $q);
        $autom_arr = array();
        $autom_arr += ["idAutomation"=>$automation["idAutomation"], "idUser"=>$automation["idUser"], "name"=>$automation["name"], "username"=>$automation["username"], "isActive"=>$automation["isActive"]];
        /*var_dump(mysqli_fetch_all($risultato, MYSQLI_ASSOC));*/
        $autom_arr += ["actions"=>mysqli_fetch_all($risultato, MYSQLI_ASSOC)];
        array_push($obj["automations"], $autom_arr);
    }
    echo json_encode($obj);
?>