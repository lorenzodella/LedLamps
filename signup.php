<?php
	include "db_connect.php";
    
    mysqli_set_charset($con, "utf8");
    
    $idLamp = $_POST["idLamp"];
	
	$username = $_POST["username"];
    $q = "SELECT username
          FROM users
          WHERE username = '$username'";
    $ris = mysqli_fetch_array(mysqli_query($con, $q), MYSQLI_ASSOC);
    if($ris != NULL){
    	if($ris["username"] == $username)
        	echo "error: username already taken";
    }
    else {
        $name = $_POST["name"];
        $surname = $_POST["surname"];
    	$password = $_POST["password"];
    	$mail = $_POST["mail"];
    	$q = "INSERT INTO users (username, password, name, surname, mail, idLamp) VALUES ('$username', '$password', '$name', '$surname', '$mail', '$idLamp');";
	    $ris = mysqli_query($con, $q);
        if(!$ris){
        	echo "error: idLamp does not exist";
        }
        else {
            var_dump($ris);
        }
    }

?>