
function handleEnter(e){
    if(e.code == "Enter"){
        ws.send("message "+ msg.value+" "+userName.value);
        msg.value ="";
    }
}
function handleSend(e){
    ws.send("message "+ msg.value+" "+userName.value);
    msg.value ="";
}

function handleOpenCB(e){
    wsOpen = true;
    console.log("opened")
    // writeMsg.appendChild();
}
function handleCloseCB(e){
    console.log("The connection has been closed successfully.")
    ws.send("leave "+ userName.value+" "+roomName.value);
    // writeMsg.appendChild();
}


function handleMagCB(e){
    //e.data -> get the msg from the event
    let msgFromServer = JSON.parse(e.data);

    if(msgFromServer.type == "join"){
        console.log("add"+msgFromServer.user)
        for (const u in writeUser.childNodes.item(1)) {
            console.log(u)
        }

        const node = document.createElement("li");//<li> </li>
        node.setAttribute("id",msgFromServer.user);
        const userNameAdded = document.createTextNode(msgFromServer.user);//user's name that I've typed in
        node.appendChild(userNameAdded)//<li> Name </li>
        writeUser.appendChild(node)


        const node2 = document.createElement("li");
        const userNameAddedInMsg = document.createTextNode(msgFromServer.user+" is in the room");
        node2.appendChild(userNameAddedInMsg)
        writeMsg.appendChild(node2)

    }else if (msgFromServer.type == "message"){
        const now = new Date();
        const day = now.getDay(); // returns a number representing the day of the week, starting with 0 for Sunday
        const hours = now.getHours();
        const minutes = now.getMinutes();
        const time = now+" " + day+" " + hours+" " +minutes;

        const node = document.createElement("li");
        const userNameAdded = document.createTextNode(time + "-> "+msgFromServer.user+": "+msgFromServer.message);
        node.appendChild(userNameAdded)
        writeMsg.appendChild(node)
        writeMsg.value=""
    }else if (msgFromServer.type == "leave"){
        console.log("leave");
        const node = document.createElement("li");
        const userNameAdded = document.createTextNode(msgFromServer.user+": left room "+msgFromServer.room);
        node.appendChild(userNameAdded)
        writeMsg.appendChild(node)
        let nodeRm = document.getElementById(msgFromServer.user);
        writeUser.removeChild(nodeRm);

    }
}
function handleUserAndRoom(e){
    
    if(e.code == "Enter" && wsOpen){
        let check = true;
        for(let a of roomName.value){
            if(a>'z' || a<'a'){
                check = false
                alert("error roomname")
            }
        }
        if(check){
            console.log("send :"+ userName.value + " roome"+  roomName.value)
            ws.send("join "+ userName.value+" "+roomName.value);

        }
        
    }

}
let ws;
let wsOpen = false;
const userName = document.getElementById("username");//console.log(userName.value)
const roomName = document.getElementById("roomname");
const msg = document.getElementById("msg")
const sendbut = document.getElementById("sendBut");
const writeMsg = document.getElementById("msgs");
const writeUser = document.getElementById("nameList");
const leaveBut = document.getElementById("leaveBut");
//Web Socket
ws= new WebSocket("ws://localhost:8080");
ws.onopen = handleOpenCB;
ws.onmessage = handleMagCB;
ws.onclose = handleCloseCB
userName.addEventListener("keypress",handleUserAndRoom)
roomName.addEventListener("keypress",handleUserAndRoom)
msg.addEventListener("keypress",handleEnter);
sendbut.addEventListener("click",handleSend);
leaveBut.addEventListener("click",handleCloseCB);

//gotta make sure the roomname is lower case
//get message let msgObj = JSOM.parse(msg); cuz the msg we got is a string can't use . to get the field from the structure{"":""}
//message area is using a <div>
//send message-> 1st "join username roomname" -> server remembers this 
//send message -> "message "
//event -> .target -> the gui argument=> it will let us know  ->.keycode => the word we key in => "Enter" == 13
//.preventDefault()