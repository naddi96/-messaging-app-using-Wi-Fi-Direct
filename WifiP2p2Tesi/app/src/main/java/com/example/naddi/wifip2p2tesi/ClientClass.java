package com.example.naddi.wifip2p2tesi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;
        SendReceive sendReceive;

        public ClientClass(InetAddress hostAddress,SendReceive sendReceive){
            this.sendReceive = sendReceive;
            hostAdd = hostAddress.getHostAddress();
            socket= new Socket();
        }

        @Override
        public void run() {
            try{
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                sendReceive.setSocket(socket);
                if (sendReceive.getState() == Thread.State.NEW){
                    sendReceive.start();
                };
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

