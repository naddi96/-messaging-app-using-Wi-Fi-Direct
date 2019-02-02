package com.example.naddi.wifip2p2tesi;

import android.util.Base64;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;

public class Cript{
    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    public PublicKey pubkey;
    public PrivateKey prikey;

    public PublicKey hispub;




    public void setHisKey(String hispub) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte [] pub= Base64.decode(hispub,Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(pub);
        java.security.KeyFactory fact = java.security.KeyFactory.getInstance("EC");
        this.hispub = fact.generatePublic(spec);
        System.out.println(this.hispub.toString());
    }



    public Cript() {
        try {
            ECGenParameterSpec ecParamSpec = new ECGenParameterSpec("secp224k1");
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SC");// KeyPairGenerator.getInstance("ECDH","SC");
            kpg.initialize(ecParamSpec);
            KeyPair kpair=kpg.generateKeyPair();
            pubkey = kpair.getPublic();
            prikey = kpair.getPrivate();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }



    public  String decript(String mex){
        try{
            byte[] mexDec = Base64.decode(mex.getBytes(),0);
            Cipher ecies = Cipher.getInstance("ECIESwithAES-CBC");
            ecies.init(Cipher.DECRYPT_MODE, prikey, ecies.getParameters());
            byte[] decrypted = ecies.doFinal(mexDec);
            return  new String(decrypted,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
            return "errore";
        }

    }

    public String encript(String mex){
        try {
            Cipher ecies = Cipher.getInstance("ECIESwithAES-CBC");
            ecies.init(Cipher.ENCRYPT_MODE, hispub);
            byte[] message = mex.getBytes("UTF-8");
            byte[] ciphertext = ecies.doFinal(message);
            return  Base64.encodeToString( ciphertext,0 );
            //return new String(ciphertext, "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
            return "errore";
        }

    }



}
