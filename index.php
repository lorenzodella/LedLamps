<?php
    session_start();
    include "db_connect.php";

    if (!isset($_SESSION['idLamp'])) {
      echo "<body style='background: linear-gradient(to bottom right,#00eaff,#ff00d6);";
      echo "font-family: Comic Sans MS;";
      echo "background-repeat: no-repeat;";
      echo "background-attachment: fixed;'>";
      
      echo "<h2 style='text-align: center;'>You must log in to control your lamps</h2>";
      echo "<a href=login.php style='display: blocK; text-align: center;'> GO TO LOGIN </a></body>";
      return;
    }
    $idLamp = $_SESSION['idLamp'];

    $q = "SELECT * FROM lamps WHERE idLamp='$idLamp' LOCK IN SHARE MODE";
    $risultato = mysqli_query($con, $q);
    $data = mysqli_fetch_array($risultato, MYSQLI_ASSOC);
    //var_dump($data);
    
    $brightness_int = $data["brightness"];
    $hex = dechex($brightness_int);
    $text;
    if($brightness_int<=80)
        $text = "white";
    else
        $text = "black";
    $hex = "#".$hex.$hex.$hex;

?>

<!doctype html>
<html>
  
  <head>
    <link rel="icon" href="icon.png" type="image/png"/>
    <title>LedLamps</title>
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Gloria+Hallelujah&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="styles.css"/>
    <script type="text/javascript" src="ajax.js"></script>
    <script type="text/javascript">
      function logout(){
        if(confirm("Do you want to log out?"))
          location.href = "login.php";
      }
	  window.setInterval(function() {
        requestHTTP('sync.php');
      }, 3000);
    </script>
  </head>

  <body>
    <button onclick="logout()" class="profile">
      <img src="user.png" width=30px style="margin-right: 10px;">
      <?php echo $idLamp;?>
    </button>

    <a href=index.php> <img src="icon.png" width=10% class="center" title="refresh" > </a>
  
    <h1 style="font-size:60px;">You are connected to LED LAMPS!</h1>
    <p>Choose the option mode below to control your lamps</p>
    
    <hr> <br>

    <button id="42" onclick="requestHTTP('mode.php?opMode=off')" class="button <?php echo $data['opMode']==42 ? 'current' : 'default' ?>" style="background-color:black; color:white;">
      TURN OFF
    </button> <br>

    <button id="41" onclick="requestHTTP('mode.php?opMode=on')" class="button <?php echo $data['opMode']==41 ? 'current' : 'default' ?>" style="background-color:white;">
      TURN ON
    </button> </br>

    <hr> <br>

    <button id="1" onclick="requestHTTP('mode.php?opMode=sound_reactive')" class="button <?php echo $data['opMode']==1 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#34eb59,#4034eb,#e834eb,#eb3434,#ebd034,#fff);">
      SOUND REACTIVE
    </button> </br>
    <button id="2" onclick="requestHTTP('mode.php?opMode=chill_fade')" class="button <?php echo $data['opMode']==2 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#00eaff,#ff00d6);">
      CHILL FADE
    </button> </br>
    <button id="3" onclick="requestHTTP('mode.php?opMode=color_wipe')" class="button <?php echo $data['opMode']==3 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#FF0000,#00FF00,#0000FF);">
      COLOR WIPE
    </button> </br>
    <button id="4" onclick="requestHTTP('mode.php?opMode=color_flash')" class="button <?php echo $data['opMode']==4 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#FF0000,#00FF00,#0000FF);">
      COLOR FLASH
    </button> </br>
    <button id="5" onclick="requestHTTP('mode.php?opMode=rainbow_cycle')" class="button <?php echo $data['opMode']==5 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#FF0000,#FF7F00,#FFFF00,#00FF00,#0000FF,#4B0082,#9400D3);">
      RAINBOW CYCLE
    </button> </br>
    <button id="6" onclick="requestHTTP('mode.php?opMode=rainbow_fade')" class="button <?php echo $data['opMode']==6 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#FF0000,#FF7F00,#FFFF00,#00FF00,#0000FF,#4B0082,#9400D3);">
      RAINBOW FADE
    </button> </br>
    <button id="7" onclick="requestHTTP('mode.php?opMode=rainbow_chase')" class="button <?php echo $data['opMode']==7 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#FF0000,#FF7F00,#FFFF00,#00FF00,#0000FF,#4B0082,#9400D3);">
      RAINBOW CHASE
    </button> </br>
    <button id="8" onclick="requestHTTP('mode.php?opMode=strobe')" class="button <?php echo $data['opMode']==8 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#FF0,#ff8,#fff,#ff8,#FF0,#ff8,#fff,#ff8,#FF0,#ff8,#fff,#ff8,#ff0);">
      STROBE
    </button> </br>
    <button id="9" onclick="requestHTTP('mode.php?opMode=fire')" class="button <?php echo $data['opMode']==9 ? 'current' : 'default' ?>" style="background:linear-gradient(to right,#FFFF00,#FF0000);">
      FIRE
    </button> </br>
    <button id="10" onclick="requestHTTP('mode.php?opMode=balls')" class="button <?php echo $data['opMode']==10 ? 'current' : 'default' ?>" style="background-color:#b75ffa;">
      BOUNCING BALLS
    </button> </br>
    <button id="11" onclick="requestHTTP('mode.php?opMode=fill_random')" class="button <?php echo $data['opMode']==11 ? 'current' : 'default' ?>" style="background-color:#d7ff87;">
      FILL RANDOM
    </button> </br>
    <button id="12" onclick="requestHTTP('mode.php?opMode=sound_colors')" class="button <?php echo $data['opMode']==12 ? 'current' : 'default' ?>" style="background-color:#03fcad;">
      CLAP
    </button> </br>
    <button id="13" onclick="requestHTTP('mode.php?opMode=twinkle')" class="button <?php echo $data['opMode']==13 ? 'current' : 'default' ?>" style="background-color:#ffa987;">
      TWINKLE
    </button> </br>
    <button id="14" onclick="requestHTTP('mode.php?opMode=sparkle')" class="button <?php echo $data['opMode']==14 ? 'current' : 'default' ?>" style="background-color:#b34b74;">
      SPARKLE
    </button> </br>
    <button id="15" onclick="requestHTTP('mode.php?opMode=strobe_shot')" class="button <?php echo $data['opMode']==15 ? 'current' : 'default' ?>" style="background-color:#207532;">
      STROBE SHOT
    </button> </br>
    <button id="16" onclick="requestHTTP('mode.php?opMode=strobe_fade')" class="button <?php echo $data['opMode']==16 ? 'current' : 'default' ?>" style="background-color:#8d6b0e;">
      STROBE FADE
    </button> </br>
    
    <hr> <br>

    <button id="43" onclick="requestHTTP('mode.php?opMode=red')" class="button <?php echo $data['opMode']==43 ? 'current' : 'default' ?>" style="background-color:red;">
      RED
    </button> </br>
    <button id="43" onclick="requestHTTP('mode.php?opMode=blue')" class="button <?php echo $data['opMode']==43 ? 'current' : 'default' ?>" style="background-color:blue;">
      BLUE
    </button> </br>
    <button id="43" onclick="requestHTTP('mode.php?opMode=green')" class="button <?php echo $data['opMode']==43 ? 'current' : 'default' ?>" style="background-color:green;">
      GREEN
    </button> </br>

    <hr> <br>
    
    <div id="double">
      <button id="46" onclick="requestHTTP('mode.php?opMode=fade')" class="button <?php echo $data['opMode']==46 ? 'current' : 'default' ?>" style="background: linear-gradient(to right, <?php echo $data["fade1"].",".$data["fade2"] ?>)">
        FADE COLORS
      </button> </br>
      <button id="47" onclick="requestHTTP('mode.php?opMode=double')" class="button <?php echo $data['opMode']==47 ? 'current' : 'default' ?>" style="background: linear-gradient(to right, <?php echo $data["fade1"].",".$data["fade2"] ?>)">
        DOUBLE COLOR
      </button> </br>
      <button id="48" onclick="requestHTTP('mode.php?opMode=fix')" class="button <?php echo $data['opMode']==48 ? 'current' : 'default' ?>" style="background: linear-gradient(to right, <?php echo $data["fade1"].",".$data["fade2"] ?>)">
        FIX COLORS
      </button> </br>
      <button id="49" onclick="requestHTTP('mode.php?opMode=swapping')" class="button <?php echo $data['opMode']==49 ? 'current' : 'default' ?>" style="background: linear-gradient(to right, <?php echo $data["fade1"].",".$data["fade2"] ?>)">
        SWAPPING COLORS
      </button> </br>

      <form action="doubleColorPicker.php" method="post" class="center">
        <input type="hidden" name="first_now" value="<?php echo $data['fade1'] ?>">
        <input type="hidden" name="second_now" value="<?php echo $data['fade2'] ?>">
        <img onclick="submit()" src="color_picker.jpg" class="icon"> <p style="font-size: 30px;">Choose colors</p>
    </form>
      
    </div>
    
    <hr> <br>

    <button id="44" onclick="requestHTTP('mode.php?opMode=custom_color')" class="button <?php echo $data['opMode']==44 ? 'current' : 'default' ?>" style="background: <?php echo $data["custom"] ?>">
      COLOR
    </button> </br>
    <button id="45" onclick="requestHTTP('mode.php?opMode=custom_fade')" class="button <?php echo $data['opMode']==45 ? 'current' : 'default' ?>" style="background: linear-gradient(to right, <?php echo $data["custom"]?>, white)">
      COLOR FADE
    </button> </br>

    <form action="colorPicker.php" method="post" class="center">
      <input type="hidden" name="color_now" value="<?php echo $data['custom'] ?>">
      <img onclick="submit()" src="color_picker.jpg" class="icon"> <p style="font-size: 30px;">Choose color</p>
    </form>

    <hr> <br>

    <form action="brightnessPicker.php" method="post" class="center">
      <button onclick="submit()" class="button default" id="brightness" name="brightness_now" value="<?php echo $brightness_int ?>" style="background: <?php echo $hex ?>; color: <?php echo $text?>;">
        SET BRIGHTNESS
      </button>
    </form>
    
    <hr> <br>

    <marquee>&copy Lorenzo Della Matera - all rights reserved</marquee>
  </body>
</html>