package com.example.naddi.wifip2p2tesi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass extends Thread{
    Socket socket;
    ServerSocket serverSocket;
    SendReceive sendReceive;



    public ServerClass(SendReceive sendReceive) {
        this.sendReceive = sendReceive;

    }

    @Override
    public void run(){
        try {
            serverSocket= new ServerSocket(8888);
            socket = serverSocket.accept();
            sendReceive.setSocket(socket);
            if (sendReceive.getState() == Thread.State.NEW){
                sendReceive.start();
            };
        }catch (IOException e){
            e.printStackTrace();
        }}
}