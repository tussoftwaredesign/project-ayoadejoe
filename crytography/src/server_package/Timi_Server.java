package server_package;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import client_package.Employee;



public class Timi_Server {
	public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(2000);
        System.out.println("Server: waiting for connection ...");
        while (true) {
            Socket s = ss.accept();
            System.out.println("Server: client connected.");
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            
            // Read DH parameters from client (format: p,g,l)
            String dhParams = (String) ois.readObject();
            System.out.println("Received DH Params: " + dhParams);
            String[] values = dhParams.split(",");
            BigInteger p = new BigInteger(values[0]);
            BigInteger g = new BigInteger(values[1]);
            int l = Integer.parseInt(values[2]);
            System.out.println("p: " + p);
            System.out.println("g: " + g);
            System.out.println("l: " + l);
            DHParameterSpec dhSpec = new DHParameterSpec(p, g, l);
            
            // Generate server DH key pair using received parameters
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            keyGen.initialize(dhSpec);
            KeyPair serverKP = keyGen.generateKeyPair();
            PrivateKey serverPrivateKey = serverKP.getPrivate();
            PublicKey serverPublicKey = serverKP.getPublic();
            // Send server public key to client
            oos.writeObject(serverPublicKey);
            
            // Read client's public key
            PublicKey clientPublicKey = (PublicKey) ois.readObject();
            
            // Generate shared secret key using Diffie–Hellman
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(serverPrivateKey);
            ka.doPhase(clientPublicKey, true);
            byte[] rawSecret = ka.generateSecret();
            // Use the first 16 bytes as the AES key
            SecretKey sharedSecret = new SecretKeySpec(rawSecret, 0, 16, "AES");
            String encodedKey = Base64.getEncoder().encodeToString(sharedSecret.getEncoded());
            System.out.println("Shared secret (Base64): " + encodedKey);
            
            // ---------------- Mutual Authentication ----------------
            // Step 1: Server authentication
            // Server generates a random challenge string and sends it (encrypted)
            String serverChallenge = Integer.toString(new Random().nextInt(100000));
            System.out.println("Server Challenge: " + serverChallenge);
            byte[] encryptedServerChallenge = Utils.encryptString(serverChallenge, sharedSecret);
            oos.writeObject(encryptedServerChallenge);
            
            // Server receives the client's response and verifies it
            byte[] encryptedServerChallengeResponse = (byte[]) ois.readObject();
            String serverChallengeResponse = Utils.decryptString(encryptedServerChallengeResponse, sharedSecret);
            if (!serverChallenge.equals(serverChallengeResponse)) {
                System.out.println("Server authentication failed.");
                s.close();
                continue;
            }
            System.out.println("Server authenticated by client.");
            
            // Step 2: Client authentication
            // Server receives the client's challenge
            byte[] encryptedClientChallenge = (byte[]) ois.readObject();
            String clientChallenge = Utils.decryptString(encryptedClientChallenge, sharedSecret);
            System.out.println("Received Client Challenge: " + clientChallenge);
            // Echo the client challenge back (encrypted)
            byte[] encryptedClientChallengeResponse = Utils.encryptString(clientChallenge, sharedSecret);
            oos.writeObject(encryptedClientChallengeResponse);
            System.out.println("Client authenticated.");
            // ---------------------------------------------------------
            
            // Receive the encrypted (sealed) Student object from the client
            SealedObject sealedObj = (SealedObject) ois.readObject();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sharedSecret);
            Employee student = (Employee) sealedObj.getObject(cipher);
            System.out.println("Received Employee object: " + student);
            
            s.close();
        }
    }
}
