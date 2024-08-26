package HTTPRequest;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    public static Map<String, ArrayList<Socket>> _room = new HashMap<>();
    public static Map<String,ArrayList<String>> _roomWithName = new HashMap<>();
    public ArrayList<Socket> getAllClient(String roomName){
        ArrayList<Socket> allSockets = new ArrayList<>();
        allSockets = _room.get(roomName);
        return allSockets;
    }
    public Room(){
//        _room = new HashMap<>();
    }
    public synchronized void addRoom(String roomName, Socket user,String userName){
        ArrayList<Socket> userSocket = new ArrayList<>();
        ArrayList<String> userNames = new ArrayList<>();
        userSocket.add(user);
        _room.put(roomName,userSocket);
        userNames.add(userName);
        _roomWithName.put(roomName,userNames);
    }
    public synchronized void addUser(String roomName,Socket user,String userName){
        ArrayList<Socket> add = new ArrayList<>();
        ArrayList<String> addName = new ArrayList<>();
        add = _room.get(roomName);
        add.add(user);
        _room.put(roomName,add);
        addName = _roomWithName.get(roomName);
        addName.add((userName));
        _roomWithName.put(roomName,addName);
    }

    public synchronized void removeUser(String roomName,Socket userSocket,String userName){
        ArrayList<Socket> remove = new ArrayList<>();
        remove = _room.get(roomName);
        System.out.println(remove);
        remove.remove(userSocket);
        System.out.println(remove);
        _room.put(roomName,remove);
        if (_room.get(roomName).isEmpty()){
            System.out.println("enpty");
            _room.remove(roomName);
        }
        ArrayList<String> removeName = new ArrayList<>();
        removeName= _roomWithName.get(roomName);
        removeName.remove(userName);
        _roomWithName.put(roomName,removeName);

    }
    public static boolean getRoom(String name) {
        if (_room.containsKey(name)){
            return true;
        }else {
            return false;
        }
    }

}

