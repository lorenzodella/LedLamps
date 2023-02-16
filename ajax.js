function requestHTTP(param){
    var url = param;
    
    xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", url, true);
    xmlHttp.onreadystatechange = function() {
        //alert("Server risponde con readyState="+xmlHttp.readyState+" e status="+xmlHttp.status);
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
            var text = xmlHttp.responseText;
            if(text.includes("error"))
                alert(xmlHttp.responseText);
            else
                load(xmlHttp.responseText);
        }
    }
    
    xmlHttp.send();
}

function load(data) {
    var mydata = JSON.parse(data);

    var n = document.querySelectorAll(".button");
    for (i = 0; i < n.length; i++) {
        n[i].className = (n[i].id == mydata["opMode"] ? "button current" : "button default");
    }

    var y = document.getElementById("double");
    var x = y.querySelectorAll(".button");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].style.background = "linear-gradient(to right," + mydata["fade1"] + "," + mydata["fade2"] + ")";
    }

    y = document.getElementById("44");
    x = document.getElementById("45");
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
    x.value = int;
    
} 