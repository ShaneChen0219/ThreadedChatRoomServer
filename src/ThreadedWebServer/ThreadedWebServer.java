package ThreadedWebServer;// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import MyRunnable.MyRunnable;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedWebServer {

    public static void main(String[] args){
        ServerSocket _server = null;
        Socket _client = null;
        try {
            _server = new ServerSocket(8080);
            while (true) {
                _client = _server.accept();
                MyRunnable _myRunnable = new MyRunnable(_client);
                Thread _thread = new Thread(_myRunnable);
                _thread.start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}