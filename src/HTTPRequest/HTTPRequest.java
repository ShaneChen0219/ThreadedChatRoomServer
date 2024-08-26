package HTTPRequest;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class HTTPRequest {
    public String _method;
    public DataInputStream _input;
    public Map<String,String> _mapOfReq;
    public String[] _parts;
    public String _userName;
    public String _roomName;

    public HTTPRequest(Socket client) throws IOException {

        _mapOfReq = new HashMap<>();
        int i =0;
        try {
            _input = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        _method = _input.readLine();
        _parts = _method.split("\\s+");
        String[] keyAndVal;
        while (true){
            _method = _input.readLine();
            if(_method.isEmpty()){
                break;
            }
            keyAndVal = _method.split(": ");
             if (keyAndVal[0].equals("Sec-WebSocket-Key")) {
                _mapOfReq.put(keyAndVal[0],keyAndVal[1]);
            }
        }
    }
}
