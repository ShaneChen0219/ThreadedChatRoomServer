package HTTPResponse;

import HTTPRequest.HTTPRequest;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class HTTPResponse {

    public void sendHandShake(OutputStream outStream, HTTPRequest request) throws NoSuchAlgorithmException, IOException {

        String encodeKey = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((request._mapOfReq.get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")));
        outStream.write("HTTP/1.1 101 Switching Protocols\n".getBytes());
        outStream.write("Upgrade: websocket\n".getBytes());
        outStream.write("Connection: Upgrade\n".getBytes());
        outStream.write(("Sec-WebSocket-Accept: " + encodeKey + "\r\n\n").getBytes());
        outStream.flush();
    }
    public void errorHandleNotFound(OutputStream outStream) throws IOException {
        outStream.write("HTTP/1.1 404 Not Found ".getBytes());
        outStream.write("Server: MyHttpServer ".getBytes());
        outStream.write("Content-type: text/plain\n".getBytes());
        outStream.write("404 Not Found".getBytes());
    }

    public void imageRespond(OutputStream outputImage, String[] parts) {
        File imageFile = new File("src/HTTPResponse/" + parts[1]);

        if (imageFile.exists()) {
            InputStream imageInputStream = null;
            try {
                imageInputStream = new FileInputStream(imageFile);
                outputImage.write("HTTP/1.1 200 OK".getBytes());
                outputImage.write("Server: MyHttpServer".getBytes());
                outputImage.write("Content-type: image/jpeg\r\n".getBytes());

                //the images have to convert to bytes to make the server read it
                //Create an array that can contain 1024 bytes( the image can't be larger than 1024)
                //an integer that records the bytes that have been read
                //it will return -1 when there are any bytes
                //use the write method of outputImage to write the bytes stored in the buffer array to the outputImage stream.
                //The 0 is the starting index in the buffer array, and bytesRead is the number of bytes to write.
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = imageInputStream.read(buffer)) != -1) {
//                    outputImage.write(buffer, 0, bytesRead);
//                }
//                imageInputStream.transferTo(outputImage);
                for (int i = 0; i < imageFile.length(); i++) {
                    outputImage.write(imageInputStream.read());
                    outputImage.flush();
                    // Thread.sleep( 10 ); // Maybe add <- if images are still loading too quickly...
                }

                imageInputStream.close();
                outputImage.flush();
                outputImage.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    public void cssRespond(OutputStream outputImage) throws IOException {
        //exceptions
        File cssFile = new File("src/resources/style.css");
        if (cssFile.exists()) {
            InputStream cssInputStream = null;
            try {
                cssInputStream = new FileInputStream(cssFile);
                outputImage.write("HTTP/1.1 200 OK".getBytes());
                outputImage.write("Server: MyHttpServer".getBytes());
                outputImage.write("Content-type: text/css\n\n".getBytes());
                cssInputStream.transferTo(outputImage);
                cssInputStream.close();
                outputImage.flush();
                outputImage.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public void jsRespond(OutputStream outputImage){
        File jsFile = new File("src/resources/WebChat.js");
        if (jsFile.exists()) {
            InputStream htmlInputStream = null;
            try {
                htmlInputStream = new FileInputStream(jsFile);
                outputImage.write("HTTP/1.1 200 OK".getBytes());
                outputImage.write("Server: MyHttpServer".getBytes());
                outputImage.write("Content-type: text/javascript\n\n".getBytes());
                htmlInputStream.transferTo(outputImage);
                htmlInputStream.close();
                outputImage.flush();
                outputImage.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }}
    
    public void htmlRespond(OutputStream outputImage) {
        //exceptions
        File htmlFile = new File("src/resources/WebChat.html");

        if (htmlFile.exists()) {
            InputStream htmlInputStream = null;
            try {
                htmlInputStream = new FileInputStream(htmlFile);
                outputImage.write("HTTP/1.1 200 OK".getBytes());
                outputImage.write("Server: MyHttpServer".getBytes());
                outputImage.write("Content-type: text/html\n\n".getBytes());
                htmlInputStream.transferTo(outputImage);
                htmlInputStream.close();
                outputImage.flush();
                outputImage.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
