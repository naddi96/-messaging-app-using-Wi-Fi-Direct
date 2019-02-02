package com.example.naddi.wifip2p2tesi;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.spongycastle.bcpg.SymmetricEncDataPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int backView = R.layout.activity_main;
    Database myDb;
    Button btnOnOff, btnDiscover, btnSend,btnCripto, btnConver;
    ListView  listView, conversList;
    TextView read_msg_box, connectionsStatus;
    EditText writeMsg;
    WifiManager wifimanager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChanel;
    WifiDirectBroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;

    WifiP2pDevice[] deviceArray;
    static  final int MESSAGE_READ =1;



    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;


    Button btnChatConnect;
    ListView chatMex;
    EditText mexText;
    ListView chatList;

    Cript cript;


    String currentMacConnect;
    String currentNameConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cript = new Cript();
        super.onCreate(savedInstanceState);
        backView = R.layout.activity_main;
        setContentView(R.layout.activity_main);

        myDb = new Database(this);
        sendReceive = new SendReceive(MESSAGE_READ,handler);
        initialWork();
        exqListener();
    }


    //-------------------------handler del messaggio ricevuto----------------------------------------
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String (readBuff,0,msg.arg1);
                    WifiP2pConfig config = new WifiP2pConfig();
                    if(tempMsg.length()>16 &&tempMsg.substring(0,4).equals("MAC:") ){
                        currentMacConnect = tempMsg.substring(4,21);
                        currentNameConnect = tempMsg.substring(7,tempMsg.lastIndexOf(">")-1);
                        try {
                            cript.setHisKey(tempMsg.substring( tempMsg.lastIndexOf(">")+1));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }

                        System.out.println(currentMacConnect);
                        setContentView(R.layout.chat);
                        chatWork();
                        try{
                            updateChatMex();
                        }catch (Exception e){}

                    }else{
                        String mex =cript.decript(tempMsg);
                        myDb.insertMessage(currentMacConnect,mex);
                        updateChatMex();
                        Toast.makeText(getApplicationContext(),tempMsg,Toast.LENGTH_SHORT).show();

                    }

                    Toast.makeText(getApplicationContext(),tempMsg,Toast.LENGTH_SHORT).show();
                   // myDb.insertMessage();
                    break;
            }
            return true;
        }
    });

    private void exqListener() {
        btnOnOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wifimanager.isWifiEnabled()){
                    wifimanager.setWifiEnabled(false);
                    btnOnOff.setText("WIFI ACCESSO");
                }else{
                    wifimanager.setWifiEnabled(true);
                    btnOnOff.setText("WIFI SPENTO");
                }
            }
        });
        btnDiscover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChanel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionsStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int i) {
                        connectionsStatus.setText("Discovery start fail");
                    }
                });
            }
        });


        //-----------------------------START CONNECTION----------------------------------------------------
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mManager.connect(mChanel,config,new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"connected to"+device.deviceName+"  mac"+device.deviceAddress,Toast.LENGTH_SHORT).show();
                        currentMacConnect = device.deviceAddress;
                        currentNameConnect = device.deviceName;
                    }
                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(),"not connected",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //--------------------invio------------------------------------
/*
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = writeMsg.getText().toString();
               // Toast.makeText(getApplicationContext(),msg.getBytes().toString(),Toast.LENGTH_SHORT).show();
                try{
                    sendReceive.write(msg.getBytes());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"invio fallito",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
*/
        btnCripto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Cript x = new Cript();
                //System.out.println(x.prikey);
                //System.out.println(x.pubkey);
            }
        });



        btnConver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.conversazioni);
                conversList = findViewById(R.id.convList);
                conversList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Cursor peers = myDb.getPeers();
                        int x=0;
                        while(peers.moveToNext()){
                            if (i == x){
                                currentMacConnect = peers.getString(peers.getColumnIndex("mac"));
                                break;
                            }
                        }
                        setContentView(R.layout.chat);
                        System.out.println(currentMacConnect);
                        chatWork();
                        updateChatMex();

                    }
                });

                Cursor peers = myDb.getPeers();
                String[] list = new String[peers.getCount()];
                int i =0;
                //final String mac;
                String name;

                while(peers.moveToNext()){
                    final String mac = peers.getString(peers.getColumnIndex("mac"));
                    name= peers.getString(peers.getColumnIndex("name"));
                    System.out.println(mac+" "+ name);
                    list[i] = mac+" "+ name;
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String> (getApplicationContext(), (android.R.layout.simple_list_item_1),list) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                conversList.setAdapter(adapter);

            }
        });
    }




    private void initialWork() {
        btnConver = findViewById(R.id.conversazioni);
        btnCripto = findViewById(R.id.cripto);
        btnOnOff = findViewById(R.id.onOff);
        btnDiscover = findViewById(R.id.discover);
     //   btnSend = findViewById(R.id.sendButton);
        listView =  findViewById(R.id.peerListView);

     //   read_msg_box =  findViewById(R.id.readMsg);
        connectionsStatus= findViewById(R.id.connectionStatus);
       // writeMsg = findViewById(R.id.writeMsg);
        wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChanel = mManager.initialize(this,getMainLooper(),null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager,mChanel,this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }




    private void chatWork(){
        btnChatConnect = findViewById(R.id.reconnectionButton);
        chatMex = findViewById(R.id.chatList);
        mexText = findViewById(R.id.mexText);
        btnSend = findViewById(R.id.sendChat);
        chatList = findViewById(R.id.chatList);

        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mexText.getText().toString();
                // Toast.makeText(getApplicationContext(),msg.getBytes().toString(),Toast.LENGTH_SHORT).show();
                try{


                    String msgcript = cript.encript(msg);
                    sendReceive.write(msgcript.getBytes());
                    mexText.setText("");
                    myDb.insertMessage(currentMacConnect,"me: "+msg);
                    updateChatMex();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"invio fallito",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        }











    WifiP2pManager.PeerListListener peerListListener= new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                deviceNameArray= new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for(WifiP2pDevice device : peerList.getDeviceList()){
                    deviceNameArray[index]= device.deviceAddress+"  "+device.deviceName;
                    deviceArray[index]=device;

                    myDb.insertPeers(device.deviceAddress,device.deviceName);
                    index++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String> (getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                listView.setAdapter(adapter);
            }
            if (peers.size()==0){
                Toast.makeText(getApplicationContext(),"no device found",Toast.LENGTH_SHORT).show();
            }

        }
    };





    //---------------------------------------LISTENER DELLA CONNESSIONE----------------------------------------------------------
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
         @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

            final InetAddress groupOwnwerAndres =wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){

                connectionsStatus.setText("host");
                //sendReceive = new SendReceive(MESSAGE_READ,handler);
                serverClass=new ServerClass(sendReceive);

                serverClass.start();
                Toast.makeText(getApplicationContext(),"host",Toast.LENGTH_SHORT).show();
                // serverClass.run();


            }else if (wifiP2pInfo.groupFormed){
               // sendReceive = new SendReceive(MESSAGE_READ,handler);

                connectionsStatus.setText("client");

                clientClass = new ClientClass(groupOwnwerAndres,sendReceive);
                clientClass.start();
                Toast.makeText(getApplicationContext(),"client",Toast.LENGTH_SHORT).show();
            }

             Handler handler = new Handler();


             handler.postDelayed(new Runnable() {
                 public void run() {
                     try{

                         String myDn = mReceiver.myDevice.deviceName;
                         String myDm = mReceiver.myDevice.deviceAddress;
                         String snd = "MAC:"+myDm+" "+myDn+" >"+ Base64.encodeToString(cript.pubkey.getEncoded(),0);
                         sendReceive.write(snd.getBytes());
                     }catch (Exception e){
                         Toast.makeText(getApplicationContext(),"invio fallito",Toast.LENGTH_SHORT).show();
                         e.printStackTrace();
                     }
                 }
             }, 5000);

           // setContentView(R.layout.chat);
          //  chatWork();
         }
    };



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    private void updateChatMex(){
        Cursor mexs = myDb.getMessages(currentMacConnect);
        String[] list = new String[mexs.getCount()];

        int i = 0;
        while(mexs.moveToNext()){
            final String mex = mexs.getString(mexs.getColumnIndex("mex"));
            list[i]= mex;
            i++;

        }
        final Myadapter adapter = new Myadapter(this,list);
        chatList.setAdapter(adapter);
        chatList.setTranscriptMode(chatList.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatList.setSelection(adapter.getCount() - 1);
            }
        });

    }

}