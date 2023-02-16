function load() {
    var mydata = JSON.parse(data);

    var y = document.getElementById("double");
    var x = y.querySelectorAll(".button");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].style.background = "linear-gradient(to right," + mydata["fade1"] + "," + mydata["fade2"] + ")";
    }

    y = document.getElementById("color");
    x = document.getElementById("fade");
    y.style.background = mydata["custom"];
    x.style.background = "linear-gradient(to right," + mydata["custom"] + ", white)";

    var int = mydata["brightness"];
    var hex = (+int).toString(16);
    var text;
    if(int<=80)
        text = "white";
    else
        text = "black";
    x = document.getElementById("brightness");
    x.style.background = "#"+hex+hex+hex;
    x.style.color = text;
    
} 