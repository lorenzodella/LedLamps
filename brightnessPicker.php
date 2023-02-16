<?php
    session_start();

    if (!isset($_SESSION['idLamp'])) {
      echo "<h1>Area riservata, accesso negato.</h1>";
      die;
    }
    $idLamp = $_SESSION['idLamp'];
?>

<!DOCTYPE html>
<html>
<head>
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Gloria+Hallelujah&display=swap" rel="stylesheet">
    <link rel="icon" href="icon.png" type="image/png"/>
    <title>LedLamps - picker</title>

    <style>
        body {
            background: linear-gradient(to bottom right,#00eaff,#ff00d6);
            font-family: Gloria Hallelujah;
            height: 100%;
            margin: 0;
            background-repeat: no-repeat;
            background-attachment: fixed;
        }

        input[type=range] {
            -webkit-appearance: none;
            width: 500px;
        }
        input[type=range]::-webkit-slider-runnable-track {
            width: 500px;
            height: 8px;
            background: #ddd;
            border-radius: 3px;
        }
        input[type=range]::-webkit-slider-thumb {
            -webkit-appearance: none;
            height: 40px;
            width: 20px;
            border: 2px solid #000;
            border-radius: 6px;
            background: #fff;
            margin-top: -16px;
        }
        input[type=range]:focus {
            outline: none;
        }
        .button {
            font-family: Comic Sans MS;
            -webkit-user-select: none;  
            -moz-user-select: none;    
            -ms-user-select: none;      
            user-select: none;
            
            width: 20%;  
            display: block;

            margin-top: 10px;
            margin-bottom: 10px;
            margin-left: auto;
            margin-right: auto;
            border-radius: 25px;
            border: solid black;
            color: black;
            background-color: whitesmoke;
            outline: none;

            text-align: center;
            text-decoration: none;
            font-size: 200%;
            font-weight: bold;
            padding: 20px;

            box-shadow: 0 9px #999;
            transition: width 500ms, font-size 500ms;
        }
        .button:hover {
            width: 30%;
            font-size: 300%;
        }
        .button:active {
            box-shadow: 0 5px #666;
            transform: translateY(4px);
        }
    </style>

    <script type="text/javascript">
        function requestHTTP(){
            //var url = "http://ledlampsweb.hostinggratis.it"+param;
            var url = "set_brightness_hash.php?brightness="+encodeURIComponent(document.getElementById('brightness').value);
            //location.href = url;
            
            xmlHttp = new XMLHttpRequest();
            xmlHttp.open("GET", url, true);
            xmlHttp.onreadystatechange = function() {
                //alert("Server risponde con readyState="+xmlHttp.readyState+" e status="+xmlHttp.status);
                if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                    var text = xmlHttp.responseText;
                    if(text.includes("error"))
                        alert(xmlHttp.responseText);
                    else
                        location.href = "index.php";
                }
            }
            
            xmlHttp.send();
        }
    </script>
</head>

<body>
    <p style="text-align: center;">
        <strong style="font-size:400%">Drag the cursor to set the brightness</strong>
    </p><br><br>
    <div style="text-align: center;">
        <input type="range" id="brightness" name="brightness" min="0" max="255" value="<?php echo isset($_POST["brightness_now"]) ? $_POST["brightness_now"] : "0" ?>">
        <br><br>
        <button class="button" onclick="requestHTTP()">Send</button>
    </div>
    <br><br>

    <a class="button" style="width: 25%;" id="homeBtn" href="index.php">Main Menu</a>
    <br><br>
</body>

</html>