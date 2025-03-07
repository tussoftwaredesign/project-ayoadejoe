package client_package;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;




public class TimiClient {

	 public static void main(String[] args) throws Exception {
		Socket s = new Socket("localhost", 2000);
        System.out.println("Client: connected to server.");
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        
        // Generate DH key pair with default parameters
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
        keyGen.initialize(2048);
        KeyPair clientKP = keyGen.generateKeyPair();
        PrivateKey clientPrivateKey = clientKP.getPrivate();
        PublicKey clientPublicKey = clientKP.getPublic();
        
        // Extract DH parameters from the public key and send them as a string (p,g,l)
        DHPublicKey dhPubKey = (DHPublicKey) clientPublicKey;
        DHParameterSpec dhSpec = dhPubKey.getParams();
        String dhParams = dhSpec.getP().toString() + "," + dhSpec.getG().toString() + "," + dhSpec.getL();
        oos.writeObject(dhParams);
        System.out.println("Sent DH parameters: " + dhParams);
        
        // Read server's public key
        PublicKey serverPublicKey = (PublicKey) ois.readObject();
        
        // Send client's public key
        oos.writeObject(clientPublicKey);
        
        // Generate shared secret key
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(clientPrivateKey);
        ka.doPhase(serverPublicKey, true);
        byte[] rawSecret = ka.generateSecret();
        // Use first 16 bytes for AES key
        SecretKey sharedSecret = new SecretKeySpec(rawSecret, 0, 16, "AES");
        String encodedKey = Base64.getEncoder().encodeToString(sharedSecret.getEncoded());
        System.out.println("Shared secret (Base64): " + encodedKey);
        
        // ---------------- Mutual Authentication ----------------
        // Step 1: Client receives the server's challenge and echoes it back.
        byte[] encryptedServerChallenge = (byte[]) ois.readObject();
        String serverChallenge = Utils.decryptString(encryptedServerChallenge, sharedSecret);
        System.out.println("Received Server Challenge: " + serverChallenge);
        // Echo back the challenge
        byte[] encryptedServerChallengeResponse = Utils.encryptString(serverChallenge, sharedSecret);
        oos.writeObject(encryptedServerChallengeResponse);
        
        // Step 2: Client authentication - send a challenge to the server.
        String clientChallenge = Integer.toString(new Random().nextInt(100000));
        System.out.println("Client Challenge: " + clientChallenge);
        byte[] encryptedClientChallenge = Utils.encryptString(clientChallenge, sharedSecret);
        oos.writeObject(encryptedClientChallenge);
        // Receive server's response to the client challenge.
        byte[] encryptedClientChallengeResponse = (byte[]) ois.readObject();
        String clientChallengeResponse = Utils.decryptString(encryptedClientChallengeResponse, sharedSecret);
        if (!clientChallenge.equals(clientChallengeResponse)) {
            System.out.println("Client authentication failed.");
            s.close();
            return;
        }
        System.out.println("Client authenticated by server.");
        // ---------------------------------------------------------
        
        // Create a Student object (extra class) with two String attributes and one int attribute.
        Employee student = new Employee("Timi", "Ajax", 27);
        System.out.println("Created Student object: " + student);
        
        // Encrypt the Student object using a SealedObject and the shared secret key.
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
        SealedObject sealedStudent = new SealedObject(student, cipher);
        
        // Send the encrypted (sealed) Student object to the server.
        oos.writeObject(sealedStudent);
        System.out.println("Sent encrypted Student object.");
        
        s.close();
	}

}
