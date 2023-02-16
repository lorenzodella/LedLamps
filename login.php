<?php
    session_start();

    session_unset(); // libera tutte le variabili di sessione attualmente registrate.
    session_destroy(); //distruggo tutte le sessioni

    session_start();

    $error = "false";
    if(isset($_POST["username"]) && isset($_POST["password"])){
        include "db_connect.php";

        $username=mysqli_real_escape_string($con,$_POST['username']); //faccio l'escape dei caratteri dannosi
        $password=mysqli_real_escape_string($con,$_POST['password']);

        $q = "SELECT * FROM users WHERE username = '$username' AND password = '$password';";
        $risultato = mysqli_query($con, $q);
        $data = mysqli_fetch_array($risultato, MYSQLI_ASSOC);
        $idLamp = $data["idLamp"];
        if($idLamp!=null){
            $_SESSION["idLamp"] = $idLamp;
            if(isset($_GET["details"]))
                echo json_encode($data);
            else
                header('Location: index.php');
        }
        else
            $error = "true";
    }

?>

<!DOCTYPE html>
<html>
<head>
    
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Gloria+Hallelujah&display=swap" rel="stylesheet">
    <link rel="icon" href="icon.png" type="image/png"/>
    <title>LedLamps - login</title>
    <script src="https://www.myersdaily.org/joseph/javascript/md5.js"></script>
    <script type="text/javascript">
        setTimeout(function() {
            if(<?php echo $error;?>) alert("Login not valid!");
        }, 50);

        function pwd(){
            var pass = document.getElementById("password");
            pass.value = md5(pass.value);
        }
    </script>
    <style>
        body{
            background: linear-gradient(to bottom right,#00eaff,#ff00d6);
            background-repeat: no-repeat;
            background-attachment: fixed;
        }
        #login{
            position: absolute;
            top: 65%;
            transform: translateY(-50%);
            background: linear-gradient(to bottom,#fff,#aaa);
            border: none;
            border-radius: 20px;
            padding: 20px;
            box-shadow: 0 10px 10px black;
        }
        fieldset {
            border: none;
            margin: 10px;
        }
        #inputs {
            margin-bottom: 30px;
        }
        #inputs input{
            padding: 10px;
            width: 400px;
        }
        #submit {
            padding: 10px;
            border-radius: 3px;
            background: linear-gradient(to bottom,#00eaff,#4287f5);
            color: black;
            font: bold 18px Arial, Helvetica;
            text-shadow: 0 1px 0 rgba(255,255,255,0.5);
        }
        #submit:hover {
            background: linear-gradient(to top,#00eaff,#4287f5);
        }
        .center {
            display: flex;
                    align-items: center;
                    justify-content: center;
        }
        img.center {
            display: block;
            margin-left: auto;
            margin-right: auto;
            margin-top: 50px;
        }
    </style>

</head>
<body>


    <img src="icon.png" class="center" width=10%>
  
    <h1 style="font-size: 80px; font-family: Gloria Hallelujah; color: white; text-align: center; margin: 0">Login</h1>



<div class=center>
    <form id="login" method="post" onsubmit="pwd();">
        <fieldset id="inputs">
            <input id="username" name="username" type="text" placeholder="Username" autofocus required>
            <br><br>
            <input id="password" name="password" type="password" placeholder="Password" required>
        </fieldset>
        <fieldset id="actions">
            <input type="submit" id="submit" value="Login">
        </fieldset>
    </form>
</div>

    

</body>
</html>