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
    if (!isset($_GET['command'])) {
        echo "error: command unset";
        die;
    }

    $url = str_ireplace(".php", "", $_SERVER['REQUEST_URI']);
    $url = substr($url, strpos($url, "/", 1));
    //$split = explode("/",$url);
    //$url = "/".$split[3];

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, "http://lorenzodellamatera.northeurope.cloudapp.azure.com:8000/".$idLamp.$url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    $output = curl_exec($ch);
    //echo $output;
    curl_close($ch);

    if(strpos($output, "client not connected!")){
        echo "error: ".str_ireplace("LedLamps", "", strip_tags($output));
        die();
    }

    echo "success";
?>