package MyRunnable;

import HTTPRequest.HTTPRequest;
import HTTPRequest.Room;
import HTTPResponse.HTTPResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MyRunnable implements Runnable {
    Socket _client;

    public MyRunnable(Socket client) {
        _client = client;
    }

    @Override
    public void run() {

        HTTPResponse response = new HTTPResponse();
        HTTPRequest request = null;
        Room room = new Room();
        try {
            request = new HTTPRequest(_client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(request._method);
        if (request._parts[0].equals("GET")) {
            OutputStream outputImage = null;
            try {
                outputImage = _client.getOutputStream();
                if (request._mapOfReq.containsKey("Sec-WebSocket-Key")) {
                    response.sendHandShake(outputImage, request);
                    byte firstByte;
                    byte secondByte;
                    while (true) {
                        // TODO handle messages
                        firstByte = request._input.readByte();
                        secondByte = request._input.readByte();
                        int opcode = firstByte & 0x0f;
                        boolean masked = (secondByte & 0x80) != 0;
                        int payloadLength = (secondByte & 0x7f);
                        if (payloadLength == 126) {
                            payloadLength = request._input.readUnsignedShort();
                        } else if (payloadLength == 127) {
                            payloadLength = (int) request._input.readLong();
                        }

                        byte[] maskKey = null;
                        if (masked) {
                            maskKey = new byte[4];
                            request._input.readFully(maskKey);
                        }

                        byte[] payLoad = new byte[payloadLength];
                        request._input.readFully(payLoad);

                        if (masked) {
                            for (int i = 0; i < payloadLength; i++) {
                                payLoad[i] = (byte) (payLoad[i] ^ maskKey[i % 4]);
                            }
                        }
                        String textMessage = new String(payLoad, "UTF-8");
                        String[] joinSplit = textMessage.split(" ");
                        if (joinSplit[0].equals("join")) {

                            request._userName = joinSplit[1];
                            request._roomName = joinSplit[2];
                            //Handle the Room
                            boolean roomExist = Room.getRoom(request._roomName);
                            if (roomExist) {
                                room.addUser(request._roomName, _client, request._userName);
                            } else {
                                room.addRoom(request._roomName, _client, request._userName);
                            }

                            String joinSendBack = "{\"type\":\"" + joinSplit[0] + "\",\"user\":\"" + joinSplit[1] + "\"}";
                            ArrayList<Socket> a = room.getAllClient(request._roomName);
                            for (Socket client : a) {
                                DataOutputStream output1 = new DataOutputStream(client.getOutputStream());
                                output1.writeByte(0x81); // Indicates a text frame
                                output1.writeByte(joinSendBack.length());
                                output1.writeBytes(joinSendBack);
                                output1.flush();
                            }

                            for (String userNameForJoin : Room._roomWithName.get(request._roomName)) {
                                if (userNameForJoin.equals(request._userName)) {
                                    continue;
                                }
                                String joinSendBackBefore = "{\"type\":\"" + joinSplit[0] + "\",\"user\":\"" + userNameForJoin + "\"}";
                                DataOutputStream output1 = new DataOutputStream(_client.getOutputStream());
                                output1.writeByte(0x81); // Indicates a text frame
                                output1.writeByte(joinSendBackBefore.length());
                                output1.writeBytes(joinSendBackBefore);
                                output1.flush();
                            }
                        } else if (joinSplit[0].equals("message")) {
                            System.out.println(textMessage);
                            if(Room._roomWithName.get(request._roomName).contains(request._userName)) {
                                System.out.println(joinSplit.length);

                                String message;
                                if (joinSplit.length == 1) {
                                    message = "";
                                } else {
                                    message = joinSplit[1];
                                }

                                for (int i = 2; i < joinSplit.length-1; i++) {
                                    message += " "+joinSplit[i];
                                }
                                String joinSendback = "{\"type\":\"" + joinSplit[0] + "\",\"user\":\"" + request._userName + "\",\"message\":\"" + message + "\"}";
                                ArrayList<Socket> a = room.getAllClient(request._roomName);
                                for (Socket client : a) {
                                    System.out.println(a);
                                    DataOutputStream output2 = new DataOutputStream(client.getOutputStream());
                                    output2.writeByte(0x81); // Indicates a text frame
                                    output2.writeByte(joinSendback.length());
                                    output2.writeBytes(joinSendback);
                                    output2.flush();
                                }
                            }
                        }else if (joinSplit[0].equals("leave")){
                            System.out.println(_client);
                            System.out.println(request._userName);
                            room.removeUser(joinSplit[2],_client,request._userName);
                            String joinSendback = "{\"type\":\"" + joinSplit[0] + "\",\"user\":\"" + joinSplit[1] + "\",\"room\":\"" + joinSplit[2] + "\"}";
                            ArrayList<Socket> c = room.getAllClient(request._roomName);
                            request._roomName = null;
                            for (Socket client : c) {
                                DataOutputStream output2 = new DataOutputStream(client.getOutputStream());
                                output2.writeByte(0x81); // Indicates a text frame
                                output2.writeByte(joinSendback.length());
                                output2.writeBytes(joinSendback);
                                output2.flush();
                            }
                        }

                    }

                } else if (request._parts[1].equals("/") || request._parts[1].equals("/WebChat.html")) {
                    response.htmlRespond(outputImage);
                    //endsWith method-> to check it with the
                } else if (request._parts[1].equals("/style.css")) {
                    response.cssRespond(outputImage);
                } else if (request._parts[1].substring(0, 6).equals("/image")) {
                    response.imageRespond(outputImage, request._parts);
                } else if (request._parts[1].equals("/WebChat.js")) {
                    response.jsRespond(outputImage);
                } else {
                    response.errorHandleNotFound(outputImage);
                }

            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        //Closing
//            request._input.close();
//            try {
//                _client.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
    }
}
