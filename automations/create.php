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
    
    if(!isset($_GET["idUser"])){
        echo "error: idUser unset";
        die;
    }
    if(!isset($_GET["name"])){
        echo "error: name unset";
        die;
    }
    if(!isset($_GET["modeId"])){
        echo "error: modeId unset";
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
    $idUser = $_GET["idUser"];
    $name = $_GET["name"];
    $modeId = $_GET["modeId"];
    $custom = $_GET["custom"];
    $fade1 = $_GET["fade1"];
    $fade2 = $_GET["fade2"];
    $time = $_GET["time"];
    
    include "../db_connect.php";
    
    $q = "CALL create_automation($idUser, '$name', $modeId, 1, '$custom', '$fade1', '$fade2', $time);";
	$ris = mysqli_query($con, $q);
    if(!$ris)
        echo "error: creation failed (maybe you already have an automation called \"$name\")";
    else{
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
    }
?>